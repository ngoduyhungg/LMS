#!/bin/bash
# =============================================================================
# PostgreSQL Multi-Database Initialization Script
#
# WHEN THIS RUNS:
#   PostgreSQL Docker image executes scripts in /docker-entrypoint-initdb.d/
#   ONLY on the very first container startup (when the data volume is empty).
#   On subsequent startups, this script is silently skipped.
#
# PERMISSION REQUIREMENT (IMPORTANT on Windows + WSL):
#   This script MUST have execute permission, otherwise PostgreSQL silently
#   ignores it. After cloning or creating this file, run:
#     git update-index --chmod=+x docker/postgres/init-multiple-dbs.sh
#   Or on Linux/macOS:
#     chmod +x docker/postgres/init-multiple-dbs.sh
#
# =============================================================================
set -e

create_database() {
    local database="$1"
    echo "  [INIT] Creating database: '$database'"

    # Check existence before creating â€” idempotent and avoids error noise
    if psql --username "$POSTGRES_USER" --dbname postgres \
            -tqc "SELECT 1 FROM pg_database WHERE datname = '$database'" \
            | grep -q 1; then
        echo "  [SKIP] Database '$database' already exists."
    else
        psql --username "$POSTGRES_USER" --dbname postgres \
             -c "CREATE DATABASE \"$database\";"
        echo "  [OK]   Database '$database' created."
    fi

    # Grant privileges unconditionally (idempotent)
    psql --username "$POSTGRES_USER" --dbname postgres \
         -c "GRANT ALL PRIVILEGES ON DATABASE \"$database\" TO $POSTGRES_USER;"
}

if [ -n "$POSTGRES_MULTIPLE_DATABASES" ]; then
    echo "======================================================"
    echo " LMS Platform â€” Multi-Database Initialization"
    echo " Targets: $POSTGRES_MULTIPLE_DATABASES"
    echo "======================================================"

    for db in $(echo "$POSTGRES_MULTIPLE_DATABASES" | tr ',' ' '); do
        create_database "$db"
    done

    echo "======================================================"
    echo " Done. All databases ready."
    echo "======================================================"
fi
