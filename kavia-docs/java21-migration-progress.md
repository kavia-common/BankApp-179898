# Java 21 Migration Progress Tracker

## Overview

This document tracks the step-by-step progress of migrating the BankApp-179898 project to Java 21 and Spring Boot 3.x. After completing each step, we will update the Status, and add any outputs, notes, and links to diffs or pull requests in the Notes/Links column. The intent is to keep a single source of truth for the migration state that is easy to scan and share.

Status values:
- To-do: Not started yet
- In-progress: Work underway
- Success: Completed and validated
- Blocked: Cannot proceed due to a dependency or issue (add explanation in Notes)

## Planned Steps

| Step ID | Description | Status | Notes/Links to diffs |
|---|---|---|---|
| 01.01 | Create tracker | To-do |  |
| 02.01 | Update Java version/toolchain | To-do |  |
| 02.02 | Upgrade Spring Boot | To-do |  |
| 02.03 | Update dependencies (H2, Spring Security, springdoc-openapi) | To-do |  |
| 02.04 | Update Maven Wrapper | To-do |  |
| 02.05 | Adjust .mvn/jvm.config | To-do |  |
| 03.01 | Code refactor to jakarta and Security 6 | To-do |  |
| 03.02 | Replace Springfox with Springdoc | To-do |  |
| 04.01 | Clean build on Java 21 | To-do |  |
| 05.01 | Run and smoke-test | To-do |  |
| 06.01 | Update docs | To-do |  |

## How to use this tracker

- After each step, change the Status and add a concise summary of what changed in the Notes/Links column. When available, include links to:
  - Commit hashes, PRs, or diffs
  - Build logs or test outputs
  - Any follow-up tasks or blockers discovered
- If a step is Blocked, include the reason and a link to the relevant issue or log, and optionally add a follow-up subtask to this list.

