import { expect } from "@playwright/test";
import { dumpUi } from "./ui-dump.js";
import { xmlContains } from "./selectors.js";

export function expectUiToContain(serial: string, uiDumpPath: string, text: string): string {
  const xml = dumpUi(serial, uiDumpPath);
  expect(xmlContains(xml, text), `Expected UI dump to contain "${text}"`).toBeTruthy();
  return xml;
}
