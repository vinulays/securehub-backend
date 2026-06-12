#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE securehub_user_dev;
    CREATE DATABASE securehub_org_dev;
EOSQL