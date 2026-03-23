import { test, expect } from "@playwright/test";
import { readFileSync } from "node:fs";
import { appConfig, repoRoot } from "../../config/app-config.js";
import { scenarioMatrix } from "../../config/scenario-matrix.js";
import { recordScenario, writeMarkdown } from "../../helpers/results.js";

test("A1 repository and identity audit", async () => {
  const buildFile = readFileSync(`${repoRoot}/app/build.gradle.kts`, "utf8");
  expect(buildFile).toContain('applicationId = "com.easyui.launcher"');
  expect(buildFile).toContain("minSdk = 26");
  expect(buildFile).toContain("targetSdk = 35");
  recordScenario({
    ...scenarioMatrix.find((item) => item.id === "A1")!,
    status: "PASS",
    note: `Debug package=${appConfig.packageName}; release package=${appConfig.releasePackageName}.`,
  });
  writeMarkdown("notes/repo-audit.md", "Identity audit completed from current Gradle metadata.\n");
});
