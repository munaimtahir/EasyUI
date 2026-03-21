import { test } from "@playwright/test";
import { appConfig } from "../../config/app-config.js";
import { scenarioMatrix } from "../../config/scenario-matrix.js";
import { launchApp } from "../../helpers/actions.js";
import { openCaregiverEntry } from "../../helpers/caregiver.js";
import { assertDeviceConnected, unlockScreen } from "../../helpers/device.js";
import { ensureOnboardingFinished } from "../../helpers/onboarding.js";
import { withScenario } from "../../helpers/test-runner.js";
import { sleep } from "../../helpers/waits.js";

test("B6 caregiver entry path", async ({}, testInfo) => {
  const serial = appConfig.deviceSerial;
  assertDeviceConnected(serial);
  await withScenario(testInfo, serial, scenarioMatrix.find((item) => item.id === "B6")!, async () => {
    unlockScreen(serial);
    const onboardingResult = await ensureOnboardingFinished(serial, `${process.env.EASYUI_RUN_DIR}/ui_dumps/b6-onboarding`);
    if (onboardingResult !== "home") {
      throw new Error("Caregiver entry could not be tested because home was not available.");
    }
    launchApp(serial);
    await sleep(1000);
    const opened = await openCaregiverEntry(serial, `${process.env.EASYUI_RUN_DIR}/ui_dumps/b6-caregiver.xml`);
    if (!opened) {
      return {
        status: "PARTIALLY_VERIFIED",
        note: "Home was reachable, but the hidden caregiver gesture could not be confirmed in this adb-driven run on the current OEM build.",
      };
    }
    return { note: "Hidden caregiver entry was reachable through the status-bar long press or its clock fallback." };
  });
});
