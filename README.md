# AllApplicationBase - Multi-Module Android Project

A comprehensive Android project built with a modular architecture, featuring multiple applications
including a **Question Application** as the primary focus, along with quote and radio applications.
The project emphasizes clean architecture, Jetpack Compose UI, and Firebase integration.

## 🏗️ Project Architecture

### Core Architecture Pattern

- **Clean Architecture** with clear separation of concerns
- **MVVM Pattern** with Jetpack Compose UI
- **Dependency Injection** using Koin
- **Modular Design** with feature-based modules

### Layer Structure

```
📁 data/
├── models/          # Data models and entities
├── local/           # Local database (Room)
├── remote/          # API and Firebase integration
└── repository/      # Repository implementations

📁 domain/
└── useCases/        # Business logic and use cases

📁 composeBase/
└── projectQuestionsFeature/  # Question application UI
```

## 🎯 Primary Application: Question App

The **Question Application** is the main focus of this project, providing a comprehensive platform
for creating, managing, and answering various types of questions.

### Key Features

#### 1. Question Management

- **Question Creation**: Support for multiple question types
- **Question Types**: Single choice, multiple choice, rating scales, open-ended
- **Question Categories**:
    - Two Choice (Yes/No, Good/Bad, Up/Down)
    - Three Choice (Low/Medium/High, Agree/Neutral/Disagree)
    - Multi Choice, Scale, Open-ended

#### 2. Question Taxonomy System

- **Meaning Tags**: Knowledge, Opinion, Emotion, Behavior, Creativity, Society, Future
- **Category Tags**: Yes/No, Good/Bad
- **Moderation System**: Pending, Approved, Declined statuses

#### 3. User Interface

- **Bottom Navigation**: Home, Question List, Settings, User List, Messages
- **Question Pager**: Swipeable question interface
- **Filter System**: Tag-based and admin-based filtering
- **Parameterized Navigation**: Deep linking support

#### 4. User Management

- **Authentication**: Google Sign-in, Facebook, Non-social auth
- **User Profiles**: Complete profile management
- **Messaging System**: User-to-user communication

### Navigation Structure

```
QuestionAppHomepage
├── QuestionCreateQuestionPage
├── QuestionPager
├── QuestionListWithParams (with filters)
├── AdminApproveQuestion
├── UserList
├── UserProfile
├── MessageConversationList
├── MessageDetail
└── QuestionAppSettings
```

## 📱 Applications

### 1. Question Application (`quoteapplication`)

- **Main Activity**: `QuestionMainActivity`
- **Primary Feature**: Question creation, answering, and management
- **Target Users**: General users, content creators, administrators

### 2. Quote Application (`fac`)

- **Main Activity**: `QuoteMainActivity`
- **Primary Feature**: Quote browsing and management
- **Integration**: Shared with question app in same module

### 3. Radio Application (`radioeveryonee`)

- **Note**: Marked for future removal as per project requirements
- **Current Status**: Legacy module, not primary focus

## 🧩 Core Modules

### Compose Base Module (`composeBase`)

The central UI module containing all Compose components and screens.

#### Key Components:

- **Base Components**: `BaseScaffold`, `BaseViewModel`, `UIState`
- **Navigation**: Complete navigation system with parameterized routes
- **Question Features**: All question-related screens and logic
- **Shared Screens**: User management, messaging, settings
- **Helper Classes**: Vibration, image handling, list management

#### Question Feature Structure:

```
projectQuestionsFeature/
├── main/                    # Main activity and screen
├── navigation/              # Navigation routes and setup
├── homeScreen/             # Home screen implementation
├── questionScreens/
│   ├── createQuestion/     # Question creation
│   ├── list/              # Question listing with filters
│   └── questionList/      # Additional list views
├── admin/                  # Admin functionality
├── adminApprove/          # Question approval system
├── contentOperation/      # Content management
├── generalOperationScreen/ # General operations
├── theme/                 # UI theming
└── views/                 # Reusable question views
```

### Data Layer (`data/`)

Comprehensive data management with multiple sub-modules.

#### Models (`data/models`)

- **Question Models**: `QuestionOperationResponseBody`, `QueAnswer`, `QueTag`
- **User Models**: `FirebaseUserProfileModel`, `UserDetailDataResponse`
- **Message Models**: `MessageConversationDataResponse`, `FirebaseMessagingResponseData`
- **Quote Models**: `QuoteResponseData`

#### Key Data Models:

```kotlin
// Question Structure
data class QuestionOperationResponseBody(
    val questionId: String,
    val questionTitle: String,
    val questionType: QuestionType,
    val moderationStatus: ModerationStatus,
    val taxonomy: QuestionTaxonomyRef,
    val options: List<QueOption>,
    val tags: List<QueTag>,
    val metadata: Map<String, String>
)

// Answer Structure  
data class QueAnswer(
    val questionId: String,
    val type: QuestionType,
    val selectedOptionIds: List<String>?,
    val numericValue: Double?,
    val textValue: String?,
    val userId: String
)
```

### Domain Layer (`domain`)

Business logic and use cases.

#### Key Use Cases:

- **QuestionUseCase**: Question management and filtering
- **AuthOperationUseCase**: Authentication operations
- **NavigationUseCase**: App navigation management

