import { appConfig, getComponentName } from "../config/app-config.js";
import { runAdb } from "./adb.js";

export function pressHome(serial: string): void {
  runAdb(serial, ["shell", "input", "keyevent", "KEYCODE_HOME"]);
}

export function pressBack(serial: string): void {
  runAdb(serial, ["shell", "input", "keyevent", "KEYCODE_BACK"]);
}

export function openRecents(serial: string): void {
  runAdb(serial, ["shell", "input", "keyevent", "KEYCODE_APP_SWITCH"]);
}

export function tap(serial: string, x: number, y: number): void {
  runAdb(serial, ["shell", "input", "tap", String(x), String(y)]);
}

export function longPress(serial: string, x: number, y: number, durationMs = appConfig.longPressMs): void {
  runAdb(serial, ["shell", "input", "swipe", String(x), String(y), String(x), String(y), String(durationMs)]);
}

export function typeText(serial: string, text: string): void {
  runAdb(serial, ["shell", "input", "text", text.replaceAll(" ", "%s")]);
}

export function launchApp(serial: string): void {
  for (let attempt = 0; attempt < 4; attempt += 1) {
    runAdb(serial, ["shell", "am", "start", "-W", "-n", getComponentName()], { allowFailure: true });
    if (isEasyUiForeground(serial)) {
      return;
    }
    sleepSync(700);
  }
}

export function forceStopApp(serial: string): void {
  runAdb(serial, ["shell", "am", "force-stop", appConfig.packageName], { allowFailure: true });
}

export function clearAppData(serial: string): void {
  runAdb(serial, ["shell", "pm", "clear", appConfig.packageName], { allowFailure: true });
}

function isEasyUiForeground(serial: string): boolean {
  const focusedWindow = runAdb(serial, ["shell", "dumpsys", "window", "windows"], { allowFailure: true });
  const windowLines = focusedWindow
    .split("\n")
    .filter((line) => line.includes("mCurrentFocus") || line.includes("mFocusedApp"));
  if (windowLines.some((line) => line.includes(appConfig.packageName) || line.includes(getComponentName()))) {
    return true;
  }
  const focusedActivity = runAdb(serial, ["shell", "dumpsys", "activity", "activities"], { allowFailure: true });
  const activityLines = focusedActivity
    .split("\n")
    .filter((line) =>
      line.includes("topResumedActivity") ||
      line.includes("ResumedActivity") ||
      line.includes("mResumedActivity"),
    );
  return activityLines.some((line) => line.includes(appConfig.packageName) || line.includes(getComponentName()));
}

function sleepSync(ms: number): void {
  Atomics.wait(new Int32Array(new SharedArrayBuffer(4)), 0, 0, ms);
}
