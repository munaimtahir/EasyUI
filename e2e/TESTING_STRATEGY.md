# Testing Strategy

This harness follows the EasyUI testing plan:

- protect launcher truth, not kiosk assumptions
- keep single-device interaction serial
- collect screenshot, UI dump, and notes for each scenario
- distinguish `PASS`, `FAIL`, `BLOCKED`, `PARTIALLY_VERIFIED`, `OEM_DEPENDENT`, and `OUT_OF_SCOPE_BY_PRODUCT_GUARDRAIL`

Current execution bias:

- static checks are authoritative
- smoke device checks are automated
- broader device coverage is scaffolded and can be expanded per feature maturity
