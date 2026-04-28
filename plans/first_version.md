# Mirror Android App — Implementation Plan

## Context
Build the Mirror mood-tracking Android app from scratch. The repo is currently empty (git init + spec only at `specs/ProductSpec.md`). All data stored locally using Room + DataStore. No remote services, no DI framework — a manual `AppContainer` singleton wires everything.

The key architectural principle: **`AvatarState` is pure domain data with no knowledge of which avatar is selected.** The visualization layer receives `AvatarState` + `AvatarType` together and each renderer independently derives its visual parameters from the generic values.

---

## Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose (Material 3, dark theme)
- **Data**: Room (mood entries + scars), DataStore Preferences (user prefs)
- **DI**: Manual `AppContainer` singleton
- **Navigation**: Compose Navigation
- **Background work**: WorkManager (daily notifications)
- **Min SDK**: 26 / Target SDK: 35

---

## Package: `com.mirror.app`

---

## Gradle Files

### `/settings.gradle.kts`
Standard Android settings, includes `:app`, project name "Mirror".

### `/gradle/libs.versions.toml`
```toml
[versions]
agp = "8.7.3"
kotlin = "2.1.0"
ksp = "2.1.0-1.0.29"
composeBom = "2025.02.00"
room = "2.6.1"
datastore = "1.1.3"
workmanager = "2.10.0"
navigationCompose = "2.8.9"
accompanistPermissions = "0.36.0"
kotlinxSerializationJson = "1.7.3"
```

### `/build.gradle.kts` (root)
Plugin aliases only, `apply false`.

### `/app/build.gradle.kts`
Plugins: `android-application`, `kotlin-android`, `kotlin-compose`, `kotlin-serialization`, `ksp` (Room annotation processor only). No Hilt.

---

## Data Models

### Domain models (`domain/model/`)

**`AvatarState`** — pure data, no avatar-type knowledge, no Compose imports:
```kotlin
data class AvatarState(
    val moodScore: Int,            // today's score 1–5; 0 if no entry today
    val healthScore: Float,        // 0.0–1.0 accumulated trend
    val isHibernating: Boolean,    // true if latest entry is >14 days ago
    val scars: List<Scar>,
    val hasLowMoodCounter: Boolean,
    val lowMoodDays: Int           // consecutive days where rolling 7-day avg < 4.0
)
```

**`AvatarType`** — enum stored in DataStore, owned by UserPreferences:
```kotlin
enum class AvatarType { TREE, ROBOT, WATER_BUCKET, PILE_OF_SPOONS }
```

**`Scar`**: `startDate: LocalDate`, `endDate: LocalDate`

**`MoodEntry`**: `date: LocalDate`, `score: Int`, `keywords: List<String>`, `isBackfilled: Boolean`

**`UserPreferences`**: `onboardingComplete: Boolean`, `avatarType: AvatarType`, `notifHour: Int`, `notifMinute: Int`, `notifEnabled: Boolean`

---

## Architecture Flow

```
DataStore ──► UserPreferences ──────────────────────────────────────┐
                                                                    ▼
Room ──► MoodRepository ──► ComputeAvatarStateUseCase ──► AvatarState
                                                                    │
                                              AvatarType ───────────┤
                                                                    ▼
                                                           AvatarCanvas
                                                         (Compose UI layer)
                                                         dispatches to renderer:
                                                         TreeAvatarRenderer(state, type)
                                                         RobotAvatarRenderer(state, type)
                                                         WaterBucketAvatarRenderer(state, type)
                                                         PileOfSpoonsAvatarRenderer(state, type)
```

Each renderer receives `AvatarState` and derives all visual parameters locally:
- `healthScore` → leaf density/color, rust alpha, water color/level, spoon count
- `moodScore` → face expression
- `isHibernating` → cobweb overlay (all renderers)
- `scars` → scar dot positions (all renderers)

**`AvatarType` is never inside `AvatarState`.** It travels alongside it as a separate parameter.

---

## Core Business Logic: `ComputeAvatarStateUseCase`

Pure function — takes `List<MoodEntry>`, returns `AvatarState`.

**Health score** — accumulated day-by-day from first entry (normalized initial score → 0.0..1.0):

| Score | Delta |
|-------|-------|
| 1 | −0.25 |
| 2 | −0.12 |
| 3 | +0.01 |
| 4 | +0.08 |
| 5 | +0.18 |

Clamped to [0.0, 1.0].

**Low mood tracking**: Rolling 7-day average. Count consecutive days where this average is < 4.0. If `lowMoodDays >= 14` → `hasLowMoodCounter = true`.

**Hibernation**: `LocalDate.now()` vs `latestEntry.date`. Gap > 14 days → `isHibernating = true`.

**Scars** (`ComputeScarUseCase`): Any contiguous window where rolling 7-day avg stayed < 4.0 for ≥ 14 days becomes a `Scar(startDate, endDate)`. Persisted to `ScarEntity` in Room.

