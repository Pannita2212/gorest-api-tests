#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_ROOT"

print_step() {
  printf '\n==> %s\n' "$1"
}

print_error() {
  printf '\n[ERROR] %s\n' "$1" >&2
}

print_step "Checking Java"
if ! command -v java >/dev/null 2>&1; then
  print_error "Java was not found. Please install Java 17 or higher."
  cat <<'MSG'

macOS with Homebrew:
  brew install openjdk

After installation, follow the Homebrew output to add Java to your shell profile.
Then verify:
  java -version
MSG
  exit 1
fi
java -version

JAVA_MAJOR_VERSION="$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F. '{ if ($1 == "1") print $2; else print $1 }')"
if [ "${JAVA_MAJOR_VERSION:-0}" -lt 17 ]; then
  print_error "Java 17 or higher is required. Current major version: ${JAVA_MAJOR_VERSION:-unknown}"
  exit 1
fi

print_step "Checking Maven"
if ! command -v mvn >/dev/null 2>&1; then
  print_error "Maven was not found. Please install Maven."
  cat <<'MSG'

macOS with Homebrew:
  brew install maven

Then verify:
  mvn -version
MSG
  exit 1
fi
mvn -version

print_step "Preparing .env file"
if [ ! -f .env ]; then
  if [ -f .env.example ]; then
    cp .env.example .env
    printf '[OK] Created .env from .env.example\n'
  else
    cat > .env <<'ENV'
GOREST_API_TOKEN=
# BASE_URL=https://gorest.co.in/public/v2
ENV
    printf '[OK] Created .env\n'
  fi
else
  printf '[OK] .env already exists\n'
fi

if ! grep -q '^GOREST_API_TOKEN=' .env; then
  printf '\nGOREST_API_TOKEN=\n' >> .env
  printf '[OK] Added GOREST_API_TOKEN to .env\n'
fi

TOKEN_VALUE="$(grep '^GOREST_API_TOKEN=' .env | tail -n 1 | cut -d '=' -f 2- | tr -d '[:space:]')"
if [ -z "$TOKEN_VALUE" ] || [ "$TOKEN_VALUE" = "your_token_here" ]; then
  cat <<'MSG'

[INFO] GOREST_API_TOKEN is empty or still using a placeholder.
       Read-only tests will run.
       Write-operation tests such as POST, PUT, PATCH, and DELETE will be skipped.

To run the full CRUD test suite, update .env:
  GOREST_API_TOKEN=your_real_token_here
MSG
else
  printf '[OK] GOREST_API_TOKEN is configured\n'
fi

print_step "Validating project build configuration"
mvn -q -DskipTests test

cat <<'MSG'

[SUCCESS] Project setup completed.

You can now run:
  mvn clean test

Useful commands:
  mvn test -Dtest=ListUsersApiTest
  mvn test -Dtest=CreateUserApiTest#createUserSuccessfully
MSG
