## AiHighPulse — AI‑Driven Fitness (KMM)

Проект фитнес‑приложения с умными планами тренировок, питанием и сном. Клиент для Android построен на Jetpack Compose (Material 3), архитектура — Clean Architecture + MVVM, общий домен/данные — Kotlin Multiplatform.

— Быстрый прогон: `./gradlew :app-android:assembleDebug`

### Основные фичи
- Адаптивный UI: нижняя навигация на телефонах, Navigation Rail на планшетах
- Онбординг профиля: цель, опыт, параметры, предпочтения, расписание
- Дом: обзор дня, быстрые действия, «тренировка сегодня»
- Тренировки: план недели, логирование подходов (сеты/повторы/вес/RPE)
- Питание: недельное меню, макросы, список покупок (заглушка, но рабочий флоу)
- Сон и советы: базовые рекомендации с репозитория советов
- Прогресс: агрегированные метрики (кол-во тренировок/подходов/объём)
- Paywall: каркас подписки (интеграция Billing запланирована)

### Технологии
- UI: Jetpack Compose (Material3, Navigation), Compose adaptive layout
- DI: Koin (включая `koin-androidx-compose` для ViewModel)
- Архитектура: Clean Architecture, MVVM, `UiState` (Loading/Error/Data)
- KMM: общий слой домена/данных (`shared`), Ktor, kotlinx.serialization, SQLDelight (заготовка)
- Build: AGP 8.12.x, Kotlin 2.0.x, JDK 17

### Структура проекта
```
AiHighPulse/
├─ app-android/                      # Android клиент (Compose)
│  ├─ src/main/java/com/example/aihighpulse/
│  │  ├─ di/                         # Модуль Koin (Android VM)
│  │  ├─ ui/navigation/              # Routes
│  │  ├─ ui/components/              # Общие UI-компоненты (Placeholder/Skeleton/StatChip)
│  │  ├─ ui/screens/                 # Экранные Composable’ы
│  │  └─ ui/vm/                      # ViewModel’и (MVVM)
│  └─ build.gradle.kts               # Зависимости Android
├─ shared/                           # KMM слой (домен/данные/юзкейсы)
│  └─ src/commonMain/kotlin/...      # UseCases, Repositories, Models, Ktor
├─ app-ios/                          # iOS заготовка (таргеты выключены локально)
├─ gradle/ libs.versions.toml        # Версии зависимостей
├─ settings.gradle.kts               # Gradle настройки воркспейса
└─ README.md                         # Этот файл
```

### Архитектура (вкратце)
- Presentation (Android): Compose + ViewModel (Koin DI), `UiState` для явных состояний
- Domain: UseCases оперируют Domain‑моделями
- Data: Repositories инкапсулируют источники (Ktor API, SQLDelight, Settings)

Shared модуль уже содержит: модели, интерфейсы репозиториев, use cases и локальные заглушки через Koin.

### Быстрый старт (Android)
1) Требования
- Android Studio Koala+ / JDK 17
- Android SDK 24+

2) Сборка
- Windows: `gradlew.bat :app-android:assembleDebug`
- macOS/Linux: `./gradlew :app-android:assembleDebug`

3) Запуск на устройстве/эмуляторе
- `:app-android:installDebug`
- Запускайте `MainActivity`

Примечание: предупреждения KMM/AGP подавлены флагами в `gradle.properties`:
```
kotlin.mpp.androidGradlePluginCompatibility.nowarn=true
kotlin.native.ignoreDisabledTargets=true
```

### Конфигурация API
- Базовый URL задаётся в `app-android/src/main/java/com/example/aihighpulse/AiHighPulseApp.kt`:
  `modules(DI.coreModule(apiBaseUrl = "https://api.example.com"))`
- Реальные реализации репозиториев можно подключить вместо заглушек в `shared`.

### Скриншоты
Добавьте в `docs/screenshots/` и вставьте сюда:
- Home (телефон)
- Home (планшет / NavigationRail)
- Onboarding, Workout, Nutrition

Пример вставки:
```
![Home Phone](docs/screenshots/home_phone.png)
![Home Tablet](docs/screenshots/home_tablet.png)
```

### Roadmap
- Данные: репозитории -> реальные источники (Ktor + SQLDelight), оффлайн‑кэш
- Покупки: Billing + полноценный флоу подписки
- Health Connect: импорт сна/активности
- Дизайн‑система: выделить `:core:designsystem` модуль
- Локализация: вынести строки и добавить `values-ru/values-en`
- Тесты: unit (usecases), UI (Compose), интеграционные для репозиториев
- CI/CD: GitHub Actions, подпись, релизные артефакты

### Вклад
PR и обсуждения приветствуются. Предлагайте улучшения по архитектуре и UX.

### Быстрые ссылки на ключевые файлы
- Android корень: `app-android/src/main/java/com/example/aihighpulse/MainActivity.kt:1`
- DI (Android ViewModel): `app-android/src/main/java/com/example/aihighpulse/di/AppModule.kt:1`
- Роуты: `app-android/src/main/java/com/example/aihighpulse/ui/navigation/Routes.kt:1`
- Экран Онбординга: `app-android/src/main/java/com/example/aihighpulse/ui/screens/OnboardingScreen.kt:1`
- ViewModels: `app-android/src/main/java/com/example/aihighpulse/ui/vm/`
- UseCases (KMM): `shared/src/commonMain/kotlin/com/example/aihighpulse/shared/domain/usecase/UseCases.kt:1`

