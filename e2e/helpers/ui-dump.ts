import { mkdirSync, writeFileSync } from "node:fs";
import { dirname } from "node:path";
import { appConfig } from "../config/app-config.js";
import { launchApp } from "./actions.js";
import { runAdb } from "./adb.js";

export function dumpUi(serial: string, outputPath: string): string {
  mkdirSync(dirname(outputPath), { recursive: true });
  let lastError: Error | null = null;
  for (let attempt = 0; attempt < 5; attempt += 1) {
    try {
      runAdb(serial, ["shell", "rm", "-f", "/sdcard/window_dump.xml"], { allowFailure: true });
      const dumpOutput = runAdb(serial, ["shell", "uiautomator", "dump", "/sdcard/window_dump.xml"]);
      if (dumpOutput.includes("ERROR: null root node") || dumpOutput.includes("null root node")) {
        throw new Error("uiautomator dump returned null root node");
      }
      const xml = runAdb(serial, ["exec-out", "cat", "/sdcard/window_dump.xml"]);
      if (!xml.trim().startsWith("<?xml")) {
        throw new Error("uiautomator dump file is not valid XML");
      }
      writeFileSync(outputPath, xml, "utf8");
      return xml;
    } catch (error) {
      lastError = error as Error;
      if (!shouldRetryUiDump(lastError.message) || attempt === 4) {
        break;
      }
      runAdb(serial, ["shell", "rm", "-f", "/sdcard/window_dump.xml"], { allowFailure: true });
      sleepSync(700);
    }
  }
  const fallbackXml = `<?xml version="1.0" encoding="UTF-8" standalone="yes" ?><hierarchy dump_error="${escapeXml(lastError?.message ?? "unknown")}"></hierarchy>`;
  writeFileSync(outputPath, fallbackXml, "utf8");
  return fallbackXml;
}

export function dumpUiForEasyUi(serial: string, outputPath: string): string {
  return dumpUiForPackage(serial, outputPath, appConfig.packageName, () => {
    launchApp(serial);
  });
}

export function dumpUiForPackage(
  serial: string,
  outputPath: string,
  expectedPackage: string,
  prepare: () => void,
): string {
  let lastXml = "";
  for (let attempt = 0; attempt < 5; attempt += 1) {
    prepare();
    sleepSync(1200);
    const xml = dumpUi(serial, outputPath);
    lastXml = xml;
    if (xml.includes(`package="${expectedPackage}"`)) {
      return xml;
    }
    sleepSync(700);
  }
  return lastXml;
}

function shouldRetryUiDump(message: string): boolean {
  return (
    message.includes("uiautomator dump") ||
    message.includes("FtCpuInfo") ||
    message.includes("user_cpu_freq") ||
    message.includes("Permission denied") ||
    message.includes("EACCES") ||
    message.includes("null root node") ||
    message.includes("not valid XML")
  );
}

function escapeXml(value: string): string {
  return value
    .replaceAll("&", "&amp;")
    .replaceAll("\"", "&quot;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;");
}

function sleepSync(ms: number): void {
  Atomics.wait(new Int32Array(new SharedArrayBuffer(4)), 0, 0, ms);
}