---

## Visual Parameter Derivation (per renderer)

Each renderer computes its own visual params from the generic values. No shared "visual state" data class needed.

| Avatar | Derived from |
|--------|-------------|
| **Tree** | `healthScore` → leaf count (lerp 3..20), leaf color (green→yellow), canopy radius |
| **Robot** | `healthScore` → rust alpha (`1f - healthScore`), shine visible if > 0.8 |
| **Water Bucket** | `healthScore` → water color (lerp murkyBrown→cleanBlue); water level = `(recentAvg + healthScore) / 2` so short-term mood and long-term trend both affect fill |
| **Pile of Spoons** | `healthScore` → spoon count (`max(1, (healthScore * 10).roundToInt())`) |

All: `moodScore` → face expression via shared `drawFace(center, radius, moodScore)` helper.
All: `isHibernating` → semi-transparent grey + cobweb Path overlay.
All: `scars` → tappable dot(s) at fixed avatar-specific anchor points.

> **`recentAvg`** is the 7-day trailing average of scores, normalized 0.0–1.0. It lives in `AvatarState` so all renderers can use it without extra plumbing — water bucket uses it directly for level; other renderers may use it for secondary effects if desired.

**Revised `AvatarState`:**
```kotlin
data class AvatarState(
    val moodScore: Int,
    val healthScore: Float,
    val recentAvg: Float,          // 7-day trailing average normalized to 0.0–1.0
    val isHibernating: Boolean,
    val scars: List<Scar>,
    val hasLowMoodCounter: Boolean,
    val lowMoodDays: Int
)
```

---

## Project Structure

```
app/src/main/java/com/mirror/app/
├── MirrorApp.kt
├── AppContainer.kt                    singleton: db, repos, use cases
├── MainActivity.kt                    single activity, sets up NavHost
├── data/
│   ├── local/
│   │   ├── MirrorDatabase.kt
│   │   ├── dao/MoodEntryDao.kt
│   │   ├── dao/ScarDao.kt
│   │   ├── entity/MoodEntryEntity.kt  PK = date string; score, keywords JSON, isBackfilled, timestamps
│   │   ├── entity/ScarEntity.kt       id, startDate, endDate, createdAt
│   │   └── converter/Converters.kt    List<String> ↔ JSON
│   ├── preferences/
│   │   └── UserPreferencesDataStore.kt
│   └── repository/
│       ├── MoodRepository.kt
│       └── MoodRepositoryImpl.kt
├── domain/
│   ├── model/
│   │   ├── AvatarState.kt
│   │   ├── AvatarType.kt
│   │   ├── MoodEntry.kt
│   │   ├── Scar.kt
│   │   └── UserPreferences.kt
│   └── usecase/
│       ├── ComputeAvatarStateUseCase.kt
│       ├── ComputeScarUseCase.kt
│       ├── HandleInactivityUseCase.kt
│       ├── GetKeywordSuggestionsUseCase.kt
│       ├── GetAvatarMessageUseCase.kt
│       ├── GetRecentEntriesUseCase.kt
│       ├── SaveMoodEntryUseCase.kt
│       └── EditMoodEntryUseCase.kt
├── ui/
│   ├── theme/Color.kt, Theme.kt, Type.kt
│   ├── navigation/Screen.kt, NavGraph.kt
│   ├── component/
│   │   ├── MirrorFrame.kt
│   │   ├── MoodSlider.kt
│   │   ├── MoodTimeline.kt
│   │   ├── KeywordChip.kt
│   │   └── ScarBadge.kt
│   ├── avatar/
│   │   ├── AvatarCanvas.kt            dispatches on AvatarType; hosts animations
│   │   ├── TreeAvatarRenderer.kt      DrawScope extensions; derives visuals from AvatarState
│   │   ├── RobotAvatarRenderer.kt
│   │   ├── WaterBucketAvatarRenderer.kt
│   │   └── PileOfSpoonsAvatarRenderer.kt
│   └── screen/
│       ├── onboarding/OnboardingScreen.kt + OnboardingViewModel.kt
│       ├── home/HomeScreen.kt + HomeViewModel.kt
│       ├── checkin/CheckInScreen.kt + CheckInViewModel.kt
│       ├── history/HistoryScreen.kt + HistoryViewModel.kt
│       └── settings/SettingsScreen.kt + SettingsViewModel.kt
└── worker/
    ├── DailyReminderWorker.kt         plain CoroutineWorker; no Hilt
    └── BootReceiver.kt
```

---

## Key Screens

### Onboarding (4 pages via HorizontalPager)
1. Animated expanding circle + "Take three deep breaths"
2. `MoodSlider` — initial mood rating
3. 2×2 avatar picker grid with mini `AvatarCanvas(state, avatarType)` previews
4. Explanation + "Let's go" CTA
   → Saves `AvatarType` + initial score to DataStore; sets `onboardingComplete = true`

