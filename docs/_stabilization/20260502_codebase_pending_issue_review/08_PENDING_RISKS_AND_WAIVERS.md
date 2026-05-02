# Pending Risks and Waivers

| Issue | Severity | Evidence | Fixed? | Waiver Allowed? | Release Impact | Next Action |
|-------|----------|----------|--------|-----------------|----------------|-------------|
| OEM launcher/default-home behavior | High | Manual testing requires device variation handling. Android APIs for this vary by OEM. | No | Yes | May not be fully automatable across OEMs. User manually follows guidance. | Document behavior per top OEMs (Samsung, Xiaomi, Pixel). |
| Session timeout real timed manual verification | Medium | CaregiverDashboard uses timer but unit testing/real world timer is tricky. | No | Yes | None | Provide manual QA guide and verifiable states. |
| Emergency call safety | High | Tested to trigger Intents, not direct CALL. Tested as safe fallback. | Yes | Yes | None | None |
| Release signing/AAB status | Blocker | Missing keystore for release AAB. | No | Yes | Block Play Store upload until provided. | Create Keystore and configure variables. |
| Privacy/data safety still needed | Medium | Requires Play Console input. | No | Yes | Blocks store listing publication. | Fill out Data Safety form. |
| Play Store assets still needed | Medium | Needs screenshots and text. | No | Yes | Blocks store listing publication. | Generate marketing assets. |
| Large font/accessibility layout breaks | Low | Setup screens updated to scroll correctly with navigation insets. | Yes | No | None | N/A |
| Backup/restore real persistence | Low | Room database handles JSON correctly. End to end requires real device file system to pick files. | Yes | Yes | None | Verify file picking on real device. |
