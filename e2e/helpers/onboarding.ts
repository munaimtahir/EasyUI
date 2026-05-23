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
function isOnboardingWizard(xml: string): boolean {
  // Matches dynamic step formats such as "Step 1 of 12" using regex to avoid hardcoded step totals.
  return /Step \d+ of \d+/.test(xml);
}

function isHomeSurface(xml: string): boolean {
  if (xmlContains(xml, "home_screen") || xmlContains(xml, "Today's essentials")) {
    return true;
  }
  // Require emergency + camera to avoid false positives from onboarding previews.
  return xmlContains(xml, "Emergency") && xmlContains(xml, "Camera");
}

export async function ensureOnboardingFinished(serial: string, uiDumpPrefix: string): Promise<"home" | "onboarding"> {
  for (let step = 0; step < 20; step += 1) {
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
