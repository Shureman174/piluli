# PROJECT_AI_RULES.md

## 🛠 Core Architecture & Technology Stack
- **Language:** Kotlin only (No Java snippets).
- **UI Framework:** Jetpack Compose ONLY. No XML layout files allowed.
- **Architecture:** MVVM + Repository Pattern.
- **Asynchronous:** Coroutines + Flow. Use `StateFlow` strictly for UI state; avoid LiveData.
- **Design System:** Material 3.
- **Build System:** Gradle Kotlin DSL.

## 🏗 Development Standards
- **Principles:** Follow SOLID, DRY (Don't Repeat Yourself), and KISS (Keep It Simple, Stupid).
- **Code Style:** Use modern Kotlin idiomatic expressions.
- **Data Layer:** Implement Repository Pattern to abstract data sources (Room/DataStore).
- **State Management:** Ensure UI state is immutable and flows through ViewModels safely.

## 🚫 Constraints & Restrictions
1.  **No XML Support:** All components must be rendered with `@Composable`.
2.  **Modern Concurrency:** Use `viewModelScope` or `lifecycleScope`. Do not use `GlobalScope`.
3.  **Persistence:** Favor offline-first capabilities using local databases (Room/DataStore).
4.  **Clean Code:** Separate business logic from presentation layers clearly.

## 🎯 Long-term Goals
- Ensure high performance and stability for medical tracking.
- Build a scalable UI structure for easy addition of new types of medicines.
