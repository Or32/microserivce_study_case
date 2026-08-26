#!/usr/bin/env bash
set -euo pipefail

output_dir=${1:-offline-images}
mkdir -p "$output_dir"

images=(
  temporalio/auto-setup:1.28.1
  postgres:16
  temporalio/ui:2.34.0
  grafana/loki:3.7.0
  grafana/grafana:12.1.0
)

for image in "${images[@]}"; do
  filename=${image//\//_}
  filename=${filename//:/_}.tar
  docker pull "$image"
  docker image save --output "$output_dir/$filename" "$image"
  echo "Saved $image to $output_dir/$filename"
done
