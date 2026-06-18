---
name: spring-migrate
version: 2.0.0
description: Spring Boot 2.5→3.4 + Java 8→21 migration via OpenRewrite. Guardrails enforced. Caveman-compatible output.
author: e-SwaRa Technologies
tags: [java, spring-boot, openrewrite, migration, guardrails]
agents: [claude-code, github-copilot, cursor, windsurf, cline, codex]
---

# SPRING MIGRATION SKILL

## OUTPUT RULES (caveman mode — always active for migration responses)
- No filler. No pleasantries. No "Sure, I'll help with that."
- Use symbols: → = ✓ ✗ ⚠ ⛔ ✦
- Keywords + arrows over full sentences
- Code blocks: exact, no paraphrase
- EXCEPTION: Expand to full prose for ⛔ STOP events, security rewrites, destructive ops

---

## ACTIVATION
Trigger on any of:
- "migrate spring", "spring boot 3", "upgrade boot", "openrewrite", "/spring-migrate"
- File open: pom.xml + contains spring-boot-starter-parent version 2.x

---

## STATE MACHINE — ALWAYS FOLLOW THIS ORDER

```
SCAN → GUARDRAIL_CHECK → PHASE_SELECT → DRY_RUN → CONFIRM → APPLY → TEST → COMMIT → REPEAT
```

Never skip steps. Never jump to APPLY without DRY_RUN + CONFIRM.

---

## ⛔ GLOBAL STOP CONDITIONS
Check FIRST before any action. If any trigger → STOP, explain, wait for user fix.

| ID  | Check | Command | If Fail |
|-----|-------|---------|---------|
| G1  | Git clean | `git status --porcelain` | ⛔ STOP — commit/stash first |
| G2  | On feature branch | `git branch --show-current` | ⛔ STOP if main/master/develop |
| G3  | pom.xml exists | `ls pom.xml` | ⛔ STOP — not a Maven project |
| G4  | Tests pass (baseline) | `mvn test -q` | ⛔ STOP — fix tests before migration |
| G5  | Java target available | `java -version` | ⛔ STOP — install target JDK first |

⛔ STOP output = full prose (caveman OFF). State: what failed, exact fix command, estimated time.

---

## ⚠ PHASE GUARDRAILS

### Before Phase 1 (Boot 2.5→2.7)
```
WARN_P1_1: Spring Cloud present?
  cmd: grep "spring-cloud" pom.xml
  if yes → WARN: check compat matrix at spring.io/spring-cloud

WARN_P1_2: javax count
  cmd: grep -r "import javax\." src/main --include="*.java" | wc -l
  if > 500 → WARN: high effort. Budget 3+ hours for Phase 3.
  if > 100 → WARN: medium effort. Budget 1-2 hours for Phase 3.
```

### Before Phase 2 (Java 8→17)
```
STOP_P2_1: Java 17 installed?
  cmd: sdk list java | grep "17.*tem\|17.*zulu\|17.*graal"
  if not found → STOP. Install: sdk install java 17.0.9-tem

STOP_P2_2: Lombok version
  cmd: grep -A1 "lombok" pom.xml | grep version
  if version < 1.18.26 → STOP. Update lombok to 1.18.30 first.
```

### Before Phase 3 (Boot 2.7→3.x — THE BIG ONE)
```
STOP_P3_1: Must be on Java 17+
  cmd: java -version 2>&1 | grep -oP '(?<=version ")[0-9]+'
  if < 17 → STOP. Complete Phase 2 first.

STOP_P3_2: Springfox present?
  cmd: grep -i "springfox" pom.xml
  if found → WARN: will break. Plan Springdoc replacement.

STOP_P3_3: WebSecurityConfigurerAdapter present?
  cmd: grep -r "WebSecurityConfigurerAdapter" src/ --include="*.java" | wc -l
  if > 0 → WARN (COUNT) files need manual Security rewrite after OpenRewrite run.

STOP_P3_4: Spring Batch present?
  cmd: grep "spring-batch" pom.xml
  if found → WARN: @EnableBatchProcessing behavior inverted in Boot 3.
```

### Before Phase 4 (Boot 3.x→3.4)
```
INFO_P4_1: Already on Boot 3.4? Skip.
  cmd: grep -A2 "spring-boot-starter-parent" pom.xml | grep version
  if 3.4.x → SKIP this phase.
```

### Before Phase 5 (Java 17→21)
```
STOP_P5_1: Java 21 installed?
  cmd: sdk list java | grep "21.*tem\|21.*zulu"
  if not found → STOP. Install: sdk install java 21.0.1-tem

INFO_P5_2: Virtual threads available (Boot 3.2+)?
  cmd: grep -A2 "spring-boot-starter-parent" pom.xml | grep version
  if 3.2+ → INFORM: add spring.threads.virtual.enabled=true to get Loom.
```

---

## PHASE EXECUTION PROTOCOL

For EACH phase, follow EXACTLY:

```
1. RUN guardrails for that phase (see above)
2. ANNOUNCE: "Phase N: [name] — dry-run first"
3. SET recipe in pom.xml (exact value below)
4. RUN: mvn rewrite:dryRun 2>&1 | tail -20
5. SHOW: summary of what WILL change (file count, key changes)
6. ASK: "Apply? (y to proceed, n to skip)" — WAIT for user
7. IF yes: mvn rewrite:run -q
8. UPDATE pom.xml parent version manually (exact version below)
9. RUN: mvn clean verify -q
10. IF pass: git add -A && git commit -m "chore: [phase message]"
11. IF fail: SHOW compile errors, diagnose, suggest fix
12. REPORT: files changed, tests passed, next phase
```

---

## RECIPE CATALOG (exact values — no paraphrase)

