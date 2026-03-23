import type { TestInfo } from "@playwright/test";
import type { ScenarioDefinition, ScenarioStatus, Severity } from "../config/scenario-matrix.js";
import { attachEvidence, captureEvidence } from "./evidence.js";
import { recordIssue, recordScenario } from "./results.js";

export async function withScenario(
  testInfo: TestInfo,
  serial: string,
  scenario: ScenarioDefinition,
  run: () => Promise<{ status?: ScenarioStatus; note: string; issue?: { title: string; severity: Severity; actual: string; expected: string; suspectedArea: string; recommendation: string } }>,
): Promise<void> {
  try {
    const result = await run();
    captureEvidence(serial, scenario.id, result.note);
    recordScenario({
      id: scenario.id,
      suite: scenario.suite,
      scenario: scenario.scenario,
      priority: scenario.priority,
      status: result.status ?? "PASS",
      note: result.note,
      issueId: result.issue ? `${scenario.id}-ISSUE` : undefined,
      severity: result.issue?.severity,
      suspectedArea: result.issue?.suspectedArea,
    });
    if (result.issue) {
      recordIssue({
        issueId: `${scenario.id}-ISSUE`,
        title: result.issue.title,
        severity: result.issue.severity,
        type: "behavior",
        workflow: scenario.scenario,
        steps: [`Run scenario ${scenario.id}`],
        expected: result.issue.expected,
        actual: result.issue.actual,
        evidenceId: scenario.id,
        suspectedArea: result.issue.suspectedArea,
        recommendation: result.issue.recommendation,
      });
    }
  } catch (error) {
    captureEvidence(serial, scenario.id, `Unhandled failure: ${(error as Error).message}`);
    recordScenario({
      id: scenario.id,
      suite: scenario.suite,
      scenario: scenario.scenario,
      priority: scenario.priority,
      status: "FAIL",
      note: (error as Error).message,
      issueId: `${scenario.id}-ISSUE`,
      severity: scenario.priority,
      suspectedArea: "Needs triage",
    });
    recordIssue({
      issueId: `${scenario.id}-ISSUE`,
      title: `${scenario.scenario} failed`,
      severity: scenario.priority,
      type: "behavior",
      workflow: scenario.scenario,
      steps: [`Run scenario ${scenario.id}`],
      expected: "Scenario should complete successfully",
      actual: (error as Error).message,
      evidenceId: scenario.id,
      suspectedArea: "Needs triage",
      recommendation: "Review evidence and reproduce manually on the same device state.",
    });
    attachEvidence(testInfo, scenario.id);
    throw error;
  }
  attachEvidence(testInfo, scenario.id);
}
