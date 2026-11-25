package com.oyetech.kmpfeatures.diary

import com.oyetech.kmpdomain.error.HttpStatusException
import com.oyetech.kmpdomain.usecase.navigation.NavigationUseCase
import com.oyetech.kmpfeatures.auth.GoogleLoginOperation
import com.oyetech.kmpfeatures.operator.BaseFeatureOperator
import com.oyetech.kmpmodels.entity.AreaEntry
import com.oyetech.kmpmodels.postbody.DiaryBulkEntryPostBody
import com.oyetech.kmpmodels.postbody.DiaryBulkPostBody
import com.oyetech.kmpmodels.postbody.MAX_ENTRY_TEXT_LENGTH
import com.oyetech.kmpmodels.response.TagResponse
import com.oyetech.kmpmodels.stringKeys.StringKeys
import com.oyetech.kmpmodels.ui.event.DiaryBulkAction
import com.oyetech.kmpmodels.ui.state.DiaryBulkPreviewItem
import com.oyetech.kmpmodels.ui.state.DiaryBulkTagGroup
import com.oyetech.kmpmodels.ui.state.DiaryBulkUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.longOrNull
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private val bulkUuidPattern =
    Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
private val bulkEntryFields = setOf("entryDate", "areaId", "text", "tagId", "durationSeconds", "markAsCompleted")
private val exampleJsonFormat = Json { prettyPrint = true }

@OptIn(ExperimentalUuidApi::class)
private fun bulkExampleJson(tag: TagResponse? = null): String {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
    return exampleJsonFormat.encodeToString(
        DiaryBulkPostBody(
            requestId = Uuid.random().toString(),
            entries = listOf(
                DiaryBulkEntryPostBody(
                    entryDate = today,
                    areaId = tag?.areaId ?: AreaEntry.WORK.name,
                    text = "25 dakika çalıştım.",
                    tagId = tag?.id,
                    durationSeconds = 1500,
                    markAsCompleted = true,
                ),
                DiaryBulkEntryPostBody(entryDate = today, areaId = AreaEntry.BODY.name, text = "Yürüyüş yaptım."),
            ),
        ),
    )
}

