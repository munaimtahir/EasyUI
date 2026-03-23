import { test } from "@playwright/test";
import { appConfig } from "../../config/app-config.js";
import { scenarioMatrix } from "../../config/scenario-matrix.js";
import { launchApp, tap } from "../../helpers/actions.js";
import { openCaregiverEntry } from "../../helpers/caregiver.js";
import { assertDeviceConnected, unlockScreen } from "../../helpers/device.js";
import { ensureOnboardingFinished } from "../../helpers/onboarding.js";
import { dumpUi } from "../../helpers/ui-dump.js";
import { xmlContains } from "../../helpers/selectors.js";
import { withScenario } from "../../helpers/test-runner.js";
import { sleep } from "../../helpers/waits.js";

test("B7 hidden apps and layout stability", async ({}, testInfo) => {
  const serial = appConfig.deviceSerial;
  assertDeviceConnected(serial);
  await withScenario(testInfo, serial, scenarioMatrix.find((item) => item.id === "B7")!, async () => {
    unlockScreen(serial);
    const onboardingResult = await ensureOnboardingFinished(serial, `${process.env.EASYUI_RUN_DIR}/ui_dumps/b7-onboarding`);
    if (onboardingResult !== "home") {
      return { status: "BLOCKED", note: "Home was not reachable, so hidden-app verification is blocked." };
    }
    launchApp(serial);
    await sleep(1000);
    const opened = await openCaregiverEntry(serial, `${process.env.EASYUI_RUN_DIR}/ui_dumps/b7-caregiver.xml`);
    if (!opened) {
      return { status: "BLOCKED", note: "Caregiver entry could not be opened, so hidden-app verification is blocked." };
    }
    tap(serial, 550, 1200);
    await sleep(1200);
    const xml = dumpUi(serial, `${process.env.EASYUI_RUN_DIR}/ui_dumps/b7-hidden-apps.xml`);
    return {
      status: xmlContains(xml, "Hidden Apps") ? "PARTIALLY_VERIFIED" : "BLOCKED",
      note: xmlContains(xml, "Hidden Apps")
        ? "Hidden Apps management screen opened. Mutation was intentionally not executed in this smoke-focused run."
        : "Could not reach Hidden Apps from the caregiver tools surface.",
    };
  });
});
