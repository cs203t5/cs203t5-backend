FROM maven:3.6.3 AS maven
# Create a workdir for our app
WORKDIR /usr/src/app
COPY . /usr/src/app

# Using java 17
FROM openjdk:17-oracle
# Compile and package the application to an executable JAR
RUN mvn clean package -DskipTests


ARG JAR_FILE=/usr/src/app/target/*.jar

# Copying JAR file
COPY --from=maven ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
