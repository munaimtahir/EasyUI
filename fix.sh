#!/usr/bin/env bash
set -Eeuo pipefail

echo "=== Git repo recovery starting ==="

# ---------- helpers ----------
fail() {
  echo
  echo "ERROR: $1" >&2
  exit 1
}

need_cmd() {
  command -v "$1" >/dev/null 2>&1 || fail "Required command not found: $1"
}

need_cmd git
need_cmd tar
need_cmd date

# ---------- make sure we are in repo ----------
git rev-parse --show-toplevel >/dev/null 2>&1 || fail "This directory is not a Git working tree."

REPO_ROOT="$(git rev-parse --show-toplevel)"
cd "$REPO_ROOT"

echo "Repo root: $REPO_ROOT"

[ -d ".git" ] || fail ".git directory not found."

TS="$(date +%Y%m%d_%H%M%S)"
BACKUP_DIR="../git_recovery_${TS}"
mkdir -p "$BACKUP_DIR"

echo "Backup folder: $BACKUP_DIR"

# ---------- collect repo info before touching .git ----------
REMOTE_URL=""
CURRENT_BRANCH=""
CURRENT_HEAD=""

if git remote get-url origin >/dev/null 2>&1; then
  REMOTE_URL="$(git remote get-url origin || true)"
fi

if git rev-parse --abbrev-ref HEAD >/dev/null 2>&1; then
  CURRENT_BRANCH="$(git rev-parse --abbrev-ref HEAD || true)"
fi

if git rev-parse HEAD >/dev/null 2>&1; then
  CURRENT_HEAD="$(git rev-parse HEAD || true)"
fi

echo "origin remote : ${REMOTE_URL:-<not found>}"
echo "current branch: ${CURRENT_BRANCH:-<unknown>}"
echo "current HEAD  : ${CURRENT_HEAD:-<unknown>}"

[ -n "$REMOTE_URL" ] || fail "Could not detect origin remote URL. Stop here to avoid rebuilding without a remote."

if [ -z "$CURRENT_BRANCH" ] || [ "$CURRENT_BRANCH" = "HEAD" ]; then
  fail "Could not detect a normal current branch name."
fi

# ---------- save evidence ----------
{
  echo "timestamp=$TS"
  echo "repo_root=$REPO_ROOT"
  echo "remote_url=$REMOTE_URL"
  echo "current_branch=$CURRENT_BRANCH"
  echo "current_head=$CURRENT_HEAD"
} > "$BACKUP_DIR/recovery_info.txt"

echo "Saving repo status and diagnostics..."
git status --short > "$BACKUP_DIR/status_short.txt" 2>/dev/null || true
git status > "$BACKUP_DIR/status_full.txt" 2>/dev/null || true
git branch -vv > "$BACKUP_DIR/branch_vv.txt" 2>/dev/null || true
git remote -v > "$BACKUP_DIR/remotes.txt" 2>/dev/null || true
ls -lah .git/objects/pack > "$BACKUP_DIR/pack_listing.txt" 2>/dev/null || true
git fsck --full > "$BACKUP_DIR/fsck.txt" 2>&1 || true

# ---------- save tracked changes patch ----------
echo "Saving tracked changes patch..."
git diff > "$BACKUP_DIR/tracked_changes_worktree.patch" 2>/dev/null || true
git diff --cached > "$BACKUP_DIR/tracked_changes_index.patch" 2>/dev/null || true

# ---------- save untracked files ----------
echo "Saving untracked files archive..."
UNTRACKED_LIST="$BACKUP_DIR/untracked_files.txt"
git ls-files --others --exclude-standard > "$UNTRACKED_LIST" 2>/dev/null || true

if [ -s "$UNTRACKED_LIST" ]; then
  tar -czf "$BACKUP_DIR/untracked_files.tar.gz" -T "$UNTRACKED_LIST"
  echo "Untracked files archived."
else
  echo "No untracked files found." | tee "$BACKUP_DIR/untracked_files_note.txt"
fi

# ---------- backup broken .git ----------
echo "Backing up existing .git..."
mv .git "$BACKUP_DIR/.git_broken_backup"

# ---------- rebuild git metadata ----------
echo "Reinitializing repository..."
git init

echo "Reattaching remote..."
git remote add origin "$REMOTE_URL"

echo "Fetching from origin..."
git fetch origin --prune

# ---------- restore branch tracking ----------
echo "Checking out branch: $CURRENT_BRANCH"

if git show-ref --verify --quiet "refs/remotes/origin/$CURRENT_BRANCH"; then
  # Create/reset local branch to track remote branch
  git checkout -B "$CURRENT_BRANCH" --track "origin/$CURRENT_BRANCH"
else
  fail "Remote branch origin/$CURRENT_BRANCH not found after fetch."
fi

# ---------- final status ----------
echo
echo "=== Recovery completed ==="
echo "Backup directory: $BACKUP_DIR"
echo
echo "Run these next:"
echo "  git status"
echo "  git log --oneline -n 5"
echo
echo "If needed, review preserved changes here:"
echo "  $BACKUP_DIR/tracked_changes_worktree.patch"
echo "  $BACKUP_DIR/tracked_changes_index.patch"
echo "  $BACKUP_DIR/untracked_files.tar.gz"
echo
echo "If your working files look different than expected, stop and inspect the backup before doing anything else."
