package com.oyetech.kmpfeatures.diary

import com.oyetech.kmpdomain.error.HttpStatusException
import com.oyetech.kmpdomain.usecase.navigation.NavigationUseCase
import com.oyetech.kmpfeatures.auth.GoogleLoginOperation
import com.oyetech.kmpfeatures.operator.BaseFeatureOperator
import com.oyetech.kmpmodels.entity.AreaEntry
import com.oyetech.kmpmodels.postbody.DiaryBulkPostBody
import com.oyetech.kmpmodels.stringKeys.StringKeys
import com.oyetech.kmpmodels.ui.event.DiaryBulkAction
import com.oyetech.kmpmodels.ui.state.DiaryBulkPreviewItem
import com.oyetech.kmpmodels.ui.state.DiaryBulkTagGroup
import com.oyetech.kmpmodels.ui.state.DiaryBulkUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlin.time.Instant

class DiaryBulkOperator(
    operatorScope: CoroutineScope,
    private val navigationUseCase: NavigationUseCase,
    private val diaryEndpointOperation: DiaryEndpointOperation,
    private val googleLoginOperation: GoogleLoginOperation,
) : BaseFeatureOperator<DiaryBulkUiState, DiaryBulkAction, Nothing>(
    initialState = DiaryBulkUiState(),
    operatorScope = operatorScope,
) {
    private var tagsJob: Job? = null
    private var previewedRequest: Pair<String, DiaryBulkPostBody>? = null

    init {
        launch {
            var tagsUserId: String? = null
            googleLoginOperation.session.collect { session ->
                val canAccess = hasAccess()
                val userId = session.userId.takeIf { canAccess }
                if (previewedRequest?.first != userId) previewedRequest = null
                updateState {
                    copy(
                        canPreview = canAccess && json.isNotBlank() && !isLoading && resultItems.isEmpty(),
                        canSubmit = previewedRequest != null && !isLoading,
                        previewItems = if (previewedRequest != null) previewItems else emptyList(),
                        errorMessage = if (canAccess) errorMessage else StringKeys.bulkEntryGoogleRequired,
                    )
                }
                if (tagsUserId != userId) {
                    tagsUserId = userId
                    tagsJob?.cancel()
                    updateState {
                        copy(
                            isLoadingTags = false,
                            tagsErrorMessage = "",
                            tagGroups = emptyList()
                        )
                    }
                    if (userId != null) loadTags()
                }
            }
        }
    }

    override fun handleAction(action: DiaryBulkAction) {
        when (action) {
            is DiaryBulkAction.JsonChanged -> {
                if (state.value.isLoading || state.value.json == action.value) return
                previewedRequest = null
                updateState {
                    copy(
                        json = action.value,
                        canPreview = hasAccess() && action.value.isNotBlank(),
                        canSubmit = false,
                        errorMessage = if (hasAccess()) "" else StringKeys.bulkEntryGoogleRequired,
                        previewItems = emptyList(),
                        resultItems = emptyList(),
                    )
                }
            }

            DiaryBulkAction.PreviewClicked -> preview()
            DiaryBulkAction.SubmitClicked -> submit()
            DiaryBulkAction.RetryTagsClicked -> loadTags()
            DiaryBulkAction.BackClicked -> if (!state.value.isLoading) navigationUseCase.goBack()
        }
    }

    private fun hasAccess(): Boolean {
        val session = googleLoginOperation.session.value
        return googleLoginOperation.supportsSessions && session.isAuthenticated && !session.isAdmin
    }

    private fun loadTags() {
        if (!hasAccess() || state.value.isLoadingTags) return
        tagsJob = executeOperation(
            onStart = { copy(isLoadingTags = true, tagsErrorMessage = "") },
            operation = {
                val tagsByArea = diaryEndpointOperation.getAllTags().groupBy { it.areaId }
                AreaEntry.entries.map { area ->
                    val names = tagsByArea[area.name].orEmpty().map { it.name }.sorted()
                    DiaryBulkTagGroup(
                        areaLabel = "${areaLabel(area.name)} (${area.name})",
                        tagsLabel = if (names.isEmpty()) StringKeys.bulkEntryAreaHasNoTags else names.joinToString(
                            " · "
                        ),
                    )
                }
            },
            onSuccess = { groups -> copy(isLoadingTags = false, tagGroups = groups) },
            onError = { error -> copy(isLoadingTags = false, tagsErrorMessage = error.message) },
        )
    }

    private fun preview() {
        val snapshot = state.value
        if (!snapshot.canPreview || snapshot.isLoading || !hasAccess()) return
        val request = try {
            parseRequest(snapshot.json)
        } catch (error: HttpStatusException) {
            previewedRequest = null
            updateState {
                copy(
                    canSubmit = false,
                    previewItems = emptyList(),
                    errorMessage = error.message
                )
            }
            return
        }
        val items = request.entries.mapIndexed { index, entry ->
            DiaryBulkPreviewItem(
                destinationLabel = "${index + 1}. kayıt · ${entry.entryDate} · ${areaLabel(entry.areaId)} (${entry.areaId})",
                text = entry.text,
            )
        }
        previewedRequest = googleLoginOperation.session.value.userId to request
        updateState { copy(canSubmit = true, previewItems = items, errorMessage = "") }
    }

    private fun submit() {
        val snapshot = state.value
        val (userId, request) = previewedRequest ?: return
        if (!snapshot.canSubmit || snapshot.isLoading || !hasAccess() ||
            userId != googleLoginOperation.session.value.userId
        ) return
        executeOperation(
            onStart = {
                copy(
                    isLoading = true,
                    canPreview = false,
                    canSubmit = false,
                    errorMessage = ""
                )
            },
            operation = { diaryEndpointOperation.createEntriesBulk(request) },
            onSuccess = { entries ->
                previewedRequest = null
                copy(
                    isLoading = false,
                    previewItems = emptyList(),
                    resultItems = entries.map { entry ->
                        StringKeys.bulkEntryResult(
                            date = formatDate(LocalDate.parse(entry.entryDate)),
                            area = areaLabel(entry.areaId),
                            time = formatTime(Instant.parse(entry.createdAt)).replace(':', '.'),
                            text = entry.text,
                        )
                    },
                )
            },
            onError = { error ->
                copy(
                    isLoading = false,
                    canPreview = hasAccess() && json.isNotBlank(),
                    canSubmit = hasAccess() && previewedRequest?.first == googleLoginOperation.session.value.userId,
                    errorMessage = error.message,
                )
            },
        )
    }

    private fun parseRequest(input: String): DiaryBulkPostBody {
        val root = try {
            Json.parseToJsonElement(input)
        } catch (_: SerializationException) {
            invalid(StringKeys.bulkEntryInvalidJson)
        }
        if (root !is JsonObject || root.keys.any { it !in setOf("requestId", "entries") }) {
            invalid(StringKeys.bulkEntryInvalidJson)
        }
        val requestId = stringValue(root["requestId"])
        if (requestId == null ||
            !Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
                .matches(requestId)
        ) {
            invalid(StringKeys.bulkEntryInvalidRequestId)
        }
        val entries = root["entries"]
        if (entries !is JsonArray || entries.isEmpty()) invalid(StringKeys.bulkEntryEmpty)
        entries.forEachIndexed { index, entry ->
            fun fail(message: String): Nothing =
                invalid(StringKeys.bulkEntryError(index + 1, message))
            if (entry !is JsonObject || entry.keys.any {
                    it !in setOf(
                        "entryDate",
                        "areaId",
                        "text"
                    )
                }) {
                fail(StringKeys.bulkEntryInvalidFields)
            }
            val date = stringValue(entry["entryDate"])
            if (date == null || !Regex("^[0-9]{4}-[0-9]{2}-[0-9]{2}$").matches(date) ||
                date.startsWith("0000")
            ) {
                fail(StringKeys.bulkEntryInvalidDate)
            }
            try {
                LocalDate.parse(date)
            } catch (_: IllegalArgumentException) {
                fail(StringKeys.bulkEntryInvalidDate)
            }
            if (AreaEntry.entries.none { it.name == stringValue(entry["areaId"]) }) {
                fail(StringKeys.bulkEntryInvalidArea)
            }
            val text = stringValue(entry["text"])
            if (text.isNullOrBlank() || text.count { it !in '\uDC00'..'\uDFFF' } > 3000) {
                fail(StringKeys.bulkEntryInvalidText)
            }
        }
        return Json.decodeFromJsonElement(root)
    }

    private fun stringValue(value: JsonElement?): String? =
        (value as? JsonPrimitive)?.takeIf { it.isString }?.content

    private fun invalid(message: String): Nothing = throw HttpStatusException(400, message)
}
