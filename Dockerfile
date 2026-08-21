# Stage 1: Build
FROM gradle:8.7-jdk17 AS build
WORKDIR /app
COPY . .
RUN ./gradlew :backend:installDist --no-daemon

# Stage 2: Minimal Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/backend/build/install/backend /app/

RUN mkdir -p /data && chmod 777 /data
VOLUME ["/data"]

ENV PORT=8080
ENV HOST=0.0.0.0
ENV EASYUI_ENV=production
ENV EASYUI_STORAGE_FILE=/data/easyui_store.json
ENV EASYUI_SEED_DEV_TOKENS=false

EXPOSE 8080
ENTRYPOINT ["/app/bin/backend"]
