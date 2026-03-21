import { test } from "@playwright/test";
import { appConfig } from "../../config/app-config.js";
import { featureFlags } from "../../config/feature-flags.js";
import { scenarioMatrix } from "../../config/scenario-matrix.js";
import { tap } from "../../helpers/actions.js";
import { assertDeviceConnected, unlockScreen } from "../../helpers/device.js";
import { ensureOnboardingFinished } from "../../helpers/onboarding.js";
import { dumpUi } from "../../helpers/ui-dump.js";
import { xmlContains } from "../../helpers/selectors.js";
import { withScenario } from "../../helpers/test-runner.js";
import { sleep } from "../../helpers/waits.js";

test("B4 simple app access and visible tile actions", async ({}, testInfo) => {
  const serial = appConfig.deviceSerial;
  assertDeviceConnected(serial);
  await withScenario(testInfo, serial, scenarioMatrix.find((item) => item.id === "B4")!, async () => {
    unlockScreen(serial);
    await ensureOnboardingFinished(serial, `${process.env.EASYUI_RUN_DIR}/ui_dumps/b4-onboarding`);
    await sleep(1000);
    if (!featureFlags.fullAppListEntryOnHome) {
      return {
        status: "PARTIALLY_VERIFIED",
        note: "The app list screen exists in code, but the docs and current feature flags still mark the senior-facing home entry as not wired in this build.",
      };
    }
    tap(serial, 820, 1950);
    await sleep(1200);
    const xml = dumpUi(serial, `${process.env.EASYUI_RUN_DIR}/ui_dumps/b4-app-list.xml`);
    return {
      status: xmlContains(xml, "All Apps") ? "PASS" : "FAIL",
      note: xmlContains(xml, "All Apps") ? "App list is reachable from the senior surface." : "Expected All Apps surface did not open from home.",
    };
  });
});
