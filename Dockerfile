##########################################################################################
## Step 1 : Build application
##########################################################################################
FROM maven:3.6.3-adoptopenjdk-15 as maven_build
#
LABEL maintainer="Anupam Rao"

# Copy source code
COPY src /home/app/src
COPY pom.xml /home/app

# Build app
RUN mvn -f /home/app/pom.xml clean package

##########################################################################################
## Step 2 : Build application image
##########################################################################################
FROM adoptopenjdk/openjdk15:alpine-jre

#
LABEL maintainer="Anupam Rao"


# Copy jar file from build step
COPY --from=maven_build /home/app/target/warehouse-service-*.jar warehouse-service.jar

# Expose ports
EXPOSE 8080

# Entry point
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=default", "/warehouse-service.jar"]