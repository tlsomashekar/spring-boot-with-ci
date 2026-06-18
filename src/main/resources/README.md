# Spring Migration Skill + Caveman

> Spring Boot 2.5→3.4 + Java 8→21 · OpenRewrite · Token-efficient · Guardrails enforced

---

## What This Is

A reusable **AI skill** for GitHub Copilot (and 40+ other agents) that guides a Spring Boot migration step by step — with mandatory guardrails preventing unsafe actions, and **caveman** keeping responses dense and token-efficient during the long multi-phase run.

```
You ask → Guardrails check → Dry-run → You confirm → Apply → Test → Commit → Next phase
```

---

## Why Caveman + Migration Skill Together

**Problem with long migrations:**
Each phase produces long AI responses. Over 5 phases × 3-5 exchanges = 50+ turns.
Standard AI output wastes tokens on filler: "Sure! I'd be happy to run the OpenRewrite recipe for your Spring Boot migration project..."

**Caveman fix:**
Drops filler, keeps facts. Same information, ~65% fewer tokens.
`"Phase 3 done. javax→0. WSCA: 2 files need manual rewrite. Next: Phase 5?"` → enough.

**Caveman guardrail:**
Caveman mode **auto-disables** for:
- ⛔ STOP events (user must understand what to fix)
- Spring Security rewrite (precision matters)
- Destructive operations (need full clarity)

This matches exactly what the migration skill needs — fast for routine steps, verbose when stakes are high.

---

## Install

### One Command (auto-detects agent)

```bash
# Clone or copy this skill folder to your project, then:
chmod +x install.sh && ./install.sh
```

### Agent-Specific

```bash
# GitHub Copilot
./install.sh --agent github-copilot

# Claude Code
./install.sh --agent claude

# Cursor
./install.sh --agent cursor

# All agents at once
./install.sh --agent all

# Preview only (no changes)
./install.sh --dry-run
```

### Manual Install (Copilot)

```bash
# Step 1: Install caveman
npx skills@latest add JuliusBrussee/caveman -a github-copilot

# Step 2: Add migration instructions
cp .github/copilot-instructions.md .github/copilot-instructions.md
# (or append to existing file)
```

### Manual Install (Claude Code)

```bash
# Step 1: Install caveman
npx skills@latest add JuliusBrussee/caveman

# Step 2: Add migration skill
mkdir -p .claude/skills
cp SKILL.md .claude/skills/spring-migrate.md
```

---

## Activate

| Agent | Command |
|-------|---------|
| GitHub Copilot | Type `/spring-migrate` in Copilot Chat |
| Claude Code | `/spring-migrate` or `/caveman` then ask |
| Cursor | Mention "migrate spring boot" in AI chat |
| Any | Open pom.xml + ask about migration |

---

## Guardrail Reference

### Global STOP (blocks all phases)
| ID | Condition | Fix |
|----|-----------|-----|
| G1 | Git not clean | `git add -A && git stash` |
| G2 | On main/master branch | `git checkout -b migration/spring3-java21` |
| G3 | No pom.xml | Wrong directory |
| G4 | Tests failing | Fix tests first (baseline needed) |
| G5 | Java version unavailable | `sdk install java 17.0.9-tem` |

### Per-Phase WARN
- **Phase 1**: Spring Cloud → check compat matrix
- **Phase 2**: Lombok < 1.18.26 → update first
- **Phase 3**: WebSecurityConfigurerAdapter → plan manual rewrite
- **Phase 3**: Springfox → plan Springdoc replacement
- **Phase 5**: Spring Boot < 3.2 → no virtual threads

---

## File Structure

```
spring-migration-skill/
├── SKILL.md                          ← Main skill (all agents)
├── install.sh                        ← One-command installer
├── README.md                         ← This file
├── .github/
│   └── copilot-instructions.md      ← Copilot-specific activation
├── scan-migration.sh                 ← Pre-flight scanner
└── post-migration-validate.sh        ← Post-migration checker
```

---

## How Caveman Saves Tokens Here

```
Standard response (Phase 3 status):
"I've successfully completed Phase 3 of the Spring Boot migration.
The OpenRewrite recipe has been applied and has made changes to 47 files.
All javax imports have been converted to jakarta imports. There are 2 files
that still require manual attention for the Spring Security configuration..."
→ ~80 tokens

Caveman response:
"Phase 3 ✓. 47 files changed. javax→0. WSCA: 2 files (SecurityConfig.java,
OAuth2Config.java) need manual rewrite. Tests: 44/47 pass. Next: fix 3 test
failures (H2 dialect), then Phase 5?"
→ ~45 tokens (-44%)
```

Over 50 turns: saves ~1,750 tokens. At Copilot scale = meaningful cost/speed difference.

---

## Reuse on New Service

```bash
# 1. Copy skill folder to new project root
cp -r spring-migration-skill/ /path/to/new-service/
cd /path/to/new-service/

# 2. Run installer
./spring-migration-skill/install.sh

# 3. Open agent, activate skill
/spring-migrate

# 4. Agent runs scan, checks guardrails, guides migration
```

Skill is stateless per conversation — always starts with scan.
No project-specific config needed.

---

## Compatibility

| Tested With | Status |
|-------------|--------|
| Spring Boot 2.5.x | ✓ Source |
| Spring Boot 2.6.x, 2.7.x | ✓ Intermediate |
| Spring Boot 3.x | ✓ Target |
| Java 8, 11 | ✓ Source |
| Java 17, 21 | ✓ Target |
| Maven | ✓ |
| Gradle | ⚠ Phase 3 recipe works; plugin syntax differs |
| GitHub Copilot | ✓ |
| Claude Code | ✓ |
| Cursor | ✓ |
| Windsurf | ✓ |
| Cline | ✓ |
