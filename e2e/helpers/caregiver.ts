import { longPress, tap } from "./actions.js";
import { dumpUi } from "./ui-dump.js";
import { xmlContains } from "./selectors.js";
import { sleep } from "./waits.js";

export async function openCaregiverEntry(serial: string, uiDumpPath: string): Promise<boolean> {
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
