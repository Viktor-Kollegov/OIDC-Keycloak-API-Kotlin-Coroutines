#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

error_exit() {
    echo -e "${RED}Error: $1${NC}" >&2
    exit 1
}

echo -e "${GREEN}Building the project...${NC}"

./gradlew clean build -x test || error_exit "Build failed."

echo -e "${GREEN}Starting Docker Compose...${NC}"
docker-compose up --build || error_exit "Docker Compose failed."

echo -e "${GREEN}All applications started successfully!${NC}"
