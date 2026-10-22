FROM eclipse-temurin:25-jdk AS build

WORKDIR /workspace
COPY . .

RUN chmod +x gradlew \
    && ./gradlew bootJar --no-daemon \
    && cp "$(find build/libs -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' | head -n 1)" build/app.jar

FROM eclipse-temurin:25-jre

WORKDIR /app
RUN useradd --system --create-home appuser
COPY --from=build /workspace/build/app.jar app.jar

USER appuser
EXPOSE 8080

ENV MEDIA_IMAGES_DIRECTORY=/app/downloads/images \
    MEDIA_VIDEOS_DIRECTORY=/app/downloads/videos \
    SPRING_MONGODB_URI=mongodb://admin:changeme@mongodb:27017/movieApp?authSource=admin

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
