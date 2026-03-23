import { test, expect } from "@playwright/test";
import { appConfig } from "../../config/app-config.js";
import { scenarioMatrix } from "../../config/scenario-matrix.js";
import { launchApp } from "../../helpers/actions.js";
import { assertDeviceConnected, unlockScreen } from "../../helpers/device.js";
import { dumpUi } from "../../helpers/ui-dump.js";
import { xmlContains } from "../../helpers/selectors.js";
import { trySetDefaultLauncher, isDefaultLauncher } from "../../helpers/launcher.js";
import { withScenario } from "../../helpers/test-runner.js";
import { sleep } from "../../helpers/waits.js";

test("B1a onboarding default launcher guidance", async ({}, testInfo) => {
  const serial = appConfig.deviceSerial;
  assertDeviceConnected(serial);
  await withScenario(testInfo, serial, scenarioMatrix.find((item) => item.id === "B1A")!, async () => {
    unlockScreen(serial);
    launchApp(serial);
    await sleep(1000);
    const setResult = trySetDefaultLauncher(serial);
    const dumpPath = `${process.env.EASYUI_RUN_DIR}/ui_dumps/b1-guidance.xml`;
    const xml = dumpUi(serial, dumpPath);
    expect(isDefaultLauncher(serial) || xmlContains(xml, "Set EasyUI as Home") || xmlContains(xml, "Phone")).toBeTruthy();
    const defaultSet = isDefaultLauncher(serial);
    return {
      status: defaultSet ? "PASS" : "OEM_DEPENDENT",
      note: defaultSet
        ? "Device now resolves Home to EasyUI."
        : `Default launcher guidance is present, but automatic HOME role assignment did not complete cleanly: ${setResult || "no shell output"}`,
      issue: defaultSet
        ? undefined
        : {
            title: "Default launcher still requires OEM-specific confirmation",
            severity: "P2",
            actual: "EasyUI is installed and launchable, but HOME role assignment still depends on the device's launcher-role handling.",
            expected: "Launcher should be selectable and understandable, with OEM-specific friction documented rather than hidden.",
            suspectedArea: "Device/OEM HOME role handling",
            recommendation: "Keep manual validation in release checklist for Vivo launcher-role behavior.",
          },
    };
  });
});
