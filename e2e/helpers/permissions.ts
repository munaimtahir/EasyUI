import { appConfig } from "../config/app-config.js";
import { runAdb } from "./adb.js";

export function grantDeclaredDangerousPermissions(serial: string): void {
  for (const permission of ["android.permission.CALL_PHONE", "android.permission.SEND_SMS"]) {
    runAdb(serial, ["shell", "pm", "grant", appConfig.packageName, permission], { allowFailure: true });
  }
}
