FROM openjdk:20-jdk-slim
WORKDIR /app
COPY target/file-share-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
