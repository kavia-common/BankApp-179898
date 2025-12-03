# Pull Request Template – Java 21 / Spring Boot 3.2 Migration

## Title

`[Java21-Migration] <short summary of change>`

## Description

Provide a concise description of the changes in this PR:

- What part of the Java 21 / Spring Boot 3.2 migration does this address?
- Does it modify build configuration, security, OpenAPI, or business logic?

Example:

> This PR finalizes the Java 21 migration by cleaning up unused JAXB dependencies and tightening security around Swagger UI in production.

## Related Documents

- `docs/migration-summary.md`
- `docs/task-01-update-pom.md`
- `docs/task-02-security-config.md`
- `docs/task-03-openapi.md`
- `docs/task-04-javax-to-jakarta.md`
- `docs/task-05-jaxb-and-runtime.md`
- `docs/task-06-tests-and-ci.md`
- `docs/task-07-rollback-and-risk.md`
- `docs/architecture.md`
- `docs/changelog.md`
- `docs/manual-review-list.md`

## Changes

- [ ] Updated `pom.xml`
- [ ] Modified `SecurityConfig`
- [ ] Modified `OpenApiConfig`
- [ ] Updated `application.yml`
- [ ] Added/updated tests
- [ ] Documentation updates

Provide a brief bullets list:

- ...
- ...

## Verification

Describe how you validated these changes.

**Build and tests:**

```bash
./mvnw -DskipTests clean package
./mvnw test
./mvnw dependency:tree | sed -n '1,200p'
```

**Runtime checks (with app running on port 8989):**

```bash
curl -i http://localhost:8989/bank-api/healthz
curl -i http://localhost:8989/bank-api/actuator/health
curl -i http://localhost:8989/bank-api/v3/api-docs
curl -i http://localhost:8989/bank-api/swagger-ui/index.html
curl -i http://localhost:8989/bank-api/h2-console
```

Summarize results:

- [ ] All commands succeed locally
- [ ] All tests pass in CI
- [ ] No new warnings or regression failures

## Rollback Plan

If a problem is detected after deployment, how will you rollback?

- [ ] Revert this PR using `git revert` or platform UI.
- [ ] Redeploy the last known-good build.
- [ ] Confirm health endpoints and key flows after rollback.

Add details if needed:

- ...

## Manual Review Checklist (for reviewers)

Reviewers should validate:

- [ ] **Java and Spring Boot versions** – Confirm `pom.xml` uses Java 21 and Boot 3.2.10.
- [ ] **SecurityConfig** – Check:
  - [ ] Public endpoints are limited and intentional.
  - [ ] HTTP Basic is enforced where required.
  - [ ] CSRF and frame options configuration are appropriate.
- [ ] **OpenAPI paths** – Confirm:
  - [ ] `/bank-api/v3/api-docs` is reachable.
  - [ ] `/bank-api/swagger-ui/index.html` loads correctly.
- [ ] **JAXB and dependencies** – Ensure:
  - [ ] No redundant or conflicting JAXB artifacts in the dependency tree.
- [ ] **javax → jakarta** – Validate:
  - [ ] No new `javax.*` imports were introduced.
- [ ] **Tests and CI** – Confirm:
  - [ ] Tests cover new or changed logic where feasible.
  - [ ] CI is configured to run on Java 21.
- [ ] **Documentation** – Verify:
  - [ ] Relevant docs under `docs/` are updated for any significant changes.
