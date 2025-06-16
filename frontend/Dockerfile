# Stage 1 — установка зависимостей
FROM node:20-slim as deps
WORKDIR /app
COPY package*.json ./
RUN npm install --omit=dev && npm cache clean --force

# Stage 2 — основной слой (сборка)
FROM node:20-slim
WORKDIR /app
# Копируем только установленные модули и исходники
COPY --from=deps /app/node_modules ./node_modules
COPY . .
EXPOSE 8081
ENV EXPO_DEVTOOLS_LISTEN_ADDRESS=0.0.0.0
CMD ["npx", "expo", "start", "--web"]
