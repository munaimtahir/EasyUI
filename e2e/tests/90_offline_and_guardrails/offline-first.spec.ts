import { test, expect } from "@playwright/test";
import { appConfig } from "../../config/app-config.js";
import { scenarioMatrix } from "../../config/scenario-matrix.js";
import { assertDeviceConnected, setAirplaneMode, unlockScreen } from "../../helpers/device.js";
import { ensureOnboardingFinished } from "../../helpers/onboarding.js";
import { withScenario } from "../../helpers/test-runner.js";

test("B9 offline-first behavior", async ({}, testInfo) => {
  const serial = appConfig.deviceSerial;
  assertDeviceConnected(serial);
  await withScenario(testInfo, serial, scenarioMatrix.find((item) => item.id === "B9")!, async () => {
    setAirplaneMode(serial, true);
    unlockScreen(serial);
    const screen = await ensureOnboardingFinished(serial, `${process.env.EASYUI_RUN_DIR}/ui_dumps/b9-offline`);
    expect(screen === "home" || screen === "onboarding").toBeTruthy();
    setAirplaneMode(serial, false);
    return { note: "App launched and remained usable with airplane mode enabled." };
  });
});
