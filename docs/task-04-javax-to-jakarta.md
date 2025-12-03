# Task 04 – javax.* to jakarta.* Migration

## Purpose

This document describes the strategy, checks, and outcomes for migrating from `javax.*` to `jakarta.*` APIs as part of the Java 21 / Spring Boot 3.2.x upgrade.

## Files Changed

- None in this repository state.

The existing codebase already uses `jakarta.persistence.*` imports for JPA entities and does not contain any `javax.*` imports.

## Automated Search

To verify that no `javax.*` imports remain, you can run the following commands from the `BankApp-179898` project root.

### 1. Grep for javax imports

```bash
# Recursive search for javax.* imports in Java sources
grep -RIn "javax\." src || echo "No javax.* imports found"
```

### 2. Optional: Sed-based conversion (dry-run)

If `javax.*` imports were present in a different branch or fork, a typical conversion approach might look like:

```bash
# Example (DO NOT run blindly; review diffs first)
# Convert javax.persistence to jakarta.persistence
grep -RIl "javax.persistence" src | xargs sed -i.bak 's#javax\.persistence#jakarta.persistence#g'

# Convert javax.validation to jakarta.validation
grep -RIl "javax.validation" src | xargs sed -i.bak 's#javax\.validation#jakarta.validation#g'
```

In this repository, running the initial `grep` should result in `No javax.* imports found`.

## Expected Output

```bash
$ grep -RIn "javax\." src || echo "No javax.* imports found"
No javax.* imports found
```

No code changes are required for `javax`→`jakarta` in this state.

## Rollback Steps

If you ever run `sed`-based conversions in another environment or branch:

1. Restore from Git:

   ```bash
   git restore src
   ```

   or:

   ```bash
   git checkout -- src
   ```

2. Remove transient `.bak` files, if any:

   ```bash
   find src -name "*.bak" -delete
   ```

3. Rebuild and retest:

   ```bash
   ./mvnw -DskipTests clean package
   ./mvnw test
   ```

## Manual Review Notes

- **Current state:** No `javax.*` imports are present; entities already use `jakarta.persistence.*`. No manual review is required for this migration step.
- If you introduce new modules or copy code from older Java EE / Spring 5 projects, ensure that:
  - `javax.persistence.*` imports are updated to `jakarta.persistence.*`.
  - `javax.validation.*` imports are updated to `jakarta.validation.*`.
  - Any remaining Java EE dependencies are replaced with Jakarta equivalents.
- If a future `grep` reveals `javax.*` imports, document each changed file and add corresponding entries to `docs/manual-review-list.md` for human validation.
