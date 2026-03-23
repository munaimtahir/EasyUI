import { appendFileSync, mkdirSync, writeFileSync } from "node:fs";
import path from "node:path";
import type { ScenarioStatus, Severity } from "../config/scenario-matrix.js";
import { evidencePaths, ensureArtifactDirs, getRunDir } from "./evidence.js";

export type ScenarioResult = {
  id: string;
  suite: string;
  scenario: string;
  priority: Severity;
  status: ScenarioStatus;
  note: string;
  issueId?: string;
  severity?: Severity;
  suspectedArea?: string;
};

function resultFile(): string {
  ensureArtifactDirs();
  const dir = path.join(getRunDir(), "results");
  mkdirSync(dir, { recursive: true });
  return path.join(dir, "scenario-results.ndjson");
}

function issuesFile(): string {
  const dir = path.join(getRunDir(), "results");
  mkdirSync(dir, { recursive: true });
  return path.join(dir, "issues.ndjson");
}

export function recordScenario(result: ScenarioResult): void {
  const evidence = evidencePaths(result.id);
  appendFileSync(resultFile(), `${JSON.stringify({ ...result, evidence })}\n`, "utf8");
}

export function recordIssue(issue: {
  issueId: string;
  title: string;
  severity: Severity;
  type: string;
  workflow: string;
  steps: string[];
  expected: string;
  actual: string;
  evidenceId: string;
  suspectedArea: string;
  recommendation: string;
}): void {
  appendFileSync(issuesFile(), `${JSON.stringify(issue)}\n`, "utf8");
}

export function writeMarkdown(fileName: string, content: string): void {
  ensureArtifactDirs();
  writeFileSync(path.join(getRunDir(), fileName), content, "utf8");
}
