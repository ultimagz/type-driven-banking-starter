#!/usr/bin/env bash
set -euo pipefail
branch="$(git branch --show-current)"
echo "Current branch: ${branch}"
echo "Run: gradle test"
echo "Read: docs/modules/${branch#module-[0-9][0-9]-}.md"

