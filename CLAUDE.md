# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device/emulator
./gradlew installDebug

# Launch on emulator (after install)
adb shell am start -n com.mirror.app/.MainActivity

# Clean build
./gradlew clean assembleDebug
```

There are no tests yet. `local.properties` must exist with `sdk.dir` pointing to the Android SDK (not committed to git).

## Architecture

**No DI framework.** All dependencies are wired manually in `AppContainer.kt`, which is created once in `MirrorApp` (the `Application` subclass) and passed down to every ViewModel factory via `NavGraph`.

### Layer structure

```
data/
  local/          — Room database: MoodEntryEntity, ScarEntity, DAOs, Converters
  preferences/    — DataStore: UserPreferencesDataStore (avatar type, notif prefs, onboarding flag)
  repository/     — MoodRepositoryImpl bridges DAOs to domain models

domain/
  model/          — Pure Kotlin data classes: MoodEntry, AvatarState, AvatarType, Scar, UserPreferences
  usecase/        — All business logic lives here

ui/
  avatar/         — Canvas renderers + AvatarCanvas dispatcher
  component/      — Shared composables (MirrorFrame, MoodSlider, MoodTimeline, etc.)
  screen/         — One folder per screen, each with Screen + ViewModel
  navigation/     — NavGraph + Screen sealed class
  theme/          — Color, Type, Theme
worker/           — DailyReminderWorker (WorkManager) + BootReceiver
```

### Key domain concepts

**AvatarState** is pure domain data — it has no knowledge of which avatar type is rendered. It contains `moodScore`, `healthScore`, `recentAvg`, `isHibernating`, `scars`, `hasLowMoodCounter`, `lowMoodDays`.

**AvatarType** (`TREE`, `ROBOT`, `WATER_BUCKET`, `PILE_OF_SPOONS`) is stored separately in `UserPreferences` (DataStore). The visualization layer `AvatarCanvas` takes both as independent parameters and dispatches to the appropriate renderer.

**healthScore** (0.0–1.0) is accumulated via a delta table applied to each entry in chronological order: score 1→−0.25, 2→−0.12, 3→+0.01, 4→+0.08, 5→+0.18. Clamped to [0,1].

**recentAvg** = 7-day trailing average of scores, normalized to 0.0–1.0 (divide by 5).

**Water bucket fill level** = `(recentAvg + healthScore) / 2`.

**Scars** form when a 14+ day window has a rolling 7-day average below 4.0. `ComputeScarUseCase` recomputes and replaces all scars after every save/edit.

**Hibernation** triggers when the gap between the last entry date and today exceeds 14 days. `HandleInactivityUseCase` backfills every absent day with `isBackfilled = true` using the new score, then saves today's entry.

### Navigation

`NavGraph` determines start destination by calling `userPreferences.first()` in a `LaunchedEffect` before rendering `NavHost`, avoiding the flicker of showing the wrong screen. The `CheckIn` route accepts either a date string (`yyyy-MM-dd`) or the literal `"reassessment"` for the hibernation return flow.

### Avatar renderers

Each renderer (`drawTree`, `drawRobot`, `drawWaterBucket`, `drawPileOfSpoons`) is a `DrawScope` extension function. They receive `AvatarState` + animation floats (`shimmer`, `wavePhase`) and derive all visual parameters from `healthScore`, `moodScore`, and `recentAvg`. `FaceRenderer.kt` provides shared `drawFace` and `drawHibernationOverlay` helpers used by all renderers.

### Icons

Only `material-icons-core` is included (no extended icons). Use `KeyboardArrowLeft`/`KeyboardArrowRight` instead of `ChevronLeft`/`ChevronRight`, and `DateRange` instead of `History`.
