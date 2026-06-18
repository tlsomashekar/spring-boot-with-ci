#!/bin/bash
# ═══════════════════════════════════════════════════════════════
#  Spring Migration Skill Installer
#  Installs: caveman (token optimization) + spring-migrate skill
#  Supports: GitHub Copilot, Claude Code, Cursor, Windsurf, Cline
#
#  Usage:
#    ./install.sh                    # auto-detect agent
#    ./install.sh --agent copilot    # GitHub Copilot only
#    ./install.sh --agent claude     # Claude Code only
#    ./install.sh --agent all        # all agents
#    ./install.sh --dry-run          # preview only
# ═══════════════════════════════════════════════════════════════

set -e
BOLD='\033[1m'; CYAN='\033[0;36m'; GREEN='\033[0;32m'
RED='\033[0;31m'; YELLOW='\033[1;33m'; NC='\033[0m'

AGENT="auto"
DRY_RUN=false
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Parse args
while [[ $# -gt 0 ]]; do
  case $1 in
    --agent) AGENT="$2"; shift 2 ;;
    --dry-run) DRY_RUN=true; shift ;;
    *) echo "Unknown: $1"; exit 1 ;;
  esac
done

log()  { echo -e "${CYAN}[install]${NC} $1"; }
ok()   { echo -e "${GREEN}[✓]${NC} $1"; }
warn() { echo -e "${YELLOW}[⚠]${NC} $1"; }
err()  { echo -e "${RED}[✗]${NC} $1"; }
dry()  { echo -e "${YELLOW}[dry-run]${NC} would run: $1"; }

echo -e "\n${BOLD}${CYAN}Spring Migration Skill + Caveman Installer${NC}\n"

# ── Guard: Node.js required for npx ───────────────────────────
if ! command -v node &>/dev/null; then
  err "Node.js not found. Install from nodejs.org (v18+) then re-run."
  exit 1
fi
NODE_VER=$(node -v | grep -oP '(?<=v)[0-9]+')
[ "$NODE_VER" -lt 18 ] && { err "Node.js 18+ required (found v$NODE_VER)"; exit 1; }
ok "Node.js $(node -v)"

# ── Auto-detect agent ──────────────────────────────────────────
detect_agent() {
  if [ -d ".github" ] && command -v gh &>/dev/null; then
    echo "github-copilot"
  elif [ -d ".claude" ] || command -v claude &>/dev/null; then
    echo "claude-code"
  elif [ -d ".cursor" ]; then
    echo "cursor"
  else
    echo "github-copilot"  # default for Spring/Java dev
  fi
}

[ "$AGENT" = "auto" ] && AGENT=$(detect_agent) && log "Detected agent: $AGENT"

# ── Step 1: Install caveman ────────────────────────────────────
echo -e "\n${BOLD}Step 1: Install caveman (token optimizer)${NC}"
log "caveman cuts ~65% output tokens. Keeps full technical accuracy."
log "Caveman stays OFF for security warnings + destructive ops (guardrail)."

case "$AGENT" in
  github-copilot|copilot)
    CMD="npx skills@latest add JuliusBrussee/caveman -a github-copilot"
    ;;
  claude-code|claude)
    CMD="npx skills@latest add JuliusBrussee/caveman"
    ;;
  cursor)
    CMD="npx skills@latest add JuliusBrussee/caveman -a cursor"
    ;;
  windsurf)
    CMD="npx skills@latest add JuliusBrussee/caveman -a windsurf"
    ;;
  cline)
    CMD="npx skills@latest add JuliusBrussee/caveman -a cline"
    ;;
  all)
    CMD="npx -y github:JuliusBrussee/caveman"
    ;;
  *)
    warn "Unknown agent '$AGENT'. Defaulting to github-copilot."
    CMD="npx skills@latest add JuliusBrussee/caveman -a github-copilot"
    ;;
esac

if $DRY_RUN; then
  dry "$CMD"
else
  log "Running: $CMD"
  eval "$CMD" && ok "caveman installed"
fi

# ── Step 2: Install spring-migrate skill ──────────────────────
echo -e "\n${BOLD}Step 2: Install spring-migrate skill${NC}"

install_skill_copilot() {
  local target=".github/copilot-instructions.md"
  mkdir -p .github
  if [ -f "$target" ]; then
    warn "Existing $target found. Appending migration skill..."
    echo "" >> "$target"
    echo "---" >> "$target"
    cat "$SCRIPT_DIR/.github/copilot-instructions.md" >> "$target"
  else
    cp "$SCRIPT_DIR/.github/copilot-instructions.md" "$target"
  fi
  ok "Migration skill → $target"
}

install_skill_claude() {
  mkdir -p .claude/skills
  cp "$SCRIPT_DIR/SKILL.md" .claude/skills/spring-migrate.md
  ok "Migration skill → .claude/skills/spring-migrate.md"
}

install_skill_cursor() {
  mkdir -p .cursor/rules
  cp "$SCRIPT_DIR/SKILL.md" .cursor/rules/spring-migrate.mdc
  ok "Migration skill → .cursor/rules/spring-migrate.mdc"
}

install_skill_generic() {
  cp "$SCRIPT_DIR/SKILL.md" .spring-migrate.md
  ok "Migration skill → .spring-migrate.md (reference manually in agent)"
}

if $DRY_RUN; then
  dry "install skill for $AGENT"
else
  case "$AGENT" in
    github-copilot|copilot) install_skill_copilot ;;
    claude-code|claude)     install_skill_claude ;;
    cursor)                 install_skill_cursor ;;
    all)
      install_skill_copilot
      install_skill_claude
      install_skill_cursor ;;
    *) install_skill_generic ;;
  esac
fi

# ── Step 3: Copy utility scripts ──────────────────────────────
echo -e "\n${BOLD}Step 3: Copy utility scripts${NC}"
for script in scan-migration.sh post-migration-validate.sh; do
  if [ -f "$SCRIPT_DIR/$script" ]; then
    if $DRY_RUN; then
      dry "copy $script to project root"
    else
      cp "$SCRIPT_DIR/$script" "./$script"
      chmod +x "./$script"
      ok "Copied: $script"
    fi
  fi
done

# ── Done ──────────────────────────────────────────────────────
echo -e "\n${BOLD}${GREEN}═══════════════════════════════════════${NC}"
echo -e "${BOLD}${GREEN}  Install complete!${NC}"
echo -e "${BOLD}${GREEN}═══════════════════════════════════════${NC}"

echo -e "\n${BOLD}Activate:${NC}"
case "$AGENT" in
  github-copilot|copilot)
    echo "  Open Copilot Chat → type: /spring-migrate"
    echo "  Or: 'analyze my pom.xml for migration'"
    ;;
  claude-code|claude)
    echo "  Type: /spring-migrate"
    echo "  Or: /caveman then ask about migration"
    ;;
  cursor)
    echo "  Open Copilot/AI chat in Cursor → mention migration"
    ;;
esac

echo -e "\n${BOLD}Token savings:${NC}"
echo "  caveman → ~65% fewer output tokens"
echo "  Migration skill → structured responses, no repetition"
echo "  Combined → agent stays focused, uses budget on decisions not prose"
echo ""
echo "  Caveman OFF for: ⛔ STOP events, security rewrites, destructive ops"
echo ""