### Home
- `MirrorFrame` containing `AvatarCanvas(avatarState, avatarType)` — both sourced from ViewModel
- Dynamic message text from `GetAvatarMessageUseCase(avatarState, avatarType)`
- "How are you feeling?" CTA (hidden/becomes "Edit" if today already entered)
- `MoodTimeline` — last 7 days
- Hibernation banner if `isHibernating` → tapping prompts reassessment flow
- Low mood banner showing days count if `hasLowMoodCounter`

### Hibernation return flow (`HandleInactivityUseCase`)
When `isHibernating = true` and user taps the banner, they're taken through a mini reassessment (Onboarding page 2 reused). On confirm, `HandleInactivityUseCase` bulk-inserts a `MoodEntry(score = newScore, isBackfilled = true)` for **every calendar day** between `lastEntry.date + 1` and `LocalDate.now() - 1` (i.e., all the days the user was actually absent), then recomputes `AvatarState`. Does **not** reset avatar type or historical data before that window.

Backfilled entries are visually distinguished in all history views:
- **Slider tab**: dim overlay or "Estimated" label on the page
- **Calendar tab**: score dot rendered with reduced opacity or a dashed border

### Check-In
- Date header, `MoodSlider`, keyword `FlowRow`, Save

### History (TabRow)
- **Slider tab**: HorizontalPager; each page shows `AvatarCanvas(historicalState, avatarType)` + keywords for that date
- **Calendar tab**: month grid with colored score dots; tap cell → navigate to `CheckIn/{date}` for view/edit

### Settings
- Notification time (`TimePicker` dialog) + enable/disable `Switch`
- Avatar type selector (can change at any time — no data migration needed, only UserPreferences updated)
- On notification change → re-enqueue `PeriodicWorkRequest` with recalculated initial delay

---

## Notifications

`DailyReminderWorker` is a plain `CoroutineWorker` (no Hilt). Gets `AppContainer` via `(applicationContext as MirrorApp).container`.

Scheduled as `PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)` with `initialDelay` = milliseconds until next occurrence of user's chosen `notifHour:notifMinute`. Uses `ExistingPeriodicWorkPolicy.UPDATE` so Settings changes reschedule cleanly.

`BootReceiver` reads DataStore and re-enqueues on device reboot.

---

## Theme

Dark palette:
- Background: `Color(0xFF1A1A2E)` deep charcoal-navy
- Mirror frame accent: `Color(0xFFD4AF37)` gold/brass
- Surface: `Color(0xFF16213E)`
- Per-avatar accent colors live in the renderer files, not in `AvatarState`

---

## Implementation Phases

```
Phase 1: Build System & Data Layer
  settings.gradle.kts, build.gradle.kts (root + app), libs.versions.toml
  AndroidManifest.xml, MirrorApp.kt, AppContainer.kt, MainActivity.kt
  Entities → DAOs → MirrorDatabase → UserPreferencesDataStore → MoodRepositoryImpl

Phase 2: Domain Logic (no Android deps, pure Kotlin)
  Domain models (AvatarState, AvatarType, MoodEntry, Scar, UserPreferences)
  All use cases: ComputeAvatarStateUseCase (trend math), ComputeScarUseCase,
  HandleInactivityUseCase, GetKeywordSuggestionsUseCase, GetAvatarMessageUseCase,
  GetRecentEntriesUseCase, SaveMoodEntryUseCase, EditMoodEntryUseCase

Phase 3: Navigation & Shared Components
  Screen.kt, NavGraph.kt
  MoodSlider, MirrorFrame (Canvas), KeywordChip, ScarBadge, MoodTimeline
  Theme.kt, Color.kt, Type.kt

Phase 4: Avatar Canvas
  AvatarCanvas.kt (dispatch + infiniteTransition host)
  All 4 renderers — static first, then wave/shimmer animations

Phase 5: Screens & ViewModels (Onboarding → Home → CheckIn → History → Settings)

Phase 6: Notifications & Edge Cases
  DailyReminderWorker, BootReceiver, POST_NOTIFICATIONS permission flow,
  hibernation return flow, scar tap dialog
```

---

## Verification
1. `./gradlew assembleDebug` — clean compile
2. Fresh install: 4-page onboarding → Home with chosen avatar
3. Avatar type change in Settings → Home immediately reflects new avatar, same `AvatarState`
4. Check-in: enter mood → timeline + avatar update
5. Trend: enter score 1 several days → `healthScore` drops → all 4 avatar types degrade
6. Scar: insert past entries spanning 14+ low-mood days → scar dot appears → tap → dialog shows date range
7. Hibernation: latest entry > 14 days ago → cobweb overlay → reassessment → every absent day backfilled with `isBackfilled = true`; history slider/calendar shows them as estimated
8. Notifications: set time in Settings → `WorkManager` task enqueued with correct delay
9. History slider: confirm each page shows avatar rendered from that day's historical `AvatarState` + current `AvatarType`