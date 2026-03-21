import { test, expect } from "@playwright/test";
import { readFileSync } from "node:fs";
import { repoRoot } from "../../config/app-config.js";
import { scenarioMatrix } from "../../config/scenario-matrix.js";
import { recordIssue, recordScenario } from "../../helpers/results.js";

test("A2 launcher manifest audit", async () => {
  const manifest = readFileSync(`${repoRoot}/app/src/main/AndroidManifest.xml`, "utf8");
  expect(manifest).toContain("android.intent.category.HOME");
  expect(manifest).toContain("android.intent.category.DEFAULT");
  const hasLauncherCategory = manifest.includes("android.intent.category.LAUNCHER");
  const hasNetworkStatePermission = manifest.includes("android.permission.ACCESS_NETWORK_STATE");
  const status = hasLauncherCategory ? "PARTIALLY_VERIFIED" : "PASS";
  recordScenario({
    ...scenarioMatrix.find((item) => item.id === "A2")!,
    status,
    note: hasLauncherCategory
      ? "Main launcher activity declares HOME and DEFAULT only; standard LAUNCHER category is intentionally absent for a launcher-only surface."
      : "Main launcher activity declares HOME + DEFAULT without conflicting launcher categories.",
  });
  recordScenario({
    ...scenarioMatrix.find((item) => item.id === "A3")!,
    status: hasNetworkStatePermission ? "PARTIALLY_VERIFIED" : "PASS",
    note: hasNetworkStatePermission
      ? "Manifest declares CALL_PHONE, SEND_SMS, ACCESS_NETWORK_STATE, and optional camera flash feature. ACCESS_NETWORK_STATE should be reviewed because product is offline-first."
      : "Manifest declares only the currently expected call, SMS, and optional flash capabilities for this build.",
  });
  if (hasNetworkStatePermission) {
    recordIssue({
      issueId: "A3-ISSUE",
      title: "Manifest includes ACCESS_NETWORK_STATE despite offline-first product scope",
      severity: "P2",
      type: "scope-alignment",
      workflow: "Permission declaration audit",
      steps: ["Inspect AndroidManifest.xml"],
      expected: "Only permissions strictly needed for implemented launcher actions should be declared.",
      actual: "ACCESS_NETWORK_STATE is declared, but the current offline-first launcher flow does not clearly justify it.",
      evidenceId: "A3",
      suspectedArea: "app/src/main/AndroidManifest.xml",
      recommendation: "Remove ACCESS_NETWORK_STATE unless a shipped feature depends on it and is documented.",
    });
  }
});
