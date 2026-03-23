import { execFileSync, type ChildProcessWithoutNullStreams, spawn } from "node:child_process";
import { mkdirSync } from "node:fs";
import path from "node:path";
import { appConfig, repoRoot } from "../config/app-config.js";

export function adbArgs(serial: string, ...args: string[]): string[] {
  return ["-s", serial, ...args];
}

export function runAdb(serial: string, args: string[], options?: { allowFailure?: boolean }): string {
  for (let attempt = 0; attempt < 3; attempt += 1) {
    try {
      return execFileSync("adb", adbArgs(serial, ...args), {
        encoding: "utf8",
        stdio: ["ignore", "pipe", "pipe"],
      }).trim();
    } catch (error) {
      const stderr = String((error as { stderr?: string }).stderr ?? "");
      if (attempt < 2 && shouldRetryAdb(stderr)) {
        try {
          execFileSync("adb", ["-s", serial, "wait-for-device"], { stdio: ["ignore", "ignore", "ignore"] });
          continue;
        } catch {
          continue;
        }
      }
      if (options?.allowFailure) {
        return stderr.trim();
      }
      throw error;
    }
  }
  return "";
}

export function runGradle(args: string[]): string {
  return execFileSync(args[0], args.slice(1), {
    cwd: repoRoot,
    encoding: "utf8",
    stdio: ["ignore", "pipe", "pipe"],
  });
}

export function startLogcat(serial: string, outputPath: string): ChildProcessWithoutNullStreams {
  mkdirSync(path.dirname(outputPath), { recursive: true });
  return spawn("adb", adbArgs(serial, "logcat", "-v", "time"), {
    stdio: ["ignore", "pipe", "pipe"],
  });
}

export function clearLogcat(serial: string): void {
  runAdb(serial, ["logcat", "-c"], { allowFailure: true });
}

export function runAdbBuffer(serial: string, args: string[]): Buffer {
  for (let attempt = 0; attempt < 3; attempt += 1) {
    try {
      return execFileSync("adb", adbArgs(serial, ...args), {
        stdio: ["ignore", "pipe", "pipe"],
      });
    } catch (error) {
      const stderr = String((error as { stderr?: string }).stderr ?? "");
      if (attempt < 2 && shouldRetryAdb(stderr)) {
        try {
          execFileSync("adb", ["-s", serial, "wait-for-device"], { stdio: ["ignore", "ignore", "ignore"] });
          continue;
        } catch {
          continue;
        }
      }
      throw error;
    }
  }
  return Buffer.alloc(0);
}

function shouldRetryAdb(stderr: string): boolean {
  return stderr.includes("device") && (stderr.includes("not found") || stderr.includes("offline"));
}
