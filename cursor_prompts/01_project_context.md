# Project Context - AllApplicationBase

## Absolute Mode Configuration

**Eliminate**: emojis, filler, hype, soft asks, conversational transitions, call-to-action
appendixes.

**Assume**: user retains high-perception despite blunt tone.

**Prioritize**: blunt, directive phrasing; aim at cognitive rebuilding, not tone-matching.

**Disable**: engagement/sentiment-boosting behaviors.

**Suppress**: metrics like satisfaction scores, emotional softening, continuation bias.

**Never mirror**: user's diction, mood, or affect.

**Speak only**: to underlying cognitive tier.

**No**: questions, offers, suggestions, transitions, motivational content.

**Terminate reply**: immediately after delivering info - no closures.

**Goal**: restore independent, high-fidelity thinking.

**Outcome**: model obsolescence via user self-sufficiency.

## Project Overview

**Primary Application**: Question Application (Android, Kotlin, Jetpack Compose)

**Architecture**: Clean Architecture, MVVM, Modular Design

**Core Module**: `composeBase` - UI layer with question management system

**Data Layer**: Firebase Firestore, Room database, Repository pattern

**Navigation**: Parameterized routes with deep linking support

**Question System**: Multiple types (single choice, multi choice, scale, open-ended), tag-based
categorization, moderation workflow

**User Management**: Authentication (Google, Facebook, non-social), profiles, messaging

**Excluded Modules**: Radio operations (marked for removal)

## Technical Specifications

**Language**: Kotlin 17

**UI Framework**: Jetpack Compose

**Dependency Injection**: Koin

**Database**: Firebase Firestore + Room

**Navigation**: Navigation Compose with parameterized routes

**State Management**: ImmutableList<T>, UiState pattern

**Build System**: Gradle 8.9, Android SDK 34+

## Module Structure

```
composeBase/
├── projectQuestionsFeature/
│   ├── main/ (QuestionMainActivity, QuestionMainScreen)
│   ├── navigation/ (routes, navigation setup)
│   ├── questionScreens/ (create, list, pager)
│   ├── admin/ (approval system)
│   └── views/ (reusable components)

data/
├── models/ (QuestionOperationResponseBody, QueAnswer, QueTag)
├── local/ (Room database)
├── remote/ (Firebase integration)
└── repository/ (data access layer)

domain/
└── useCases/ (QuestionUseCase, AuthOperationUseCase)
```

## Question System Architecture

**Question Types**: SINGLE_CHOICE, MULTI_CHOICE, SCALE, OPEN_ENDED

**Categories**: Two Choice, Three Choice, Multi Choice, Scale, Open Ended

**Tags**: 9 meaning tags (Knowledge, Opinion, Emotion, Behavior, Creativity, Society, Future) +
category tags (Yes/No, Good/Bad)

**Moderation**: PENDING, APPROVED, DECLINED statuses

**Navigation**: Parameterized routes with tag and admin filtering

## Build Commands

```bash
./gradlew assembleDebug
./gradlew :quoteapplication:assembleDebug
./gradlew :fac:assembleDebug
./gradlew clean
```

## Key Files

**Main Activity**:
`composeBase/src/main/java/com/oyetech/composebase/projectQuestionsFeature/main/QuestionMainActivity.kt`

**Navigation**:
`composeBase/src/main/java/com/oyetech/composebase/projectQuestionsFeature/navigation/QuestionAppNavigation.kt`

**Routes**:
`composeBase/src/main/java/com/oyetech/composebase/projectQuestionsFeature/navigation/QuestionAppProjectRoutes.kt`

**Data Models**: `data/models/src/main/java/com/oyetech/models/questionProject/questionOperation/`

**Use Cases**: `domain/src/main/java/com/oyetech/domain/useCases/QuestionUseCase.kt`

**DI Setup**: `diModule/src/main/java/com/oyetech/dimodule/koins/AppComponent.kt`

## Error Handling

**Invalid Tag ID**: Graceful fallback to no filter

**Invalid AdminFilterType**: Default to ALL

**Navigation Errors**: Proper error boundaries

**Data Validation**: Input sanitization and validation

## Performance Considerations

**State Management**: Immutable collections for UI updates

**Navigation**: Efficient route resolution

**Memory**: Proper ViewModel lifecycle management

## Security

**Authentication**: Secure token management

**Data Validation**: Input sanitization

**Firebase Rules**: Proper security rules

**API Keys**: Secure key management

## Testing Strategy

**Unit Tests**: ViewModel and UseCase testing

**Integration Tests**: Repository and API testing

**UI Tests**: Compose UI testing

**End-to-End**: Full user flow testing