### Supporting Modules

#### Core Infrastructure:

- **`diModule`**: Dependency injection setup with Koin
- **`languageModule`**: Internationalization support
- **`firebaseDB`**: Firebase Firestore integration
- **`firebaseRealtime`**: Real-time database operations
- **`notificationModule`**: Push notification handling

#### UI & Media:

- **`glideModule`**: Image loading and caching
- **`exoplayerModule`**: Media playback capabilities
- **`audioPlayerHelper`**: Audio management utilities

#### External Services:

- **`googleLogin`**: Google authentication
- **`adsModule`**: Advertisement integration
- **`analyticsModule`**: Analytics tracking
- **`reviewer`**: App review functionality

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: Latest stable version
- **JDK**: Kotlin 17 toolchain
- **Android SDK**: compileSdk 34+
- **Gradle**: 8.9 wrapper

### Build Commands

```bash
# Build all modules
./gradlew assembleDebug

# Build specific applications
./gradlew :quoteapplication:assembleDebug
./gradlew :fac:assembleDebug

# Install on device
./gradlew :quoteapplication:installDebug

# Clean build
./gradlew clean
```

### Development Setup

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Build the project
5. Run the desired application module

## 🛠️ Development Guidelines

### Code Standards

- **Compose Functions**: No hardcoded data, use UiState
- **State Management**: Use `ImmutableList<T>` for collections
- **Event Handling**: Use sealed class pattern for events
- **Screen Setup**: Wiring only, no business logic
- **Navigation**: Use `NavigationUseCase` inside ViewModels

### Architecture Patterns

- **UI Events**: Single `onEvent: (Event) -> Unit` callback
- **ViewModel Injection**: Use `viewModelOf(::ViewModel)`
- **Repository Pattern**: Clear separation between data sources
- **Use Case Pattern**: Business logic encapsulation

### Import Rules

- Use short imports: `Spacer()` not `androidx.compose.foundation.layout.Spacer()`
- No import lines in documentation examples
- Prefer specific imports over wildcards

## 📊 Question System Deep Dive

### Question Types & Categories

#### Question Types

- **SINGLE_CHOICE**: Single selection from options
- **MULTI_CHOICE**: Multiple selections allowed
- **SCALE**: Numeric rating scales
- **OPEN_ENDED**: Free text responses

#### Question Categories

- **Two Choice**: Yes/No, Up/Down, Good/Bad
- **Three Choice**: Low/Medium/High, Agree/Neutral/Disagree
- **Multi Choice**: Multiple options selection
- **Scale**: Numeric rating (0-100)
- **Open Ended**: Free text input

### Tag System

Questions are categorized using a comprehensive tag system:

#### Meaning Tags

- **Knowledge & Understanding**: Educational content
- **Opinion & Belief**: Subjective questions
- **Emotion & Experience**: Feelings and experiences
- **Behavior & Choice**: Actions and decisions
- **Creativity & Imagination**: Creative thinking
- **Society & Connection**: Social aspects
- **Future & Uncertainty**: Forward-looking questions

#### Category Tags

- **Yes/No**: Binary choice questions
- **Good/Bad**: Value judgment questions

### Filtering & Navigation

The system supports advanced filtering through parameterized navigation:

```kotlin
// Tag-based filtering
val route = QuestionListNavigationHelper.buildQuestionListWithTagRoute(
    QuestionTagCatalog.KNOWLEDGE
)

// Admin filtering
val route = QuestionListNavigationHelper.buildQuestionListWithAdminFilterRoute(
    QuestionListAdminFilterType.PENDING_ADMIN
)

// Combined filtering
val route = QuestionListNavigationHelper.buildQuestionListWithFiltersRoute(
    tag = QuestionTagCatalog.OPINION,
    adminFilterType = QuestionListAdminFilterType.APPROVED_ADMIN
)
```

## 🔧 Configuration

### Firebase Setup

- **Firestore**: Question and user data storage
- **Authentication**: User management
- **Realtime Database**: Live updates
- **Cloud Messaging**: Push notifications

### Build Configuration

- **ProGuard**: Enabled for release builds
- **Multi-dex**: Supported for large apps
- **Vector Drawables**: Optimized for different densities

## 📈 Future Roadmap

### Planned Features

1. **Enhanced Question Types**: More question formats
2. **Advanced Analytics**: User behavior tracking
3. **Social Features**: Question sharing and collaboration
4. **Offline Support**: Local question caching
5. **AI Integration**: Smart question recommendations

### Module Cleanup

- **Radio Operations**: Scheduled for removal
- **Legacy Code**: Ongoing refactoring
- **Performance**: Continuous optimization

## 🤝 Contributing

### Development Workflow

1. Create feature branch from `main`
2. Follow coding standards
3. Write tests for new features
4. Update documentation
5. Submit pull request

### Code Review Process

- Architecture compliance
- Code quality standards
- Test coverage
- Documentation updates

## 📄 License

This project is proprietary software. All rights reserved.

---

**Note**: This project focuses primarily on the Question Application functionality. Radio-related
modules are marked for future removal and should not be considered part of the core application
architecture.
