import { runAdb } from "./adb.js";

export type DeviceInfo = {
  serial: string;
  manufacturer: string;
  model: string;
  androidVersion: string;
  sdkLevel: string;
  resolution: string;
  density: string;
};

export function assertDeviceConnected(serial: string): void {
  const devices = runAdb(serial, ["get-state"]);
  if (!devices.includes("device")) {
    throw new Error(`ADB device ${serial} is not available`);
  }
}

export function getDeviceInfo(serial: string): DeviceInfo {
  return {
    serial,
    manufacturer: runAdb(serial, ["shell", "getprop", "ro.product.manufacturer"]),
    model: runAdb(serial, ["shell", "getprop", "ro.product.model"]),
    androidVersion: runAdb(serial, ["shell", "getprop", "ro.build.version.release"]),
    sdkLevel: runAdb(serial, ["shell", "getprop", "ro.build.version.sdk"]),
    resolution: runAdb(serial, ["shell", "wm", "size"]).replace("Physical size: ", ""),
    density: runAdb(serial, ["shell", "wm", "density"]).replace("Physical density: ", ""),
  };
}

export function unlockScreen(serial: string): void {
  runAdb(serial, ["shell", "input", "keyevent", "KEYCODE_WAKEUP"], { allowFailure: true });
  runAdb(serial, ["shell", "wm", "dismiss-keyguard"], { allowFailure: true });
  runAdb(serial, ["shell", "input", "swipe", "540", "2100", "540", "400", "250"], { allowFailure: true });
}

export function setAirplaneMode(serial: string, enabled: boolean): void {
  const value = enabled ? "1" : "0";
  runAdb(serial, ["shell", "settings", "put", "global", "airplane_mode_on", value], { allowFailure: true });
  runAdb(serial, [
    "shell",
    "am",
    "broadcast",
    "-a",
    "android.intent.action.AIRPLANE_MODE",
    "--ez",
    "state",
    enabled ? "true" : "false",
  ], { allowFailure: true });
}
