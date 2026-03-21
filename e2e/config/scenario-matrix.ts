export type ScenarioStatus =
  | "PASS"
  | "FAIL"
  | "BLOCKED"
  | "PARTIALLY_VERIFIED"
  | "OEM_DEPENDENT"
  | "OUT_OF_SCOPE_BY_PRODUCT_GUARDRAIL";

export type Severity = "P0" | "P1" | "P2" | "P3";

export type ScenarioDefinition = {
  id: string;
  suite: string;
  scenario: string;
  priority: Severity;
};

export const scenarioMatrix: ScenarioDefinition[] = [
  { id: "A1", suite: "00_static", scenario: "Repository and identity audit", priority: "P1" },
  { id: "A2", suite: "00_static", scenario: "Launcher manifest audit", priority: "P1" },
  { id: "A3", suite: "00_static", scenario: "Permission declaration audit", priority: "P2" },
  { id: "A4", suite: "00_static", scenario: "Build health", priority: "P0" },
  { id: "A5", suite: "00_static", scenario: "Docs-to-code alignment", priority: "P2" },
  { id: "B1", suite: "10_install_and_first_run", scenario: "Fresh install and first-run flow", priority: "P0" },
  { id: "B1A", suite: "10_install_and_first_run", scenario: "Default launcher guidance and HOME role verification", priority: "P1" },
  { id: "B2", suite: "20_launcher_core", scenario: "Home button and launcher root behavior", priority: "P1" },
  { id: "B3", suite: "30_senior_home", scenario: "Home visibility and tile clarity", priority: "P1" },
  { id: "B4", suite: "30_senior_home", scenario: "Simple app access", priority: "P1" },
  { id: "B5", suite: "40_essential_actions", scenario: "Essential actions", priority: "P1" },
  { id: "B6", suite: "50_caregiver", scenario: "Caregiver entry and protection", priority: "P1" },
  { id: "B7", suite: "60_layout_and_visibility", scenario: "Layout stability and hidden apps", priority: "P1" },
  { id: "B8", suite: "70_accessibility_and_display", scenario: "Accessibility and display resilience", priority: "P2" },
  { id: "B9", suite: "90_offline_and_guardrails", scenario: "Offline-first behavior", priority: "P1" },
  { id: "B10", suite: "80_permissions_oem_resilience", scenario: "Permission and OEM resilience", priority: "P2" }
];
