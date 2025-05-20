#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

error_exit() {
    echo -e "${RED}Ошибка: $1${NC}" >&2
    exit 1
}

echo -e "${GREEN}Сборка проекта...${NC}"

./gradlew clean build -x test || error_exit "Сборка не удалась."

echo -e "${GREEN}Запуск Docker Compose...${NC}"
docker-compose up --build || error_exit "Не удалось запустить Docker Compose."

echo -e "${GREEN}Все приложения успешно запущены!${NC}"
