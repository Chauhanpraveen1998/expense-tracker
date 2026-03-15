#!/usr/bin/env bash
set -a
source "$(dirname "$0")/.env"
set +a

export JAVA_HOME
exec mvn spring-boot:run
