import { test, expect } from "@playwright/test";
import { scenarioMatrix } from "../../config/scenario-matrix.js";
import { appConfig } from "../../config/app-config.js";
import { recordScenario } from "../../helpers/results.js";
import { runGradle } from "../../helpers/adb.js";

test("A4 build health", async () => {
  const output = runGradle(appConfig.gradleBuildCommand);
  expect(output).toContain("BUILD SUCCESSFUL");
  recordScenario({
    ...scenarioMatrix.find((item) => item.id === "A4")!,
    status: "PASS",
    note: "assembleDebug and testDebugUnitTest completed successfully.",
  });
});