class DiaryBulkOperator(
    operatorScope: CoroutineScope,
    private val navigationUseCase: NavigationUseCase,
    private val diaryEndpointOperation: DiaryEndpointOperation,
    private val googleLoginOperation: GoogleLoginOperation,
) : BaseFeatureOperator<DiaryBulkUiState, DiaryBulkAction, Nothing>(
    initialState = DiaryBulkUiState(exampleJson = bulkExampleJson()),
    operatorScope = operatorScope,
) {
    private var tagsJob: Job? = null
    private var loadedTags: Pair<String, Map<String, TagResponse>>? = null
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
                    loadedTags = null
                    updateState {
                        copy(
                            isLoadingTags = false,
                            tagsErrorMessage = "",
                            tagGroups = emptyList(),
                            exampleJson = bulkExampleJson(),
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
        val userId = googleLoginOperation.session.value.userId
        tagsJob = executeOperation(
            onStart = { copy(isLoadingTags = true, tagsErrorMessage = "") },
            operation = { diaryEndpointOperation.getAllTags() },
            onSuccess = { tags ->
                val ordered = tags.sortedWith(compareBy({ it.areaId }, { it.name }, { it.id }))
                val tagsByArea = ordered.groupBy { it.areaId }
                loadedTags = userId to ordered.associateBy { it.id.lowercase() }
                val groups = AreaEntry.entries.map { area ->
                    val areaTags = tagsByArea[area.name].orEmpty()
                    DiaryBulkTagGroup(
                        areaLabel = "${areaLabel(area.name)} (${area.name})",
                        tagsLabel = if (areaTags.isEmpty()) StringKeys.bulkEntryAreaHasNoTags
                        else areaTags.joinToString("\n") { "${it.name} — ${it.id}" },
                    )
                }
                copy(isLoadingTags = false, tagGroups = groups, exampleJson = bulkExampleJson(ordered.firstOrNull()))
            },
            onError = { error -> copy(isLoadingTags = false, tagsErrorMessage = error.message) },
        )
    }

    private fun bulkAreaLabel(areaId: String?): String =
        if (areaId == null) StringKeys.bulkEntryNoArea else "${areaLabel(areaId)} ($areaId)"

    private fun entryDetails(tagName: String?, durationSeconds: Long?, completed: Boolean): String =
        listOfNotNull(
            tagName?.let { "#$it" } ?: StringKeys.bulkEntryNoTag,
            durationSeconds?.let(StringKeys::diaryExportDuration),
            if (completed) StringKeys.alreadyDone else StringKeys.bulkEntryNotCompleted,
        ).joinToString(" · ")

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
                destinationLabel = "${index + 1}. kayıt · ${entry.entryDate} · ${bulkAreaLabel(entry.areaId)}",
                text = entry.text,
                details = entryDetails(
                    entry.tagId?.let { loadedTags?.second?.get(it.lowercase())?.name },
                    entry.durationSeconds,
                    entry.markAsCompleted,
                ),
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
                            area = "${bulkAreaLabel(entry.areaId)} · " +
                                entryDetails(entry.tag?.name, entry.durationSeconds, entry.completedAt != null),
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
        if (requestId == null || !bulkUuidPattern.matches(requestId)) {
            invalid(StringKeys.bulkEntryInvalidRequestId)
        }
        val entries = root["entries"]
        if (entries !is JsonArray || entries.isEmpty()) invalid(StringKeys.bulkEntryEmpty)
        entries.forEachIndexed { index, entry ->
            fun fail(message: String): Nothing =
                invalid(StringKeys.bulkEntryError(index + 1, message))
            if (entry !is JsonObject || entry.keys.any { it !in bulkEntryFields }) {
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
            val area = entry["areaId"]?.takeUnless { it == JsonNull }?.let {
                stringValue(it) ?: fail(StringKeys.bulkEntryInvalidArea)
            }
            if (area != null && AreaEntry.entries.none { it.name == area }) {
                fail(StringKeys.bulkEntryInvalidArea)
            }
            val text = entry["text"]?.let {
                stringValue(it) ?: fail(StringKeys.bulkEntryInvalidText(MAX_ENTRY_TEXT_LENGTH))
            }.orEmpty()
            if (text.count { it !in '\uDC00'..'\uDFFF' } > MAX_ENTRY_TEXT_LENGTH) {
                fail(StringKeys.bulkEntryInvalidText(MAX_ENTRY_TEXT_LENGTH))
            }
            val duration = entry["durationSeconds"]?.takeUnless { it == JsonNull }?.let {
                (it as? JsonPrimitive)?.takeUnless { it.isString }?.longOrNull
                    ?.takeIf { seconds -> seconds in 1..MAX_CUSTOM_DURATION_MINUTES * 60 }
                    ?: fail(StringKeys.bulkEntryInvalidDuration)
            }
            val completed = entry["markAsCompleted"]?.let {
                (it as? JsonPrimitive)?.takeUnless { it.isString }?.booleanOrNull
                    ?: fail(StringKeys.bulkEntryInvalidCompletion)
            } ?: false
            entry["tagId"]?.takeUnless { it == JsonNull }?.let {
                val tagId = stringValue(it)?.takeIf(bulkUuidPattern::matches)
                    ?: fail(StringKeys.bulkEntryInvalidTag)
                if (area == null) fail(StringKeys.bulkEntryInvalidTag)
                val tags = loadedTags?.takeIf { (userId, _) -> userId == googleLoginOperation.session.value.userId }
                    ?.second ?: fail(StringKeys.bulkEntryTagsNotReady)
                if (tags[tagId.lowercase()]?.areaId != area) fail(StringKeys.bulkEntryInvalidTag)
            }
            if (area == null && text.isBlank() && duration == null && !completed) {
                fail(StringKeys.bulkEntryMissingContent)
            }
        }
        return Json.decodeFromJsonElement(root)
    }

    private fun stringValue(value: JsonElement?): String? =
        (value as? JsonPrimitive)?.takeIf { it.isString }?.content

    private fun invalid(message: String): Nothing = throw HttpStatusException(400, message)
}
