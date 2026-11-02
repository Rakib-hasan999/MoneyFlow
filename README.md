# MoneyFlow

MoneyFlow is a native offline Android application built with Kotlin, Jetpack Compose, Material Design 3, and Room SQLite persistence for managing project-based finances.

## Features

- **Unified Financial Dashboard**: View overall income, expenses, and net profit/loss across all projects at a glance.
- **Multiple Project Management**: Create, edit, and delete distinct financial projects with optional descriptions.
- **Income & Expense Tracking**: Log, edit, and delete income and expense transactions (`৳`) with notes and timestamps per project.
- **Automatic Profit & Loss Calculation**: Real-time calculation of total income, total expenses, and net profit/loss per project and globally.
- **100% Offline & Local Storage**: Powered by Android Room Database (`moneyflow.db`) with cascading project-to-transaction relationships. No internet connection required.

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material Design 3
- **Architecture**: MVVM (ViewModel + StateFlow + Repository Pattern)
- **Database**: Room (`androidx.room` with KSP)
- **Navigation**: Jetpack Navigation Compose
- **Build System**: Gradle (Kotlin DSL, AGP 9.1.1, SDK 36)
