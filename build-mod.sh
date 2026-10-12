#!/usr/bin/env sh
set -eu
if ! command -v gradle >/dev/null 2>&1; then
  echo "Gradle 9.2.1+ is required. Install it and rerun this script." >&2
  exit 1
fi
gradle build
printf '\nBuilt JARs:\n'
find build/libs -maxdepth 1 -name '*.jar' -print
