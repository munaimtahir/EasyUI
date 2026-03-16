# Dependency Warning Triage

Lint reported 19 warnings. Eighteen are dependency freshness warnings from [app/build.gradle.kts](/home/munaim/srv/apps/easyui/app/build.gradle.kts). One is a non-blocking `ObsoleteSdkInt` warning for adaptive icon resources in `mipmap-anydpi-v26`, which is being kept because the release build resolved correctly with that layout.

## Triage

- `androidx.core:core-ktx` `1.13.1 -> 1.18.0`
  - Severity: medium
  - Release blocking: no
  - Recommended action: defer post-launch
  - Reason: large jump across AndroidX core behavior surface

- `androidx.appcompat:appcompat` `1.7.0 -> 1.7.1`
  - Severity: low
  - Release blocking: no
  - Recommended action: safe quick upgrade before release if there is spare time, otherwise defer
  - Reason: patch-level update

- `androidx.activity:activity-compose` `1.9.2 -> 1.13.0`
  - Severity: medium
  - Release blocking: no
  - Recommended action: defer post-launch
  - Reason: large jump in Compose activity integration

- `androidx.lifecycle:lifecycle-runtime-ktx` `2.8.5 -> 2.10.0`
  - Severity: medium
  - Release blocking: no
  - Recommended action: defer post-launch
  - Reason: coordinated lifecycle stack update required

- `androidx.lifecycle:lifecycle-runtime-compose` `2.8.5 -> 2.10.0`
  - Severity: medium
  - Release blocking: no
  - Recommended action: defer post-launch
  - Reason: should move with the rest of the lifecycle stack

- `androidx.lifecycle:lifecycle-viewmodel-compose` `2.8.5 -> 2.10.0`
  - Severity: medium
  - Release blocking: no
  - Recommended action: defer post-launch
  - Reason: should move with the rest of the lifecycle stack

- `androidx.navigation:navigation-compose` `2.8.0 -> 2.9.7`
  - Severity: medium
  - Release blocking: no
  - Recommended action: defer post-launch
  - Reason: navigation upgrades can change runtime behavior and deep-link handling

- `androidx.compose.ui:ui` `1.7.1 -> 1.10.5`
  - Severity: medium
  - Release blocking: no
  - Recommended action: defer post-launch
  - Reason: large Compose stack move and compiler compatibility review needed

- `androidx.compose.foundation:foundation` `1.7.1 -> 1.10.5`
  - Severity: medium
  - Release blocking: no
  - Recommended action: defer post-launch
  - Reason: must move with the rest of Compose

- `androidx.compose.ui:ui-tooling-preview` `1.7.1 -> 1.10.5`
  - Severity: low
  - Release blocking: no
  - Recommended action: defer post-launch
  - Reason: tied to the main Compose version set

- `androidx.compose.material3:material3` `1.3.0 -> 1.4.0`
  - Severity: medium
  - Release blocking: no
  - Recommended action: defer post-launch
  - Reason: should move with the Compose stack, not alone under release pressure

- `com.google.android.material:material` `1.12.0 -> 1.13.0`
  - Severity: low
  - Release blocking: no
  - Recommended action: safe quick upgrade before release if desired, otherwise defer
  - Reason: patch/minor update and low app risk

- `org.jetbrains.kotlinx:kotlinx-coroutines-android` `1.8.1 -> 1.10.2`
  - Severity: medium
  - Release blocking: no
  - Recommended action: defer post-launch
  - Reason: concurrency/runtime changes deserve isolated validation

- `androidx.compose.ui:ui-tooling` `1.7.1 -> 1.10.5`
  - Severity: low
  - Release blocking: no
  - Recommended action: defer post-launch
  - Reason: debug-only dependency tied to Compose version family

- `androidx.compose.ui:ui-test-manifest` `1.7.1 -> 1.10.5`
  - Severity: low
  - Release blocking: no
  - Recommended action: defer post-launch
  - Reason: debug/test-only and tied to Compose version family

- `androidx.test.ext:junit` `1.2.1 -> 1.3.0`
  - Severity: low
  - Release blocking: no
  - Recommended action: safe quick upgrade before release if test-only churn is acceptable, otherwise defer
  - Reason: instrumentation-test-only dependency

- `androidx.test.espresso:espresso-core` `3.6.1 -> 3.7.0`
  - Severity: low
  - Release blocking: no
  - Recommended action: safe quick upgrade before release if test-only churn is acceptable, otherwise defer
  - Reason: instrumentation-test-only dependency

- `androidx.compose.ui:ui-test-junit4` `1.7.1 -> 1.10.5`
  - Severity: low
  - Release blocking: no
  - Recommended action: defer post-launch
  - Reason: test dependency tied to the broader Compose stack

## Release recommendation

Do not churn the dependency stack immediately before the first Play submission. The current build has already passed release packaging, unit tests, and lint. Only patch-level low-risk upgrades should even be considered before launch, and none are required for Play approval.
