FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
RUN apk add --no-cache maven
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/notifications-lib-1.0.0.jar app.jar
COPY --from=build /root/.m2/repository/org/slf4j/slf4j-api/2.0.16/slf4j-api-2.0.16.jar lib/
COPY --from=build /root/.m2/repository/org/slf4j/slf4j-simple/2.0.16/slf4j-simple-2.0.16.jar lib/
CMD ["java", "-cp", "app.jar:lib/*", "com.example.notifications.examples.NotificationExamples"]
