## V-Tempe вЂ” AIвЂ‘Driven Fitness (KMM)

РџСЂРѕРµРєС‚ С„РёС‚РЅРµСЃвЂ‘РїСЂРёР»РѕР¶РµРЅРёСЏ СЃ СѓРјРЅС‹РјРё РїР»Р°РЅР°РјРё С‚СЂРµРЅРёСЂРѕРІРѕРє, РїРёС‚Р°РЅРёРµРј Рё СЃРЅРѕРј. РљР»РёРµРЅС‚ РґР»СЏ Android РїРѕСЃС‚СЂРѕРµРЅ РЅР° Jetpack Compose (Material 3), Р°СЂС…РёС‚РµРєС‚СѓСЂР° вЂ” Clean Architecture + MVVM, РѕР±С‰РёР№ РґРѕРјРµРЅ/РґР°РЅРЅС‹Рµ вЂ” Kotlin Multiplatform.

вЂ” Р‘С‹СЃС‚СЂС‹Р№ РїСЂРѕРіРѕРЅ: `./gradlew :app-android:assembleDebug`

### РћСЃРЅРѕРІРЅС‹Рµ С„РёС‡Рё
- РђРґР°РїС‚РёРІРЅС‹Р№ UI: РЅРёР¶РЅСЏСЏ РЅР°РІРёРіР°С†РёСЏ РЅР° С‚РµР»РµС„РѕРЅР°С…, Navigation Rail РЅР° РїР»Р°РЅС€РµС‚Р°С…
- РћРЅР±РѕСЂРґРёРЅРі РїСЂРѕС„РёР»СЏ: С†РµР»СЊ, РѕРїС‹С‚, РїР°СЂР°РјРµС‚СЂС‹, РїСЂРµРґРїРѕС‡С‚РµРЅРёСЏ, СЂР°СЃРїРёСЃР°РЅРёРµ
- Р”РѕРј: РѕР±Р·РѕСЂ РґРЅСЏ, Р±С‹СЃС‚СЂС‹Рµ РґРµР№СЃС‚РІРёСЏ, В«С‚СЂРµРЅРёСЂРѕРІРєР° СЃРµРіРѕРґРЅСЏВ»
- РўСЂРµРЅРёСЂРѕРІРєРё: РїР»Р°РЅ РЅРµРґРµР»Рё, Р»РѕРіРёСЂРѕРІР°РЅРёРµ РїРѕРґС…РѕРґРѕРІ (СЃРµС‚С‹/РїРѕРІС‚РѕСЂС‹/РІРµСЃ/RPE)
- РџРёС‚Р°РЅРёРµ: РЅРµРґРµР»СЊРЅРѕРµ РјРµРЅСЋ, РјР°РєСЂРѕСЃС‹, СЃРїРёСЃРѕРє РїРѕРєСѓРїРѕРє (Р·Р°РіР»СѓС€РєР°, РЅРѕ СЂР°Р±РѕС‡РёР№ С„Р»РѕСѓ)
- РЎРѕРЅ Рё СЃРѕРІРµС‚С‹: Р±Р°Р·РѕРІС‹Рµ СЂРµРєРѕРјРµРЅРґР°С†РёРё СЃ СЂРµРїРѕР·РёС‚РѕСЂРёСЏ СЃРѕРІРµС‚РѕРІ
- РџСЂРѕРіСЂРµСЃСЃ: Р°РіСЂРµРіРёСЂРѕРІР°РЅРЅС‹Рµ РјРµС‚СЂРёРєРё (РєРѕР»-РІРѕ С‚СЂРµРЅРёСЂРѕРІРѕРє/РїРѕРґС…РѕРґРѕРІ/РѕР±СЉС‘Рј)
- Paywall: РєР°СЂРєР°СЃ РїРѕРґРїРёСЃРєРё (РёРЅС‚РµРіСЂР°С†РёСЏ Billing Р·Р°РїР»Р°РЅРёСЂРѕРІР°РЅР°)

### РўРµС…РЅРѕР»РѕРіРёРё
- UI: Jetpack Compose (Material3, Navigation), Compose adaptive layout
- DI: Koin (РІРєР»СЋС‡Р°СЏ `koin-androidx-compose` РґР»СЏ ViewModel)
- РђСЂС…РёС‚РµРєС‚СѓСЂР°: Clean Architecture, MVVM, `UiState` (Loading/Error/Data)
- KMM: РѕР±С‰РёР№ СЃР»РѕР№ РґРѕРјРµРЅР°/РґР°РЅРЅС‹С… (`shared`), Ktor, kotlinx.serialization, SQLDelight (Р·Р°РіРѕС‚РѕРІРєР°)
- Build: AGP 8.12.x, Kotlin 2.0.x, JDK 17

