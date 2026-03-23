import { execFileSync } from "node:child_process";
import fs from "node:fs";
import path from "node:path";

const runDir = process.argv[2];
const scenarioFile = path.join(runDir, "results", "scenario-results.ndjson");
const issuesFile = path.join(runDir, "results", "issues.ndjson");

const scenarios = fs.existsSync(scenarioFile)
  ? fs.readFileSync(scenarioFile, "utf8").trim().split("\n").filter(Boolean).map((line) => JSON.parse(line))
  : [];
const issues = fs.existsSync(issuesFile)
  ? fs.readFileSync(issuesFile, "utf8").trim().split("\n").filter(Boolean).map((line) => JSON.parse(line))
  : [];

const countBy = (items, key) => items.reduce((acc, item) => ((acc[item[key]] = (acc[item[key]] || 0) + 1), acc), {});
const statusCounts = countBy(scenarios, "status");
const severityCounts = countBy(issues, "severity");

const deviceInfo = {
  serial: process.env.EASYUI_DEVICE_SERIAL || "34081500040008N",
  manufacturer: safeExec("adb", ["-s", process.env.EASYUI_DEVICE_SERIAL || "34081500040008N", "shell", "getprop", "ro.product.manufacturer"]),
  model: safeExec("adb", ["-s", process.env.EASYUI_DEVICE_SERIAL || "34081500040008N", "shell", "getprop", "ro.product.model"]),
  androidVersion: safeExec("adb", ["-s", process.env.EASYUI_DEVICE_SERIAL || "34081500040008N", "shell", "getprop", "ro.build.version.release"]),
  sdk: safeExec("adb", ["-s", process.env.EASYUI_DEVICE_SERIAL || "34081500040008N", "shell", "getprop", "ro.build.version.sdk"]),
  size: safeExec("adb", ["-s", process.env.EASYUI_DEVICE_SERIAL || "34081500040008N", "shell", "wm", "size"]).replace("Physical size: ", ""),
  density: safeExec("adb", ["-s", process.env.EASYUI_DEVICE_SERIAL || "34081500040008N", "shell", "wm", "density"]).replace("Physical density: ", ""),
};

const packageName = "com.easyui.launcher.debug";
fs.writeFileSync(
  path.join(runDir, "DEVICE_INFO.md"),
  `# Device Info

- device serial: ${deviceInfo.serial}
- manufacturer: ${deviceInfo.manufacturer}
- model: ${deviceInfo.model}
- Android version: ${deviceInfo.androidVersion}
- SDK level: ${deviceInfo.sdk}
- resolution: ${deviceInfo.size}
- density: ${deviceInfo.density}
- build tested: debug
- package name: ${packageName}
- test date: ${new Date().toISOString()}
`,
);

fs.writeFileSync(
  path.join(runDir, "TEST_MATRIX.md"),
  `# Test Matrix

| ID | Suite | Scenario | Priority | Status | Evidence Path | Note |
| --- | --- | --- | --- | --- | --- | --- |
${scenarios
  .map(
    (item) =>
      `| ${item.id} | ${item.suite} | ${item.scenario} | ${item.priority} | ${item.status} | screenshots/${item.id.toLowerCase()}.png | ${escapePipes(item.note)} |`,
  )
  .join("\n")}
`,
);

fs.writeFileSync(
  path.join(runDir, "ISSUES_FOUND.md"),
  `# Issues Found

${issues.length === 0 ? "No issues recorded.\n" : issues
    .map(
      (item) => `## ${item.issueId} — ${item.title}
- severity: ${item.severity}
- type: ${item.type}
- affected workflow: ${item.workflow}
- reproduction steps: ${item.steps.join(" -> ")}
- expected result: ${item.expected}
- actual result: ${item.actual}
- evidence: screenshots/${item.evidenceId.toLowerCase()}.png, ui_dumps/${item.evidenceId.toLowerCase()}.xml
- suspected area: ${item.suspectedArea}
- recommended fix direction: ${item.recommendation}
`,
    )
    .join("\n")}
`,
);

const grouped = {
  A: issues.filter((item) => item.severity === "P0"),
  B: issues.filter((item) => item.severity === "P1"),
  C: issues.filter((item) => item.severity === "P2"),
  D: issues.filter((item) => item.severity === "P3"),
};

fs.writeFileSync(
  path.join(runDir, "FIX_PLAN.md"),
  `# Fix Plan

## Batch A — release blockers
${grouped.A.map((item) => `- ${item.issueId}: ${item.title}`).join("\n") || "- none"}

## Batch B — broken workflows
${grouped.B.map((item) => `- ${item.issueId}: ${item.title}`).join("\n") || "- none"}

## Batch C — trust and clarity
${grouped.C.map((item) => `- ${item.issueId}: ${item.title}`).join("\n") || "- none"}

## Batch D — visual refinement
${grouped.D.map((item) => `- ${item.issueId}: ${item.title}`).join("\n") || "- none"}
`,
);

const releaseRecommendation = grouped.A.length > 0
  ? "NOT READY"
  : (grouped.B.length > 0 || (statusCounts.FAIL || 0) > 0)
    ? "CONDITIONALLY READY AFTER FIXES"
    : "READY FOR LIMITED RELEASE TESTING";

fs.writeFileSync(
  path.join(runDir, "RELEASE_RISK_SUMMARY.md"),
  `# Release Risk Summary

- total scenarios: ${scenarios.length}
- pass count: ${statusCounts.PASS || 0}
- fail count: ${statusCounts.FAIL || 0}
- blocked count: ${statusCounts.BLOCKED || 0}
- partially verified count: ${statusCounts.PARTIALLY_VERIFIED || 0}
- OEM dependent count: ${statusCounts.OEM_DEPENDENT || 0}
- P0 count: ${severityCounts.P0 || 0}
- P1 count: ${severityCounts.P1 || 0}
- P2 count: ${severityCounts.P2 || 0}
- P3 count: ${severityCounts.P3 || 0}
- top release risks: ${issues.slice(0, 3).map((item) => item.title).join("; ") || "none recorded"}
- release recommendation: ${releaseRecommendation}
`,
);

function safeExec(cmd, args) {
  try {
    return execFileSync(cmd, args, { encoding: "utf8" }).trim();
  } catch {
    return "unavailable";
  }
}

function escapePipes(value) {
  return String(value || "").replaceAll("|", "\\|");
}
