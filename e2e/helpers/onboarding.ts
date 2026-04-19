import { launchApp, tap } from "./actions.js";
import { trySetDefaultLauncher } from "./launcher.js";
import { dumpUi } from "./ui-dump.js";
import { findNodeCenterByText, xmlContains } from "./selectors.js";
import { sleep } from "./waits.js";

const buttonLabels = [
  "Start Setup",
  "Open Default App Settings",
  "Next",
  "Looks Good",
  "Go to Home",
  "Continue",
  "Continue Anyway",
  "Finish Setup",
];
const onboardingProgressLabels = ["Step 1 of 10", "Step 2 of 10", "Step 3 of 10", "Step 4 of 10", "Step 5 of 10", "Step 6 of 10", "Step 7 of 10", "Step 8 of 10", "Step 9 of 10", "Step 10 of 10"];

function isOnboardingWizard(xml: string): boolean {
  return onboardingProgressLabels.some((label) => xmlContains(xml, label));
}

function isHomeSurface(xml: string): boolean {
  if (xmlContains(xml, "home_screen") || xmlContains(xml, "Today's essentials")) {
    return true;
  }
  // Require emergency + camera to avoid false positives from onboarding previews.
  return xmlContains(xml, "Emergency") && xmlContains(xml, "Camera");
}

export async function ensureOnboardingFinished(serial: string, uiDumpPrefix: string): Promise<"home" | "onboarding"> {
  for (let step = 0; step < 14; step += 1) {
    // Best effort to avoid getting stuck on OEM default-launcher screens.
    trySetDefaultLauncher(serial);
    launchApp(serial);
    await sleep(1500);
    const xml = dumpUi(serial, `${uiDumpPrefix}-${step}.xml`);
    if (isHomeSurface(xml) && !isOnboardingWizard(xml)) {
      return "home";
    }
    const label = buttonLabels.find((item) => xmlContains(xml, item));
    if (!label) {
      tap(serial, 540, 2160);
      continue;
    }
    const center = findNodeCenterByText(xml, label);
    if (center) {
      tap(serial, center.x, center.y);
    } else {
      tap(serial, 540, 2160);
    }
  }
  launchApp(serial);
  await sleep(1500);
  const xml = dumpUi(serial, `${uiDumpPrefix}-final.xml`);
  return isHomeSurface(xml) && !isOnboardingWizard(xml) ? "home" : "onboarding";
}

function isKnownOnboardingSurface(xml: string): boolean {
  return (
    xmlContains(xml, "EasyUI Senior Launcher") ||
    xmlContains(xml, "Set EasyUI as Home") ||
    xmlContains(xml, "Caregiver Help")
  );
}
