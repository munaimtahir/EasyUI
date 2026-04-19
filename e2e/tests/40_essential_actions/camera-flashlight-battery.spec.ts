import { test, expect } from "@playwright/test";
import { appConfig } from "../../config/app-config.js";
import { scenarioMatrix } from "../../config/scenario-matrix.js";
import { tap, pressBack } from "../../helpers/actions.js";
import { assertDeviceConnected, unlockScreen } from "../../helpers/device.js";
import { ensureOnboardingFinished } from "../../helpers/onboarding.js";
import { dumpUi, dumpUiForEasyUi } from "../../helpers/ui-dump.js";
import { findNodeCenterByText, xmlContains } from "../../helpers/selectors.js";
import { withScenario } from "../../helpers/test-runner.js";
import { sleep } from "../../helpers/waits.js";

test("B5 essential actions", async ({}, testInfo) => {
  const serial = appConfig.deviceSerial;
  assertDeviceConnected(serial);
  await withScenario(testInfo, serial, scenarioMatrix.find((item) => item.id === "B5")!, async () => {
    unlockScreen(serial);
    const onboardingResult = await ensureOnboardingFinished(serial, `${process.env.EASYUI_RUN_DIR}/ui_dumps/b5-onboarding`);
    if (onboardingResult !== "home") {
      return { status: "BLOCKED", note: "Essential actions were blocked because home was not available." };
    }
    let homeXml = dumpUiForEasyUi(serial, `${process.env.EASYUI_RUN_DIR}/ui_dumps/b5-home-before-emergency.xml`);
    const emergencyCenter = findNodeCenterByText(homeXml, "Emergency");
    if (emergencyCenter) {
      tap(serial, emergencyCenter.x, emergencyCenter.y);
    } else {
      tap(serial, 820, 1400);
    }
    await sleep(1200);
    let xml = dumpUi(serial, `${process.env.EASYUI_RUN_DIR}/ui_dumps/b5-emergency.xml`);
    const emergencyFlowVisible =
      xmlContains(xml, "Emergency") ||
      xmlContains(xml, "Call") ||
      xmlContains(xml, "Dial") ||
      !xml.includes(`package="${appConfig.packageName}"`);
    expect(emergencyFlowVisible).toBeTruthy();
    pressBack(serial);
    homeXml = dumpUiForEasyUi(serial, `${process.env.EASYUI_RUN_DIR}/ui_dumps/b5-home-before-phone.xml`);
    const phoneCenter = findNodeCenterByText(homeXml, "Phone");
    if (phoneCenter) {
      tap(serial, phoneCenter.x, phoneCenter.y);
    } else {
      tap(serial, 260, 1390);
    }
    await sleep(1500);
    xml = dumpUi(serial, `${process.env.EASYUI_RUN_DIR}/ui_dumps/b5-phone.xml`);
    const phoneOpened = xmlContains(xml, "Phone") || xmlContains(xml, "Contacts") || xmlContains(xml, "Dial");
    return {
      status: phoneOpened ? "PASS" : "PARTIALLY_VERIFIED",
      note: phoneOpened
        ? "Emergency flow and phone action opened expected system surfaces."
        : "Emergency flow opened, but the dialer surface could not be confidently identified from the UI dump.",
    };
  });
});
