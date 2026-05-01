import { test } from "@playwright/test";
import { scenarioMatrix } from "../../config/scenario-matrix.js";
import { appConfig } from "../../config/app-config.js";
import { forceStopApp, launchApp, clearAppData } from "../../helpers/actions.js";
import { assertDeviceConnected, unlockScreen } from "../../helpers/device.js";
import { withScenario } from "../../helpers/test-runner.js";
import { dumpUiForEasyUi } from "../../helpers/ui-dump.js";
import { xmlContains } from "../../helpers/selectors.js";
import { runAdb } from "../../helpers/adb.js";
import { grantDeclaredDangerousPermissions } from "../../helpers/permissions.js";
import { ensureOnboardingFinished } from "../../helpers/onboarding.js";

test("B1 fresh install and first-run flow", async ({}, testInfo) => {
  const serial = appConfig.deviceSerial;
  assertDeviceConnected(serial);
  await withScenario(testInfo, serial, scenarioMatrix.find((item) => item.id === "B1")!, async () => {
    runAdb(serial, ["uninstall", appConfig.packageName], { allowFailure: true });
    runAdb(serial, ["install", "-r", "-t", "-g", appConfig.apkPath]);
    grantDeclaredDangerousPermissions(serial);
    clearAppData(serial);
    unlockScreen(serial);
    let xml = dumpUiForEasyUi(serial, `${process.env.EASYUI_RUN_DIR}/ui_dumps/b1-first-run.xml`);
    if (
      !(
        xmlContains(xml, "Welcome to EasyUI") ||
        xmlContains(xml, "Set EasyUI as Home") ||
        xmlContains(xml, "home_screen") ||
        xmlContains(xml, "Phone") ||
        xmlContains(xml, "Emergency")
      )
    ) {
      throw new Error("Fresh install did not land on an expected EasyUI surface.");
    }
    const onboardingResult = await ensureOnboardingFinished(serial, `${process.env.EASYUI_RUN_DIR}/ui_dumps/b1-onboarding`);
    if (onboardingResult !== "home") {
      throw new Error("Onboarding flow did not reach the home screen.");
    }
    forceStopApp(serial);
    xml = dumpUiForEasyUi(serial, `${process.env.EASYUI_RUN_DIR}/ui_dumps/b1-first-run-relaunch.xml`);
    if (!(xmlContains(xml, "home_screen") || xmlContains(xml, "Phone") || xmlContains(xml, "Emergency"))) {
      throw new Error("Home screen was not visible after relaunch.");
    }
    return { note: "Fresh install succeeded, onboarding completed, and relaunch returned to the home screen." };
  });
});
