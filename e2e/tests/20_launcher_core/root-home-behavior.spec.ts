import { test, expect } from "@playwright/test";
import { appConfig } from "../../config/app-config.js";
import { scenarioMatrix } from "../../config/scenario-matrix.js";
import { launchApp, openRecents, pressHome } from "../../helpers/actions.js";
import { assertDeviceConnected, unlockScreen } from "../../helpers/device.js";
import { isDefaultLauncher } from "../../helpers/launcher.js";
import { ensureOnboardingFinished } from "../../helpers/onboarding.js";
import { handleUsbDebugPrompt } from "../../helpers/system-dialogs.js";
import { dumpUiForPackage } from "../../helpers/ui-dump.js";
import { xmlContains } from "../../helpers/selectors.js";
import { withScenario } from "../../helpers/test-runner.js";
import { sleep } from "../../helpers/waits.js";

test("B2 home button and root behavior", async ({}, testInfo) => {
  const serial = appConfig.deviceSerial;
  assertDeviceConnected(serial);
  await withScenario(testInfo, serial, scenarioMatrix.find((item) => item.id === "B2")!, async () => {
    unlockScreen(serial);
    const onboardingResult = await ensureOnboardingFinished(serial, `${process.env.EASYUI_RUN_DIR}/ui_dumps/b2-onboarding`);
    if (onboardingResult !== "home") {
      return { status: "BLOCKED", note: "Home screen was not available because onboarding was still incomplete." };
    }
    if (!isDefaultLauncher(serial)) {
      return {
        status: "OEM_DEPENDENT",
        note: "EasyUI is not the resolved HOME app, so hardware Home behavior cannot be fully verified on this run.",
      };
    }
    pressHome(serial);
    await sleep(1500);
    await handleUsbDebugPrompt(serial, `${process.env.EASYUI_RUN_DIR}/ui_dumps/b2-usb-debug.xml`);
    let xml = dumpUiForPackage(
      serial,
      `${process.env.EASYUI_RUN_DIR}/ui_dumps/b2-home.xml`,
      appConfig.packageName,
      () => {
        pressHome(serial);
      },
    );
    expect(xmlContains(xml, "Phone") || xmlContains(xml, "Emergency")).toBeTruthy();
    openRecents(serial);
    await sleep(1000);
    xml = dumpUiForPackage(
      serial,
      `${process.env.EASYUI_RUN_DIR}/ui_dumps/b2-home-after-recents.xml`,
      appConfig.packageName,
      () => {
        pressHome(serial);
      },
    );
    expect(xmlContains(xml, "home_screen") || xmlContains(xml, "Phone") || xmlContains(xml, "Emergency")).toBeTruthy();
    return { note: "Home and recents roundtrip returned to EasyUI while it was the resolved HOME app." };
  });
});
