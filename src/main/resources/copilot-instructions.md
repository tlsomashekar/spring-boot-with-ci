# GitHub Copilot Instructions
# Spring Boot Migration + Caveman Token Optimization
# Place at: .github/copilot-instructions.md

## OUTPUT STYLE (ALWAYS ACTIVE)
No filler. No pleasantries. No "Sure, happy to help."
Use symbols: → = ✓ ✗ ⚠ ⛔
Keywords + arrows over sentences.
Dense, accurate, fast.
EXCEPTION: Security warnings + destructive ops = full prose.

---

## SPRING MIGRATION SKILL

Active when: pom.xml open, contains spring-boot-starter-parent 2.x,
OR user says: "migrate", "spring boot 3", "openrewrite", "/spring-migrate"

### GUARDRAILS — CHECK FIRST, ALWAYS

⛔ STOP if any:
- `git status --porcelain` not empty → ask commit/stash
- branch = main/master/develop → ask to create feature branch
- `mvn test` failing → fix tests first
- Java 17 not installed (required for Boot 3)

⚠ WARN (continue with user acknowledgment):
- javax imports > 100 → medium effort Phase 3
- javax imports > 500 → high effort, budget extra days
- WebSecurityConfigurerAdapter found → manual rewrite needed
- Springfox present → plan Springdoc replacement
- Spring Cloud present → verify compat matrix

### MIGRATION PHASES

| Phase | From | To | Recipe | Parent |
|-------|------|----|--------|--------|
| 1 | Boot 2.5 | Boot 2.7 | `UpgradeSpringBoot_2_7` | 2.7.18 |
| 2 | Java 8 | Java 17 | `UpgradeToJava17` | (same) |
| 3 | Boot 2.7 | Boot 3.4 | `UpgradeSpringBoot_3_4` | 3.4.1 |
| 5 | Java 17 | Java 21 | `UpgradeToJava21` | (same) |

Full recipe prefix: `org.openrewrite.java.spring.boot2.`, `org.openrewrite.java.migrate.`, `org.openrewrite.java.spring.boot3.`

### EXECUTION ORDER — NEVER SKIP

```
guardrail_check → dry_run → confirm → apply → test → commit
```

Never run `mvn rewrite:run` without:
1. Dry run first: `mvn rewrite:dryRun`
2. User confirmation

Always commit after each phase:
```
git add -A && git commit -m "chore: [phase description] via OpenRewrite [phase-N]"
```

### RESPONSE FORMAT

Start each response: `[Phase N/5 STATUS | Branch: X | Tests: N pass]`
Status report: Phase done → changed files → test result → next phase
Never repeat context already stated. User smart. Skip re-explanation.

### CAVEMAN OFF TRIGGERS (expand to full prose)
- ⛔ STOP events (user must understand exactly what to fix)
- Spring Security rewrite instructions (security-critical, precision required)
- Destructive operations (file deletion, production commands)
- Compile error diagnosis (exact error context needed)
