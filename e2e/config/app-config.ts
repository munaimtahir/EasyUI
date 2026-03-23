import path from "node:path";
import { fileURLToPath } from "node:url";

const currentDir = path.dirname(fileURLToPath(import.meta.url));

export const repoRoot = path.resolve(currentDir, "..", "..");
export const e2eRoot = path.resolve(repoRoot, "e2e");

export const appConfig = {
  packageName: "com.easyui.launcher.debug",
  releasePackageName: "com.easyui.launcher",
  launcherActivity: "com.easyui.launcher.MainActivity",
  appLabel: "EasyUI Senior Launcher",
  deviceSerial: process.env.EASYUI_DEVICE_SERIAL ?? "34081500040008N",
  gradleBuildCommand: ["./gradlew", ":app:assembleDebug", ":app:testDebugUnitTest"],
  gradleStaticCommand: ["./gradlew", ":app:lintDebug"],
  apkPath: path.resolve(repoRoot, "app/build/outputs/apk/debug/app-debug.apk"),
  defaultWaitMs: 4_000,
  longPressMs: 3_200,
  runRoot: path.resolve(repoRoot, "device_test_runs"),
  reportsDir: path.resolve(repoRoot, "device_test_runs"),
};

export function getComponentName(): string {
  return `${appConfig.packageName}/${appConfig.launcherActivity}`;
}
