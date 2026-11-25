package com.oyetech.kmpmodels.ui.state

data class DiaryBulkUiState(
    val json: String = "",
    val isLoading: Boolean = false,
    val canPreview: Boolean = false,
    val canSubmit: Boolean = false,
    val errorMessage: String = "",
    val previewItems: List<DiaryBulkPreviewItem> = emptyList(),
    val resultItems: List<String> = emptyList(),
    val isLoadingTags: Boolean = false,
    val tagsErrorMessage: String = "",
    val tagGroups: List<DiaryBulkTagGroup> = emptyList(),
    val exampleJson: String = "",
)

data class DiaryBulkPreviewItem(val destinationLabel: String, val text: String, val details: String)

data class DiaryBulkTagGroup(val areaLabel: String, val tagsLabel: String)
