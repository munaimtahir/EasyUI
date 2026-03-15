# CI/CD

## CI goals
- Keep main branch stable
- Catch regressions early
- Enforce formatting and linting
- Run tests automatically on pull requests

## CI pipeline
1. Checkout
2. Gradle cache restore
3. Static analysis (ktlint/detekt)
4. Unit tests
5. Assemble debug build
6. Optional instrumentation smoke tests
7. Upload artifacts and test reports

## Release workflow
1. Merge only after CI passes
2. Produce signed release build
3. Run manual QA checklist
4. Verify billing configuration in internal testing track
5. Publish to internal -> closed -> production tracks

## Store rollout strategy
- Internal testing first
- Closed testing with caregiver feedback
- Production staged rollout
- Monitor crashes and reviews before widening

## Versioning
Semantic-ish versioning:
- `0.x` for pre-production
- `1.0.0` for first stable release

## Observability
Keep analytics minimal and privacy-respecting.
Allowed:
- anonymous local error logs surfaced in QA builds
- privacy-light crash reporting if explicitly approved

Avoid:
- invasive session tracking
- third-party ad SDK noise in first release
