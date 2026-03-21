import { appendFileSync, existsSync, mkdirSync, writeFileSync } from "node:fs";
import path from "node:path";
import type { TestInfo } from "@playwright/test";
import { appConfig } from "../config/app-config.js";
import { runAdbBuffer } from "./adb.js";
import { dumpUi } from "./ui-dump.js";

export type EvidencePaths = {
  screenshot: string;
  uiDump: string;
  note: string;
  log: string;
};

const fallbackRunDir = path.join(
  appConfig.reportsDir,
  `adhoc_${new Date().toISOString().replaceAll(/[:.]/g, "-")}_${process.pid}`,
);

export function getRunDir(): string {
  return process.env.EASYUI_RUN_DIR ?? fallbackRunDir;
}

export function ensureArtifactDirs(): void {
  const runDir = getRunDir();
  for (const child of ["logs", "screenshots", "recordings", "ui_dumps", "notes", "results"]) {
    mkdirSync(path.join(runDir, child), { recursive: true });
  }
}

export function scenarioSlug(id: string): string {
  return id.toLowerCase().replaceAll(/[^a-z0-9]+/g, "-");
}

export function evidencePaths(id: string): EvidencePaths {
  const runDir = getRunDir();
  const slug = scenarioSlug(id);
  return {
    screenshot: path.join(runDir, "screenshots", `${slug}.png`),
    uiDump: path.join(runDir, "ui_dumps", `${slug}.xml`),
    note: path.join(runDir, "notes", `${slug}.md`),
    log: path.join(runDir, "logs", `${slug}.log`),
  };
}

export function captureEvidence(serial: string, id: string, note: string): EvidencePaths {
  ensureArtifactDirs();
  const paths = evidencePaths(id);
  try {
    const screenshot = runAdbBuffer(serial, ["exec-out", "screencap", "-p"]);
    writeFileSync(paths.screenshot, screenshot);
  } catch (error) {
    writeFileSync(paths.screenshot, Buffer.from(`screenshot unavailable: ${(error as Error).message}\n`, "utf8"));
  }
  try {
    dumpUi(serial, paths.uiDump);
  } catch (error) {
    writeFileSync(paths.uiDump, `ui dump unavailable: ${(error as Error).message}\n`, "utf8");
  }
  writeFileSync(paths.note, `${note}\n`, "utf8");
  return paths;
}

export function appendScenarioLog(id: string, text: string): void {
  ensureArtifactDirs();
  appendFileSync(evidencePaths(id).log, `${text}\n`, "utf8");
}

export function attachEvidence(testInfo: TestInfo, id: string): void {
  const paths = evidencePaths(id);
  for (const file of [paths.screenshot, paths.uiDump, paths.note, paths.log]) {
    if (existsSync(file)) {
      void testInfo.attach(path.basename(file), { path: file });
    }
  }
}
