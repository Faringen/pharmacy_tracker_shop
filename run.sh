#!/usr/bin/env bash

set -e
ROOT_DIR = "$(cd "$(dirname "$0")" && pwd)"
echo "Запуск базы данных ->"
(cd "$ROOT_DIR/pharmacy-server" && docker compose up -d)

echo "Запуск сервера ->"
(cd "$ROOT_DIR/pharmacy-server" && ./mvnw spring-boot:run > "$ROOT_DIR/server.log" 2>&1) & SERVER_PID = &!

cleanup() {
  echo "Остановка сервера ->"
  pkill -P "$SERVER_PID" 2>/dev/null || true
  kill "$SERVER_PID" 2>/dev/null || true
}
trap cleanup EXIT

(cd )