### РЎС‚СЂСѓРєС‚СѓСЂР° РїСЂРѕРµРєС‚Р°
```
V-Tempe/
в”њв”Ђ app-android/                      # Android РєР»РёРµРЅС‚ (Compose)
в”‚  в”њв”Ђ src/main/java/com/vtempe/
в”‚  в”‚  в”њв”Ђ di/                         # РњРѕРґСѓР»СЊ Koin (Android VM)
в”‚  в”‚  в”њв”Ђ ui/navigation/              # Routes
в”‚  в”‚  в”њв”Ђ ui/components/              # РћР±С‰РёРµ UI-РєРѕРјРїРѕРЅРµРЅС‚С‹ (Placeholder/Skeleton/StatChip)
в”‚  в”‚  в”њв”Ђ ui/screens/                 # Р­РєСЂР°РЅРЅС‹Рµ ComposableвЂ™С‹
в”‚  в”‚  в””в”Ђ ui/vm/                      # ViewModelвЂ™Рё (MVVM)
в”‚  в””в”Ђ build.gradle.kts               # Р—Р°РІРёСЃРёРјРѕСЃС‚Рё Android
в”њв”Ђ shared/                           # KMM СЃР»РѕР№ (РґРѕРјРµРЅ/РґР°РЅРЅС‹Рµ/СЋР·РєРµР№СЃС‹)
в”‚  в””в”Ђ src/commonMain/kotlin/...      # UseCases, Repositories, Models, Ktor
в”њв”Ђ app-ios/                          # iOS Р·Р°РіРѕС‚РѕРІРєР° (С‚Р°СЂРіРµС‚С‹ РІС‹РєР»СЋС‡РµРЅС‹ Р»РѕРєР°Р»СЊРЅРѕ)
в”њв”Ђ gradle/ libs.versions.toml        # Р’РµСЂСЃРёРё Р·Р°РІРёСЃРёРјРѕСЃС‚РµР№
в”њв”Ђ settings.gradle.kts               # Gradle РЅР°СЃС‚СЂРѕР№РєРё РІРѕСЂРєСЃРїРµР№СЃР°
в””в”Ђ README.md                         # Р­С‚РѕС‚ С„Р°Р№Р»
```

### РђСЂС…РёС‚РµРєС‚СѓСЂР° (РІРєСЂР°С‚С†Рµ)
- Presentation (Android): Compose + ViewModel (Koin DI), `UiState` РґР»СЏ СЏРІРЅС‹С… СЃРѕСЃС‚РѕСЏРЅРёР№
- Domain: UseCases РѕРїРµСЂРёСЂСѓСЋС‚ DomainвЂ‘РјРѕРґРµР»СЏРјРё
- Data: Repositories РёРЅРєР°РїСЃСѓР»РёСЂСѓСЋС‚ РёСЃС‚РѕС‡РЅРёРєРё (Ktor API, SQLDelight, Settings)

Shared РјРѕРґСѓР»СЊ СѓР¶Рµ СЃРѕРґРµСЂР¶РёС‚: РјРѕРґРµР»Рё, РёРЅС‚РµСЂС„РµР№СЃС‹ СЂРµРїРѕР·РёС‚РѕСЂРёРµРІ, use cases Рё Р»РѕРєР°Р»СЊРЅС‹Рµ Р·Р°РіР»СѓС€РєРё С‡РµСЂРµР· Koin.

### Р‘С‹СЃС‚СЂС‹Р№ СЃС‚Р°СЂС‚ (Android)
1) РўСЂРµР±РѕРІР°РЅРёСЏ
- Android Studio Koala+ / JDK 17
- Android SDK 24+

2) РЎР±РѕСЂРєР°
- Windows: `gradlew.bat :app-android:assembleDebug`
- macOS/Linux: `./gradlew :app-android:assembleDebug`

3) Р—Р°РїСѓСЃРє РЅР° СѓСЃС‚СЂРѕР№СЃС‚РІРµ/СЌРјСѓР»СЏС‚РѕСЂРµ
- `:app-android:installDebug`
- Р—Р°РїСѓСЃРєР°Р№С‚Рµ `MainActivity`

РџСЂРёРјРµС‡Р°РЅРёРµ: РїСЂРµРґСѓРїСЂРµР¶РґРµРЅРёСЏ KMM/AGP РїРѕРґР°РІР»РµРЅС‹ С„Р»Р°РіР°РјРё РІ `gradle.properties`:
```
kotlin.mpp.androidGradlePluginCompatibility.nowarn=true
kotlin.native.ignoreDisabledTargets=true
```

