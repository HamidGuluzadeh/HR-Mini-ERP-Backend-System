FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY build/libs/erp-0.0.1-SNAPSHOT.jar /app/erp-0.0.1-SNAPSHOT.jar
EXPOSE ${SERVER_PORT}
ENTRYPOINT ["java", "-jar", "erp.jar"]