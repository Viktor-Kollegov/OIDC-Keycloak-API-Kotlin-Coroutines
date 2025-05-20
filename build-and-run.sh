#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

error_exit() {
    echo -e "${RED}Ошибка: $1${NC}" >&2
    exit 1
}

PROJECTS=("app" "transactions" "auth")

for PROJECT in "${PROJECTS[@]}"; do
    echo -e "${GREEN}Сборка проекта: $PROJECT${NC}"
    if [ ! -d "$PROJECT" ]; then
        error_exit "Директория $PROJECT не найдена."
    fi
    cd "$PROJECT" || error_exit "Не удалось перейти в директорию $PROJECT."
    if [ ! -f "build.gradle.kts" ]; then
        error_exit "Файл build.gradle.kts не найден в $PROJECT."
    fi
    ./gradlew clean build -x test || error_exit "Сборка $PROJECT не удалась."
    cd .. || error_exit "Не удалось вернуться в корневую директорию."
done

echo -e "${GREEN}Запуск Docker Compose...${NC}"
docker-compose up --build || error_exit "Не удалось запустить Docker Compose."

echo -e "${GREEN}Все приложения успешно запущены!${NC}"