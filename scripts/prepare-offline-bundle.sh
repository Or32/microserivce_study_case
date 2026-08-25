#!/usr/bin/env bash
set -euo pipefail

project_dir=$(cd "$(dirname "$0")/.." && pwd)
bundle_dir=${OFFLINE_BUNDLE:-"$project_dir/offline-bundle"}
image_platform=${IMAGE_PLATFORM:-}

if ! command -v docker >/dev/null; then
  echo "Docker is required to prepare the offline image bundle." >&2
  exit 1
fi

if [[ -n "$image_platform" ]]; then
  export DOCKER_DEFAULT_PLATFORM="$image_platform"
fi

mkdir -p "$bundle_dir"

echo "Resolving Maven plugins and dependencies..."
cd "$project_dir"
./mvnw -B -U dependency:go-offline

echo "Pulling infrastructure container images${image_platform:+ for $image_platform}..."
infrastructure_images=(
  temporalio/auto-setup:1.28.1
  postgres:16
  temporalio/ui:2.34.0
  grafana/loki:3.7.0
  grafana/grafana:12.1.0
)
for image in "${infrastructure_images[@]}"; do
  if [[ -n "$image_platform" ]]; then
    docker pull --platform "$image_platform" "$image"
  else
    docker pull "$image"
  fi
done

echo "Writing image archive..."
docker image save --output "$bundle_dir/images.tar" "${infrastructure_images[@]}"

echo "Writing Maven cache archive..."
m2_dir=${M2_REPO_HOME:-"$HOME/.m2"}
tar -C "$m2_dir" -czf "$bundle_dir/maven-cache.tar.gz" repository wrapper

cat > "$bundle_dir/MANIFEST.txt" <<EOF
Created: $(date -u +%Y-%m-%dT%H:%M:%SZ)
Image platform: ${image_platform:-host default}
Java: $(java -version 2>&1 | head -n 1)
Maven: $(./mvnw -version 2>/dev/null | head -n 1)
EOF

echo "Offline bundle created at $bundle_dir"
