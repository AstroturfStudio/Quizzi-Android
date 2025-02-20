[![Beta Release Pipeline](https://github.com/AstroturfStudio/Quizzi-Android/actions/workflows/closed-beta-release.yaml/badge.svg)](https://github.com/alicankorkmaz-sudo/Quizzi-Android/actions/workflows/closed-beta-release.yaml)

![GitHub Repo stars](https://img.shields.io/github/stars/alicankorkmaz-sudo/Quizzi-Android)

## Development Setup

After cloning the repository, run the following command to set up Git aliases for version tagging:
bash
chmod +x scripts/setup-git-aliases.sh
./scripts/setup-git-aliases.sh

These aliases provide convenient commands for version tagging:
- `git vtag <type>`: Create a version tag (type: breaking/feature/fix)
- `git vtagp <type>`: Create and push a version tag
bash