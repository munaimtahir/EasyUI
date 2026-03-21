import { defineConfig } from "@playwright/test";

export default defineConfig({
  testDir: "./tests",
  timeout: 180_000,
  outputDir: "../device_test_runs/playwright-output",
  fullyParallel: false,
  workers: 1,
  reporter: [["list"], ["html", { open: "never", outputFolder: "../device_test_runs/playwright-report" }]],
  use: {
    trace: "off",
    screenshot: "off",
    video: "off",
  },
});
