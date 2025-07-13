#!/bin/bash
set -e

# Resolve to project root (/server)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"  # this is /server

# === Setup pre-commit hook ===
HOOK_SOURCE="$PROJECT_ROOT/scripts/hooks/pre-commit"
HOOK_TARGET="$PROJECT_ROOT/../.git/hooks/pre-commit"

if [ ! -f "$HOOK_SOURCE" ]; then
  echo "❌ Hook script not found at $HOOK_SOURCE"
  exit 1
fi

echo "🔗 Linking Git pre-commit hook..."
ln -sf "$HOOK_SOURCE" "$HOOK_TARGET"
chmod +x "$HOOK_SOURCE"
echo "✅ Git pre-commit hook installed."

# === Check for .env file ===
ENV_FILE="$PROJECT_ROOT/.env"

if [ ! -f "$ENV_FILE" ]; then
  echo "⚠️  .env file not found at $ENV_FILE"
  echo "➡️  Creating a template .env file. Please fill in your credentials."

  cat <<EOF > "$ENV_FILE"
# Local DB config for Liquibase
DB_URL=jdbc:postgresql://localhost:5432/yourdb
DB_USER=youruser
DB_PASS=yourpass
EOF

  echo "✅ Template .env created at $ENV_FILE"
else
  echo "✅ .env already exists."
fi
