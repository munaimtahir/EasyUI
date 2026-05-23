import { longPress, tap } from "./actions.js";
import { dumpUi } from "./ui-dump.js";
import { findNodeCenterByResourceId, xmlContains } from "./selectors.js";
import { sleep } from "./waits.js";

export async function openCaregiverEntry(serial: string, uiDumpPath: string): Promise<boolean> {
  const initialXml = dumpUi(serial, uiDumpPath);

  // Try status-bar long press via resource ID
  const statusBarCenter = findNodeCenterByResourceId(initialXml, "home_top_status_bar");
  if (statusBarCenter) {
    longPress(serial, statusBarCenter.x, statusBarCenter.y, 3200);
    await sleep(2600);
    const xml = dumpUi(serial, uiDumpPath);
    if (xmlContains(xml, "Caregiver Settings") || xmlContains(xml, "Enter Caregiver PIN")) {
      return true;
    }
  } else {
    // Fallback coordinates for status bar
    for (const [x, y] of [
      [540, 300],
      [300, 300],
    ] as const) {
      longPress(serial, x, y, 3200);
      await sleep(2600);
      const xml = dumpUi(serial, uiDumpPath);
      if (xmlContains(xml, "Caregiver Settings") || xmlContains(xml, "Enter Caregiver PIN")) {
        return true;
      }
    }
  }

  // Try 5 clock taps via resource ID
  const clockCenter = findNodeCenterByResourceId(initialXml, "home_clock_text");
  if (clockCenter) {
    for (let attempt = 0; attempt < 5; attempt += 1) {
      tap(serial, clockCenter.x, clockCenter.y);
      await sleep(250);
    }
    await sleep(2200);
    const xml = dumpUi(serial, uiDumpPath);
    if (xmlContains(xml, "Caregiver Settings") || xmlContains(xml, "Enter Caregiver PIN")) {
      return true;
    }
  }

  // Fallback coordinate taps
  for (let attempt = 0; attempt < 5; attempt += 1) {
    tap(serial, 330, 310);
    await sleep(350);
  }
  await sleep(2200);
  let fallbackXml = dumpUi(serial, uiDumpPath);
  if (xmlContains(fallbackXml, "Caregiver Settings") || xmlContains(fallbackXml, "Enter Caregiver PIN")) {
    return true;
  }
  for (let attempt = 0; attempt < 5; attempt += 1) {
    tap(serial, 250, 320);
    await sleep(350);
  }
  await sleep(2200);
  fallbackXml = dumpUi(serial, uiDumpPath);
  return xmlContains(fallbackXml, "Caregiver Settings") || xmlContains(fallbackXml, "Enter Caregiver PIN");
}
