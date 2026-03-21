import { test, expect } from "@playwright/test";
import { appConfig } from "../../config/app-config.js";
import { scenarioMatrix } from "../../config/scenario-matrix.js";
import { forceStopApp, launchApp } from "../../helpers/actions.js";
import { assertDeviceConnected, unlockScreen } from "../../helpers/device.js";
import { ensureOnboardingFinished } from "../../helpers/onboarding.js";
import { withScenario } from "../../helpers/test-runner.js";
import { sleep } from "../../helpers/waits.js";

test("B10 force-stop relaunch resilience", async ({}, testInfo) => {
  const serial = appConfig.deviceSerial;
  assertDeviceConnected(serial);
  await withScenario(testInfo, serial, scenarioMatrix.find((item) => item.id === "B10")!, async () => {
    unlockScreen(serial);
    await ensureOnboardingFinished(serial, `${process.env.EASYUI_RUN_DIR}/ui_dumps/b10-onboarding`);
    forceStopApp(serial);
    launchApp(serial);
    await sleep(1500);
    const relaunched = await ensureOnboardingFinished(serial, `${process.env.EASYUI_RUN_DIR}/ui_dumps/b10-relaunch`);
    expect(relaunched).toBe("home");
    return { note: "App relaunched after force-stop without an immediate crash." };
  });
});
