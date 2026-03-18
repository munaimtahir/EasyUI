# Skin System

## Overview

EasyUI now uses a 3-layer Skin System to separate behavior, visual design, and accessibility:

1. **Layout Behavior** (`LayoutMode`)
2. **Visual Theme** (`VisualTheme`)
3. **Accessibility Overrides** (`AccessibilityMode`)

All three layers are stored in `SkinConfig` and resolved by `SkinManager`.

## Architecture

### Domain model

- `core/domain/model/SkinConfig.kt`
  - `SkinConfig(layoutMode, visualTheme, accessibilityMode)`
  - `LayoutMode`: `SIMPLE_CLASSIC`, `VERY_SIMPLE`, `CARE_MODE`, `COMMUNICATION_MODE`
  - `VisualTheme`: `LIGHT_PREMIUM`, `DARK_COMFORT`, `CLINICAL_PROFESSIONAL`, `SOFT_CALM`
  - `AccessibilityMode`: `NONE`, `HIGH_CONTRAST`, `BOLD_ACCESSIBILITY`

### Resolver/engine

- `core/ui/theme/SkinManager.kt`
  - `getColors(): ColorPalette`
  - `getTypography(): TypographySet`
  - `getSpacing(): SpacingSet`
  - `getTileStyle(): TileStyle`
  - `getLayoutConfig(): LayoutConfig`

### Theme/layout tokens

- `core/ui/theme/ColorPalette.kt`
- `core/ui/theme/TypographySet.kt`
- `core/ui/theme/SpacingSet.kt`
- `core/ui/theme/SkinManager.kt` (`LayoutConfig`, `TileStyle`, `EmphasisMode`)

## Merge order

`SkinManager` resolves tokens in this deterministic order:

1. Base theme (`LIGHT_PREMIUM`)
2. Visual theme override
3. Layout mode adjustments
4. Accessibility override

This ensures stable and conflict-free output.

## Persistence

Skin state is persisted in launcher settings:

- `LauncherSettings.skinConfig`
- DataStore keys:
  - `skin_layout_mode`
  - `skin_visual_theme`
  - `skin_accessibility_mode`
- Repository API:
  - `setSkinConfig(config: SkinConfig)`
  - `getSkinConfig(): SkinConfig`

Legacy fallback behavior:

- If skin keys are missing, `VERY_SIMPLE` is inferred from previous `verySimpleModeEnabled` value.

## UI integration

### Home

- `HomeViewModel` exposes `skinConfig` via `HomeUiState`.
- `HomeScreen` creates `SkinManager(skinConfig)` and applies resolved tokens to:
  - top bar
  - tile colors/typography/spacing
  - tile emphasis behavior
  - layout behavior settings (`gridRows`, `gridCols`, `tileSizeScale`, `showLabels`, `emphasisMode`)

### Caregiver settings

- `LayoutPagesScreen` now includes:
  - Layout mode selection
  - Visual theme selection
  - Accessibility mode selection
- `CaregiverViewModel` writes updates through `setSkinConfig`.

## Adding a new skin

1. Add enum entry in `VisualTheme` (or `LayoutMode` / `AccessibilityMode`).
2. Add resolver branch in `SkinManager.resolve`.
3. Add label/body mapping in caregiver screen helpers.
4. Add tests for selection and expected visual/layout behavior.

No UI refactor should be needed because composables consume resolved token sets, not hardcoded values.
