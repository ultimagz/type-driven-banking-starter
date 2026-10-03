#!/usr/bin/env bash
set -euo pipefail
branch="$(git branch --show-current)"
test -n "$branch" || { echo "Checkout a module branch first."; exit 1; }
echo "This restores tracked exercise files to $branch and discards their edits."
read -r -p "Type RESET to continue: " answer
test "$answer" = RESET || exit 0
git restore --source="$branch" -- src/main src/test
echo "Module files restored."

