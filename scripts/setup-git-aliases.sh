#!/bin/bash

# Add project-specific git aliases
git config --local alias.vtag '!f() { timestamp=$(date "+%Y%m%d%H%M%S"); if [ -z "$1" ]; then echo "Error: Please specify tag type (breaking/feature/fix)"; return 1; elif [ "$1" != "breaking" ] && [ "$1" != "feature" ] && [ "$1" != "fix" ]; then echo "Error: Tag type must be '\''breaking'\'', '\''feature'\'', or '\''fix'\''"; return 1; fi; tag="v-$1-$timestamp"; git tag -a "$tag" -m "Auto-generated $1 tag"; echo "Created tag: $tag"; }; f'
git config --local alias.vtagp '!f() { timestamp=$(date "+%Y%m%d%H%M%S"); if [ -z "$1" ]; then echo "Error: Please specify tag type (breaking/feature/fix)"; return 1; elif [ "$1" != "breaking" ] && [ "$1" != "feature" ] && [ "$1" != "fix" ]; then echo "Error: Tag type must be '\''breaking'\'', '\''feature'\'', or '\''fix'\''"; return 1; fi; tag="v-$1-$timestamp"; git tag -a "$tag" -m "Auto-generated $1 tag"; git push origin "$tag"; echo "Created and pushed tag: $tag"; }; f'

echo "Git aliases 'vtag' and 'vtagp' have been set up successfully!" 