### РљРѕРЅС„РёРіСѓСЂР°С†РёСЏ API
- Р‘Р°Р·РѕРІС‹Р№ URL Р·Р°РґР°С‘С‚СЃСЏ РІ `app-android/src/main/java/com/vtempe/VTempeApp.kt`:
  `modules(DI.coreModule(apiBaseUrl = "https://api.example.com"))`
- Р РµР°Р»СЊРЅС‹Рµ СЂРµР°Р»РёР·Р°С†РёРё СЂРµРїРѕР·РёС‚РѕСЂРёРµРІ РјРѕР¶РЅРѕ РїРѕРґРєР»СЋС‡РёС‚СЊ РІРјРµСЃС‚Рѕ Р·Р°РіР»СѓС€РµРє РІ `shared`.

### РЎРєСЂРёРЅС€РѕС‚С‹
Р”РѕР±Р°РІСЊС‚Рµ РІ `docs/screenshots/` Рё РІСЃС‚Р°РІСЊС‚Рµ СЃСЋРґР°:
- Home (С‚РµР»РµС„РѕРЅ)
- Home (РїР»Р°РЅС€РµС‚ / NavigationRail)
- Onboarding, Workout, Nutrition

РџСЂРёРјРµСЂ РІСЃС‚Р°РІРєРё:
```
![Home Phone](docs/screenshots/home_phone.png)
![Home Tablet](docs/screenshots/home_tablet.png)
```

### Roadmap
- Р”Р°РЅРЅС‹Рµ: СЂРµРїРѕР·РёС‚РѕСЂРёРё -> СЂРµР°Р»СЊРЅС‹Рµ РёСЃС‚РѕС‡РЅРёРєРё (Ktor + SQLDelight), РѕС„С„Р»Р°Р№РЅвЂ‘РєСЌС€
- РџРѕРєСѓРїРєРё: Billing + РїРѕР»РЅРѕС†РµРЅРЅС‹Р№ С„Р»РѕСѓ РїРѕРґРїРёСЃРєРё
- Health Connect: РёРјРїРѕСЂС‚ СЃРЅР°/Р°РєС‚РёРІРЅРѕСЃС‚Рё
- Р”РёР·Р°Р№РЅвЂ‘СЃРёСЃС‚РµРјР°: РІС‹РґРµР»РёС‚СЊ `:core:designsystem` РјРѕРґСѓР»СЊ
- Р›РѕРєР°Р»РёР·Р°С†РёСЏ: РІС‹РЅРµСЃС‚Рё СЃС‚СЂРѕРєРё Рё РґРѕР±Р°РІРёС‚СЊ `values-ru/values-en`
- РўРµСЃС‚С‹: unit (usecases), UI (Compose), РёРЅС‚РµРіСЂР°С†РёРѕРЅРЅС‹Рµ РґР»СЏ СЂРµРїРѕР·РёС‚РѕСЂРёРµРІ
- CI/CD: GitHub Actions, РїРѕРґРїРёСЃСЊ, СЂРµР»РёР·РЅС‹Рµ Р°СЂС‚РµС„Р°РєС‚С‹

### Р’РєР»Р°Рґ
PR Рё РѕР±СЃСѓР¶РґРµРЅРёСЏ РїСЂРёРІРµС‚СЃС‚РІСѓСЋС‚СЃСЏ. РџСЂРµРґР»Р°РіР°Р№С‚Рµ СѓР»СѓС‡С€РµРЅРёСЏ РїРѕ Р°СЂС…РёС‚РµРєС‚СѓСЂРµ Рё UX.

### Р‘С‹СЃС‚СЂС‹Рµ СЃСЃС‹Р»РєРё РЅР° РєР»СЋС‡РµРІС‹Рµ С„Р°Р№Р»С‹
- Android РєРѕСЂРµРЅСЊ: `app-android/src/main/java/com/vtempe/MainActivity.kt:1`
- DI (Android ViewModel): `app-android/src/main/java/com/vtempe/di/AppModule.kt:1`
- Р РѕСѓС‚С‹: `app-android/src/main/java/com/vtempe/ui/navigation/Routes.kt:1`
- Р­РєСЂР°РЅ РћРЅР±РѕСЂРґРёРЅРіР°: `app-android/src/main/java/com/vtempe/ui/screens/OnboardingScreen.kt:1`
- ViewModels: `app-android/src/main/java/com/vtempe/ui/vm/`
- UseCases (KMM): `shared/src/commonMain/kotlin/com/vtempe/shared/domain/usecase/UseCases.kt:1`





