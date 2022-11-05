FROM maven:3.8.3-openjdk-17 AS maven
# Create a workdir for our app
WORKDIR /usr/src/app
COPY . /usr/src/app

RUN mvn clean package -DskipTests

# Using java 17
FROM openjdk:17-oracle

ARG JAR_FILE=/usr/src/app/target/*.jar

# Copying JAR file
COPY --from=maven ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar","dev.env"]