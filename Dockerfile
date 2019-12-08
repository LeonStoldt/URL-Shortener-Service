FROM adoptopenjdk/openjdk11:latest
ADD target/UrlShortenerService.jar UrlShortenerService.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar",  "UrlShortenerService.jar"]