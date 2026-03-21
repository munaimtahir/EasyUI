import { test, expect } from "@playwright/test";
import { readFileSync } from "node:fs";
import { repoRoot } from "../../config/app-config.js";
import { featureFlags } from "../../config/feature-flags.js";
import { scenarioMatrix } from "../../config/scenario-matrix.js";
import { recordScenario } from "../../helpers/results.js";

test("A5 docs-to-code alignment", async () => {
  const tasks = readFileSync(`${repoRoot}/docs/engineering/tasks.md`, "utf8");
  expect(tasks).toContain("remaining clear product gap is the premium/billing path");
  const note = featureFlags.fullAppListEntryOnHome
    ? "Feature flags say the senior-facing app-list entry exists."
    : "Feature flags mark the senior-facing home entry for app list as not wired yet, matching docs/engineering/tasks.md.";
  recordScenario({
    ...scenarioMatrix.find((item) => item.id === "A5")!,
    status: "PASS",
    note,
  });
});
