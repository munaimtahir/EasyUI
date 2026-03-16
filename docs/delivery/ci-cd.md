# CI/CD

## Goals

- keep `main` stable
- catch regressions early
- enforce formatting and linting
- run tests on pull requests

## CI pipeline

1. Checkout
2. Restore Gradle cache
3. Run static analysis
4. Run unit tests
5. Assemble debug build
6. Optionally run instrumentation smoke tests
7. Publish build artifacts and reports

## Release flow

1. Merge only after CI passes
2. Produce signed release build
3. Run manual QA checklist
4. Verify billing configuration in internal testing
5. Promote internal -> closed -> production

## Rollout

- internal testing first
- closed testing with caregivers
- staged production rollout
- monitor crashes and reviews before widening