| Phase | Recipe | Parent Version | Commit Message |
|-------|--------|---------------|----------------|
| 1 | `org.openrewrite.java.spring.boot2.UpgradeSpringBoot_2_7` | `2.7.18` | `chore: Spring Boot 2.5→2.7 via OpenRewrite [phase-1]` |
| 2 | `org.openrewrite.java.migrate.UpgradeToJava17` | (same) | `chore: Java 8→17 via OpenRewrite [phase-2]` |
| 3 | `org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_4` | `3.4.1` | `chore: Spring Boot 2.7→3.4 + javax→jakarta [phase-3]` |
| 4 | (skip if already 3.4) | `3.4.1` | N/A |
| 5 | `org.openrewrite.java.migrate.UpgradeToJava21` | (same) | `chore: Java 17→21 via OpenRewrite [phase-5]` |

pom.xml java.version property:
- Phase 2 → `<java.version>17</java.version>`
- Phase 5 → `<java.version>21</java.version>`

---

## OPENREWRITE PLUGIN BLOCK (inject into pom.xml if missing)

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.openrewrite.maven</groupId>
      <artifactId>rewrite-maven-plugin</artifactId>
      <version>5.40.0</version>
      <configuration>
        <activeRecipes>
          <!-- PHASE RECIPE HERE -->
        </activeRecipes>
      </configuration>
      <dependencies>
        <dependency>
          <groupId>org.openrewrite.recipe</groupId>
          <artifactId>rewrite-spring</artifactId>
          <version>5.22.0</version>
        </dependency>
        <dependency>
          <groupId>org.openrewrite.recipe</groupId>
          <artifactId>rewrite-migrate-java</artifactId>
          <version>2.28.0</version>
        </dependency>
        <dependency>
          <groupId>org.openrewrite.recipe</groupId>
          <artifactId>rewrite-jakarta-ee</artifactId>
          <version>2.8.0</version>
        </dependency>
      </dependencies>
    </plugin>
  </plugins>
</build>
```

---

## MANUAL FIX CATALOG (trigger after Phase 3 compile errors)

### Security Fix (⛔ expand — caveman OFF for this section)
Detect: `grep -r "WebSecurityConfigurerAdapter" src/ --include="*.java"`
Action: Show full side-by-side diff, explain Bean-based config, wait for confirmation before changes.

### javax Verification
```bash
# Should return EMPTY after Phase 3
grep -r "import javax\." src/main --include="*.java"
# If not empty → show remaining files, offer to fix manually
```

### H2 Dialect (tests fail)
Add to `src/test/resources/application.properties`:
```properties
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

### Springdoc replacement
Remove: `io.springfox:springfox-boot-starter`
Add:
```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.6.0</version>
</dependency>
```
Annotation map: `@Api→@Tag` `@ApiOperation→@Operation` `@ApiParam→@Parameter` `@ApiModel→@Schema`

### Virtual Threads (Phase 5 bonus)
```properties
spring.threads.virtual.enabled=true
```

---

## CAVEMAN TOKEN RULES FOR THIS SKILL

```
migration status report format:
  Phase N: [STATUS] → [NEXT]
  Changed: N files
  Tests: N pass / N fail
  Time: ~Xmin

DO use:
  "Phase 3 done. javax→0. Security: 2 files need rewrite. Next: Phase 5?"
  "⛔ G2 fail: on main branch. Create feature branch first."
  "⚠ P3_2: Springfox found. Plan Springdoc swap after OpenRewrite run."

DON'T use:
  "I've successfully completed the migration of your Spring Boot application..."
  "That's a great question! Let me help you with the Spring Boot migration..."
  "Sure! I'll be happy to run the OpenRewrite recipe for you..."
```

---

## SESSION MEMORY (maintain across turns)

Track across conversation:
```
MIGRATION_STATE = {
  currentPhase: 0-5,
  completedPhases: [],
  pendingManualFixes: [],
  javaxRemaining: null,
  wsca_files: null,
  branch: null
}
```

Each response: start with state summary (1 line caveman format).
Example: `[Phase 2/5 ✓ | Phase 3 next | Branch: migration/spring3-java21]`

---

## INTERACTION EXAMPLES

User: `/spring-migrate`
Response:
```
Spring Migration Skill active.
Running scan...
[output of scan commands]
State: Boot 2.5 + Java 8 detected. 5 phases available.
G1 ✓ | G2 ✓ (branch: migration/spring3) | G4 ✓ (tests: 47 pass)
javax count: 134 (medium effort)
WSCA found: 2 files (manual rewrite needed after Phase 3)
Springfox: NOT found ✓
Ready. Start Phase 1? (y/n)
```

User: `run phase 3`
Response:
```
[Phase 2/5 ✓ | Phase 3 → javax+jakarta+security]
P3 guardrails:
  STOP_P3_1: Java 17 ✓
  WARN_P3_3: WSCA found in 2 files (SecurityConfig.java, OAuth2Config.java)
  WARN_P3_2: Springfox NOT found ✓
Setting recipe: UpgradeSpringBoot_3_4
Dry-run → applying...
[dry-run output summary]
Apply? (y/n)
```

User: `what javax left`
Response:
```
cmd: grep -r "import javax\." src/main --include="*.java"
[output]
```

---

## REUSE CHECKLIST (for new service)

Before running skill on a new service:
```
□ New git branch created
□ scan-migration.sh output reviewed
□ Spring Cloud version noted (if present)
□ javax import count noted
□ WSCA file count noted
□ Springfox presence noted
□ JUnit 4 count noted
□ Test suite baseline captured (mvn test)
□ Java 17 available on machine
□ Java 21 available on machine (for Phase 5)
```
