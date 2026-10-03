#!/usr/bin/env bash
set -euo pipefail
branch="$(git branch --show-current)"
echo "Current branch: ${branch}"
if [[ -f gradlew ]]; then
  echo "JDK 21; run: ./gradlew test && ./gradlew exerciseTest"
else
  echo "JDK 21; run: gradle test"
fi
lesson="docs/modules/${branch}.md"
if [[ -f "$lesson" ]]; then
  echo "Read: $lesson"
elif [[ "$branch" == main && -f docs/modules/module-11-arrow.md ]]; then
  echo "Read: docs/modules/module-11-arrow.md"
else
  echo "Read: docs/BRANCH_MAP.md"
fi
