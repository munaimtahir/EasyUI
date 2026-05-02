# Scrolling Layout Audit

## Issue
Many screens use `WizardShell`. The current implementation of `WizardShell` in `core/ui/src/main/java/com/easyui/core/ui/components/WizardShell.kt` uses:
```kotlin
Scaffold { innerPadding ->
    LazyColumn(...) {
        item { Header(...) }
        item { Column { content() } }
    }
}
```
This is an anti-pattern when `content()` uses `LazyColumn` or `weight()`. The parent `LazyColumn` measures its items with infinite height constraint, so `weight(1f)` is ignored (or causes a crash), and nested `LazyColumn`s measure all their items at once, losing virtualization, and not filling the screen as intended.

## Affected Screens
1. **WelcomeScreen**: Content might clip on very small screens because it is a simple Column inside the `item`. Wait, since parent is LazyColumn, it will scroll, but the structure is unsafe for screens that need filling.
2. **ProtectionOptionsScreen**: Scrolls via parent.
3. **ThemePickerScreen**: Scrolls via parent.
4. **PermissionsExplanationScreen**: Scrolls via parent.
5. **LauncherActivationScreen**: Scrolls via parent.
6. **ReadabilityPresetScreen**: Uses nested `LazyColumn`. Virtualization broken.
7. **HomeLayoutSetupScreen**: Contains `LazyVerticalGrid` with `userScrollEnabled = false`. Since parent is `LazyColumn`, the grid is measured fully, but if the content exceeds screen height, the whole page scrolls. But wait, if the whole page scrolls, why does the prompt say "page preview is not visible because the screen does not scroll properly"? Oh, because the `LazyVerticalGrid` has a fixed height `Modifier.height(180.dp)`! And `WizardShell` bottom button area might be overlapping if `Scaffold` inner padding isn't properly handled, or the `LazyVerticalGrid` height is too small.
8. **AllowedAppsSetupScreen**: Uses `Card` with `weight(1f)` containing a `LazyColumn`. Since parent `item` has infinite height, `weight(1f)` is ignored or evaluates to 0, or crashes. This explains why the main screen layout does not scroll, and only top 2 tiles are visible.
9. **ContactsSetupScreen**: Same weight issue with `LazyColumn(modifier = Modifier.weight(1f))`.
10. **SecuritySetupScreen**: Basic column, no nested lazy issue, but might have input method issues.
11. **DeviceSupportScreen**: Basic column.
12. **ReviewConfirmScreen**: Uses nested `LazyColumn`.

## Planned Fix
Rewrite `WizardShell` to implement **Preferred Pattern A**:
```kotlin
Scaffold(
   bottomBar = { Surface { BottomButtons(...) } }
) { innerPadding ->
    Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
        Column(modifier = Modifier.padding(...)) { Header(...) }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(...)
        ) {
            content()
        }
    }
}
```
For screens that currently use `LazyColumn` (like `ReadabilityPresetScreen`, `ReviewConfirmScreen`), we will change them to use standard `Column` with `.forEach` to emit items since they are small lists and now have a scrollable parent. For `AllowedAppsSetupScreen` and `ContactsSetupScreen`, we will also use simple `Column` since the list sizes are bounded (number of installed apps might be large though, wait. If installed apps is large, `Column` + `forEach` might be slow. If we need a `LazyColumn`, we can add a `scrollable` parameter to `WizardShell` to disable the parent `verticalScroll`).

Let's modify `WizardShell` to support `scrollableContent = true`.
If `scrollableContent = true`, `WizardShell` provides the `verticalScroll` on the content container.
If `scrollableContent = false`, `WizardShell` just provides a `Column` with `weight(1f)`, allowing the child to use `LazyColumn`.