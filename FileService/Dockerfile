FROM openjdk:17-oracle

ADD target/FileService-0.0.1-SNAPSHOT.jar /app/
CMD ["java", "-Xmx200m", "-jar", "-Dspring.profiles.active=dev", "/app/FileService-0.0.1-SNAPSHOT.jar"]

EXPOSE 8081