#!/usr/bin/env bash
set -o xtrace

./gradlew clean assemble

docker build --tag spring-testcontainers-image .

docker compose up
