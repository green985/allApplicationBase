# Soru Oluşturma ve Yayınlama Akışı (Question Creation & Publishing Flow)

Bu doküman, tasarımcıyla iletişime geçilmeden önce uygulanması gereken soru oluşturma ve yayınlama
sürecinin detaylı bir haritasını içerir.

---

## 1. Genel Akış Özeti (Overall Flow Summary)

```
[Başla] 
  ↓
[Soru Türü Seçimi] (Question Type Selection)
  ├─ TWO_CHOICE (YES/NO, UP/DOWN, GOOD/BAD)
  ├─ THREE_CHOICE (Low/Med/High, Agree/Neutral/Disagree)
  ├─ MULTI_CHOICE
  ├─ SCALE
  └─ OPEN_ENDED
  ↓
[Soru Stili/Alt Kategorisi Seçimi] (Question Style / Sub-Category Selection)
  ├─ Seçili türe göre alt kategoriler gösterilir
  └─ Her tür için varsayılan seçenekler/constraints ayarlanır
  ↓
[Soru İçeriği Girişi] (Question Content Entry)
  ├─ Başlık (Title) - Zorunlu
  ├─ Açıklama (Description) - Opsiyonel
  └─ İçerik Doğrulama
  ↓
[Soru Önizlemesi] (Question Preview)
  ├─ Seçili tür/stilde soru nasıl görüneceğini göster
  └─ Kullanıcı geri dönüş seçeneği var
  ↓
[Soru Yayınlama] (Question Publishing)
  ├─ Backend'e gönder (Firestore)
  ├─ Yayınlama Durumu: "PENDING" (Moderation için hazır)
  └─ Başarılı/Hata Bildirimi
  ↓
[Admin Moderasyon Ekranı] (Admin Moderation Screen)
  ├─ Bekleme Durumundaki Soruları Listele
  ├─ Onay (Approve) / Reddet (Decline)
  └─ Onaylanan Sorular: "APPROVED" → Kullanıcılara gösterilir
```

---

## Screens

### 1. QuestionTypeSelectionScreenSetup

File: `QuestionTypeSelectionScreenSetup.kt` (NEW)
State: QuestionTypeSelectionUiState (selectedType, isLoading)
Event: QuestionTypeSelectionEvent (OnTypeSelected, OnCancel)

### 2. QuestionStyleSelectionScreenSetup

File: `QuestionStyleSelectionScreenSetup.kt` (NEW)
State: QuestionStyleSelectionUiState (selectedType, selectedStyle, isLoading)
Event: QuestionStyleSelectionEvent (OnStyleSelected, OnBack, OnCancel)
Dynamic content based on category selected

### 3. QuestionCreateQuestionScreenSetup (Extend)

File: Existing `QuestionCreateQuestionScreenSetup.kt`
Add: selectedTypeLabel, selectedStyleLabel, currentOptions to UiState
Add: OnDescriptionChange, OnPreview to Event

### 4. QuestionPreviewScreenSetup

File: `QuestionPreviewScreenSetup.kt` (NEW)
State: QuestionPreviewUiState (question, isLoading)
Event: QuestionPreviewEvent (OnEdit, OnPublish, OnCancel)

### 5. Admin Moderation (Extend)

File: Existing `AdminApproveQuestionScreenSetup.kt`
Add approve/decline per-item buttons

## Backend Model Changes

QuestionOperationResponseBody fields:
status: String (PENDING, APPROVED, DECLINED)
creatorId: String
approvedAt: Long?
declinedAt: Long?
approvedBy: String?
declineReason: String?

## Repository Functions

Existing:
getQuestionList(): Flow<List<QuestionOperationResponseBody>>
createQuestion(body): Flow<Unit>

New:
getQuestionsByStatus(status: String): Flow<List<QuestionOperationResponseBody>>
approveQuestion(questionId, adminId): Flow<Unit>
declineQuestion(questionId, adminId, reason): Flow<Unit>
approveAllPendingQuestions(adminId): Flow<Unit>

## ViewModel Chain

QuestionTypeSelectionVm → QuestionStyleSelectionVm → QuestionCreateQuestionVm → QuestionPreviewVm
Each preserves prior selections. Back navigation retains state.

## Language Keys Required

questionTypeSelectionTitle, twoChoiceLabel, threeChoiceLabel, multiChoiceLabel, scaleLabel,
openEndedLabel
questionStyleSelectionTitle, yesNoLabel, upDownLabel, goodBadLabel, lowMedHighLabel,
agreeNeutralDisagreeLabel
questionContentInputTitle, questionTitlePlaceholder, questionDescriptionPlaceholder,
questionPreviewButton
questionPreviewTitle, editButton, publishButton
questionPublishingSuccessMessage, questionPublishingErrorMessage
adminModerationTitle, allTab, pendingTab, approvedTab, declinedTab, approveAllButton,
declineAllButton

## Designer Deliverables

UI mockups: Type Selection, Style Selection, Content Input, Preview, Moderation
Icons: Question types (2choice, 3choice, multi, scale, open), Moderation actions
Colors: PENDING (orange), APPROVED (green), DECLINED (red)
Animations: Selection feedback, Screen transitions, Loading states

## Implementation Phases

1. Model & Repository extensions
2. QuestionTypeSelectionScreen + VM
3. QuestionStyleSelectionScreen + VM
4. QuestionCreateQuestionScreen extend
5. QuestionPreviewScreen + VM
6. AdminApproveQuestionScreen extend (buttons)
7. Integration & Testing

## Test Scenarios

Positive: Create all types, publish, admin approve, verify in list
Negative: Empty title fails, admin decline, notification sent
Edge: Network failure, background kill, undo approval
