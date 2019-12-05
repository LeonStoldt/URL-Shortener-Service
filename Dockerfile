FROM adoptopenjdk/openjdk11:latest
ADD target/UrlShortenerService.jar UrlShortenerService.jar
EXPOSE 8005
ENTRYPOINT ["java", "-jar",  "UrlShortenerService.jar"]