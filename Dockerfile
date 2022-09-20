FROM maven:3.8.3-openjdk-17 AS maven
# Create a workdir for our app
WORKDIR /usr/src/app
COPY . /usr/src/app

<<<<<<< HEAD
# Using java 17
FROM openjdk:17-jdk
=======
# Compile and package the application to an executable JAR
RUN mvn clean package -DskipTests
# Using java 17
FROM openjdk:17-oracle
>>>>>>> 745adf6 (Update Dockerfile)

ARG JAR_FILE=/usr/src/app/target/*.jar

# Copying JAR file
COPY --from=maven ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
