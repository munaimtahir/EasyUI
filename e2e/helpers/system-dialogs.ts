import { tap } from "./actions.js";
import { dumpUi } from "./ui-dump.js";
import { findNodeCenterByText, xmlContains } from "./selectors.js";
import { sleep } from "./waits.js";

export async function handleUsbDebugPrompt(serial: string, uiDumpPath: string): Promise<boolean> {
  const xml = dumpUi(serial, uiDumpPath);
  if (!xmlContains(xml, "Allow USB debugging?")) {
    return false;
  }
  const checkbox = findNodeCenterByText(xml, "Always allow from this computer");
  if (checkbox) {
    tap(serial, checkbox.x, checkbox.y);
    await sleep(300);
  }
  const allow = findNodeCenterByText(xml, "ALLOW");
  if (allow) {
    tap(serial, allow.x, allow.y);
    await sleep(1000);
    return true;
  }
  return false;
}
