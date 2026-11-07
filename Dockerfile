FROM maven:3.10-eclipse-temurin-25
WORKDIR /tests
COPY . .
CMD mvn clean test