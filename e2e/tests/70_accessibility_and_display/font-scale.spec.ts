import { test } from "@playwright/test";
import { appConfig } from "../../config/app-config.js";
import { scenarioMatrix } from "../../config/scenario-matrix.js";
import { launchApp } from "../../helpers/actions.js";
import { assertDeviceConnected, unlockScreen } from "../../helpers/device.js";
import { ensureOnboardingFinished } from "../../helpers/onboarding.js";
import { dumpUi } from "../../helpers/ui-dump.js";
import { xmlContains } from "../../helpers/selectors.js";
import { runAdb } from "../../helpers/adb.js";
import { withScenario } from "../../helpers/test-runner.js";
import { sleep } from "../../helpers/waits.js";

test("B8 accessibility and display resilience", async ({}, testInfo) => {
  const serial = appConfig.deviceSerial;
  assertDeviceConnected(serial);
  await withScenario(testInfo, serial, scenarioMatrix.find((item) => item.id === "B8")!, async () => {
    const original = runAdb(serial, ["shell", "settings", "get", "system", "font_scale"], { allowFailure: true }) || "1.0";
    runAdb(serial, ["shell", "settings", "put", "system", "font_scale", "1.3"], { allowFailure: true });
    unlockScreen(serial);
    const onboardingResult = await ensureOnboardingFinished(serial, `${process.env.EASYUI_RUN_DIR}/ui_dumps/b8-onboarding`);
    if (onboardingResult !== "home") {
      runAdb(serial, ["shell", "settings", "put", "system", "font_scale", original], { allowFailure: true });
      return {
        status: "BLOCKED",
        note: "Home was not reachable after changing font scale, so accessibility review is blocked.",
      };
    }
    launchApp(serial);
    await sleep(1500);
    const xml = dumpUi(serial, `${process.env.EASYUI_RUN_DIR}/ui_dumps/b8-font-scale.xml`);
    runAdb(serial, ["shell", "settings", "put", "system", "font_scale", original], { allowFailure: true });
    return {
      status: xmlContains(xml, "Emergency") && xmlContains(xml, "Health Info") ? "PARTIALLY_VERIFIED" : "FAIL",
      note: "Font scale was raised to 1.3 and the core home labels remained present in the UI dump. Visual clipping still requires human review of the captured screenshot.",
    };
  });
});
