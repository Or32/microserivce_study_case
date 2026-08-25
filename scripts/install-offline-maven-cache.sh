#!/usr/bin/env bash
set -euo pipefail

project_dir=$(cd "$(dirname "$0")/.." && pwd)
bundle_dir=${1:-${OFFLINE_BUNDLE:-"$project_dir/offline-bundle"}}
cache_archive="$bundle_dir/maven-cache.tar.gz"
m2_dir=${M2_REPO_HOME:-"$HOME/.m2"}

if [[ ! -f "$cache_archive" ]]; then
  echo "Maven cache archive not found: $cache_archive" >&2
  exit 1
fi

mkdir -p "$m2_dir"
tar -C "$m2_dir" -xzf "$cache_archive"
echo "Installed Maven cache into $m2_dir"
