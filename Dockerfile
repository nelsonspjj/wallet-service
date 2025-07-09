FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/wallet-service-1.0.0.jar wallet-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "wallet-service.jar"]