#!/bin/bash

set -e  # Exit immediately on error

# Resolve script's own location to support running from anywhere
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"  # this is /server

# === Load .env from /server ===
ENV_FILE="$PROJECT_ROOT/.env"
if [ -f "$ENV_FILE" ]; then
  export $(grep -v '^#' "$ENV_FILE" | xargs)
else
  echo "❌ .env file not found at $ENV_FILE"
  exit 1
fi

# === CONFIGURATION ===
CHANGELOG_DIR="$PROJECT_ROOT/src/main/resources/db/changelog"
CHANGELOG_RECORDS_FOLDER="records"
MASTER_FILE="$CHANGELOG_DIR/db.changelog-master.xml"
FILE_NAME="db.changelog-$(date +%Y%m%d%H%M%S).xml"
FULL_CHANGELOG_PATH="$CHANGELOG_DIR/$CHANGELOG_RECORDS_FOLDER/$FILE_NAME"
RELATIVE_PATH="db/changelog/$CHANGELOG_RECORDS_FOLDER/$FILE_NAME"


# === Create changelog directory if missing ===
mkdir -p "$CHANGELOG_DIR/$CHANGELOG_RECORDS_FOLDER"

# === Generate changelog ===
mvn liquibase:diffChangeLog \
  -Dliquibase.changeLogFile="$FULL_CHANGELOG_PATH" \
  -Dliquibase.referenceUrl=hibernate:spring:com.yourpackage?dialect=org.hibernate.dialect.PostgreSQLDialect \
  -Dliquibase.url="$DB_URL" \
  -Dliquibase.username="$DB_USER" \
  -Dliquibase.password="$DB_PASS" \
  -Dliquibase.driver=org.postgresql.Driver

echo "✅ Changelog generated at: $FULL_CHANGELOG_PATH"

# === Add <include> to master changelog if not already included ===
if ! grep -q "$RELATIVE_PATH" "$MASTER_FILE"; then
  # Add include line before the closing </databaseChangeLog> tag
  sed -i.bak "/<\/databaseChangeLog>/i \ \ \ \ <include file=\"$RELATIVE_PATH\" relativeToChangelogFile=\"true\"/>" "$MASTER_FILE"
  echo "✅ Added <include> to: $MASTER_FILE"
else
  echo "⚠️ Already included in master changelog."
fi
