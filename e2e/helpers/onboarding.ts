import { launchApp, tap } from "./actions.js";
import { dumpUi } from "./ui-dump.js";
import { findNodeCenterByText, xmlContains } from "./selectors.js";
import { sleep } from "./waits.js";

const buttonLabels = ["Start Setup", "Continue", "Continue Anyway", "Finish Setup"];

export async function ensureOnboardingFinished(serial: string, uiDumpPrefix: string): Promise<"home" | "onboarding"> {
  for (let step = 0; step < 8; step += 1) {
    launchApp(serial);
    await sleep(1500);
    const xml = dumpUi(serial, `${uiDumpPrefix}-${step}.xml`);
    if (
      xmlContains(xml, "Phone") ||
      xmlContains(xml, "Emergency") ||
      xmlContains(xml, "Health Info") ||
      xmlContains(xml, "Today's essentials")
    ) {
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
  return (
    xmlContains(xml, "Phone") ||
    xmlContains(xml, "Emergency") ||
    xmlContains(xml, "Health Info") ||
    xmlContains(xml, "Today's essentials")
  ) ? "home" : "onboarding";
}

function isKnownOnboardingSurface(xml: string): boolean {
  return (
    xmlContains(xml, "EasyUI Senior Launcher") ||
    xmlContains(xml, "Set EasyUI as Home") ||
    xmlContains(xml, "Caregiver Help")
  );
}
