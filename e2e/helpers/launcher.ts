import { appConfig } from "../config/app-config.js";
import { runAdb } from "./adb.js";

export function isDefaultLauncher(serial: string): boolean {
  const output = runAdb(serial, [
    "shell",
    "cmd",
    "package",
    "resolve-activity",
    "--brief",
    "-a",
    "android.intent.action.MAIN",
    "-c",
    "android.intent.category.HOME",
  ]);
  return output.includes(appConfig.packageName) || output.includes(appConfig.releasePackageName);
}

export function trySetDefaultLauncher(serial: string): string {
  return runAdb(
    serial,
    ["shell", "cmd", "role", "add-role-holder", "android.app.role.HOME", appConfig.packageName, "0"],
    { allowFailure: true },
  );
}
