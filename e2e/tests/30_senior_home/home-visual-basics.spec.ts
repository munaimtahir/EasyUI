import { test, expect } from "@playwright/test";
import { appConfig } from "../../config/app-config.js";
import { scenarioMatrix } from "../../config/scenario-matrix.js";
import { assertDeviceConnected, unlockScreen } from "../../helpers/device.js";
import { ensureOnboardingFinished } from "../../helpers/onboarding.js";
import { dumpUiForEasyUi } from "../../helpers/ui-dump.js";
import { xmlContains } from "../../helpers/selectors.js";
import { withScenario } from "../../helpers/test-runner.js";

test("B3 home visibility and tile clarity", async ({}, testInfo) => {
  const serial = appConfig.deviceSerial;
  assertDeviceConnected(serial);
  await withScenario(testInfo, serial, scenarioMatrix.find((item) => item.id === "B3")!, async () => {
    unlockScreen(serial);
    const onboardingResult = await ensureOnboardingFinished(serial, `${process.env.EASYUI_RUN_DIR}/ui_dumps/b3-onboarding`);
    if (onboardingResult !== "home") {
      throw new Error("Home screen was not reachable from the current app state.");
    }
    const xml = dumpUiForEasyUi(serial, `${process.env.EASYUI_RUN_DIR}/ui_dumps/b3-home.xml`);
    for (const label of ["Phone", "Messages", "Contacts", "Photos", "Camera", "Emergency"]) {
      expect(xmlContains(xml, label), `Missing expected tile ${label}`).toBeTruthy();
    }
    return { note: "Home screen exposes the expected large-label essentials on the current build." };
  });
});
