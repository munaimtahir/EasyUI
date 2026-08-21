# EasyUI v1.0 — Pilot Test Plan & Operational Protocol

## 1. Executive Summary & Objective

This document outlines the operational test protocol for the EasyUI v1.0 Senior Launcher and Caregiver Companion pilot. The primary objective is to validate real-world usability, accessibility, notification delivery, battery management resilience, and caregiver-senior trust dynamics across diverse Android hardware and OEM form factors prior to public store distribution.

---

## 2. Pilot Cohort & Scope

| Parameter | Specification |
| --------- | ------------- |
| **Cohort Size** | 5–10 Senior Devices paired with 5–10 Caregiver Devices |
| **Duration** | 14 calendar days (2 weeks) |
| **Target OS Range** | Android 9 (API 28) through Android 15 (API 35) |
| **OEM Diversity** | Samsung OneUI, Google Pixel / AOSP, Motorola, Xiaomi MIUI/HyperOS |
| **Network Conditions** | Residential Wi-Fi, 4G LTE, 5G NR, intermittent/low-signal rural connectivity |

---

## 3. Cohort Selection & Participant Criteria

### 3.1 Senior Participants
- Older adults seeking simplified, high-contrast, distraction-free smartphone access.
- Willingness to set EasyUI Senior Launcher as the default Android `HOME` application.
- Basic daily smartphone usage (phone calls, messaging, reminders, essential apps).

### 3.2 Caregiver Participants
- Family members, adult children, or designated caregivers supporting the senior.
- Own an Android smartphone running Android 9+.
- Willingness to monitor battery status, check-in pings, and assist in schedule/reminder coordination.

---

## 4. Key Metrics & Feedback Categories

| Category | Primary Metric / Evaluation Criteria | Target Threshold |
| -------- | ----------------------------------- | ---------------- |
| **1. Pairing Ease** | Time to complete 8-digit uppercase pairing without technical intervention | < 2 minutes; > 90% first-attempt success |
| **2. HOME Reliability** | Zero accidental exits to OEM launcher on back/home gestures | 100% home retention |
| **3. UI Readability** | Senior satisfaction on typography, button contrast, and scaling | > 90% positive readability score |
| **4. Font / Display Scaling** | Proper rendering under Android system large font / high-contrast settings | Zero layout clipping or overlapping text |
| **5. Reminder Reliability** | Exact-alarm trigger rate and audio/visual reminder adherence | 100% scheduled delivery |
| **6. SOS Usability** | Time to access SOS screen and trigger confirmation without false alarms | < 5 seconds to initiate; 0 false triggers |
| **7. Check-In Comprehension** | Senior clarity and daily adoption of the "I'm OK" status tap | > 85% daily check-in completion |
| **8. Notification Delivery** | Delivery latency of SOS and Check-In events to Caregiver Companion | < 30 seconds latency |
| **9. OEM Battery Optimization** | Background worker survival against aggressive OEM battery killers | > 95% 15-minute sync interval compliance |
| **10. Reconnect Behavior** | Automatic sync resumption after airplane mode / cellular dropouts | Immediate recovery upon reconnect |
| **11. Caregiver Comprehension** | Caregiver understanding of scoped permissions vs invasive surveillance | 100% clarity on privacy boundaries |
| **12. Revocation Confidence** | Ease with which seniors can disconnect caregiver access at any time | Instant disconnection verified |

---

## 5. Pilot Onboarding & Execution Schedule

### Phase I: Onboarding & Setup (Days 1–2)
1. Provide participant packet with privacy charter and instructions.
2. Install `senior-launcher-release.apk` on Senior device; set as Default HOME.
3. Configure Caregiver PIN, emergency contact, and top 4 favorite apps.
4. Install `caregiver-companion-release.apk` on Caregiver device.
5. Generate pairing code on Senior device and establish linked pairing.
6. Verify initial battery level and charging status sync on Caregiver dashboard.

### Phase II: Active Daily Usage (Days 3–12)
- **Daily Actions**:
  - Senior completes one daily "I'm OK" check-in.
  - Caregiver pushes at least 1 weekly reminder or schedule item.
  - Regular phone calls and app launching via home grid.
- **Weekly Check-Ins**:
  - Day 7 & Day 14 structured 10-minute feedback interview.

### Phase III: Boundary & Edge Testing (Day 13)
1. **Network Disruption**: Place Senior phone in airplane mode for 4 hours; verify HOME remains fully usable.
2. **Reboot Test**: Senior phone power cycled; confirm Senior Launcher starts seamlessly on boot.
3. **Revocation Test**: Senior disconnects Caregiver; verify Companion UI resets to unlinked state immediately.
4. **Data Purge Test**: Caregiver deletes account; verify complete server-side token eradication.

### Phase IV: Debrief & Wrap-Up (Day 14)
- Collect telemetry logs (if consented) and final satisfaction surveys.
- Analyze defect tickets and prioritize v1.0.1 fixes.

---

## 6. Defect Severity & Escalation SLAs

| Severity | Definition | Target Resolution SLA |
| -------- | ---------- | --------------------- |
| **Blocker (P0)** | Launcher crashes preventing phone use, dialer inaccessibility, inability to reach HOME | Immediate fix (< 24 hours) |
| **Critical (P1)** | Pairing failure, false SOS triggers, reminder delivery failure on OEM devices | < 48 hours |
| **Major (P2)** | Battery sync lag > 1 hour, UI clipping on specific screen aspect ratios | Next pilot build (< 5 days) |
| **Minor (P3)** | Cosmetic alignment, wording ambiguities, animation stutter | v1.0.1 release backlog |

---

## 7. Privacy, Consent & Data Governance

1. **Explicit Consent**: Participants must sign/acknowledge the Pilot Consent Form detailing what telemetry and status metrics are synchronized.
2. **No Invasive Monitoring**: No microphone, camera, SMS body, or call recording telemetry is ever captured or transmitted.
3. **Data Isolation**: Pilot data is segregated on staging servers and purged within 30 days of pilot completion.
4. **Right to Revoke**: Participants may revoke pairing or delete their device data from within the app at any point with immediate effect.
