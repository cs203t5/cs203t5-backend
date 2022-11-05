ARG private
ARG public

FROM maven:3.8.3-openjdk-17 AS maven

ENV private=$private
ENV public=$public

# Create a workdir for our app
WORKDIR /usr/src/app

RUN echo $private > private.pem 
RUN echo $public > public.pem 
RUN chmod 600 public.pem 
RUN chmod 600 private.pem 

COPY private.pem VoxViridis/resources/certs
COPY public.pem VoxViridis/resources/certs

ADD  private.pem VoxViridis/src/resources/certs
ADD public.pem VoxViridis/src/resources/certs

COPY Vox-Viridis /usr/src/app

RUN mvn clean package -DskipTests

# Using java 17
FROM openjdk:17-oracle

ARG JAR_FILE=/usr/src/app/target/*.jar

# Copying JAR file
COPY --from=maven ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar","dev.env"]