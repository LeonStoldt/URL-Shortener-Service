package de.cloud.fundamentals.urlshortenerservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.cloud.fundamentals.urlshortenerservice.userfeedback.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

@Service
public class GooLnkConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(GooLnkConnector.class);
    private static final I18n USER_FEEDBACK = new I18n();
    private static final String API_URL = "https://goolnk.com/api/v1/shorten";
    private static final String API_DATA_URL_KEY = "url=";
    private static final String API_RESPONSE_JSON_URL_KEY = "result_url";

    public String getShortenedUrl(String longUrl) {
        String result;
        if (isValidUri(longUrl)) {
            try {
                HttpResponse<String> response = HttpClient
                        .newHttpClient()
                        .send(getRequest(API_DATA_URL_KEY + longUrl), BodyHandlers.ofString());

                result = extractResponseBody(response);

            } catch (Exception e) {
                result = USER_FEEDBACK.get("error.api");
            }
        } else {
            result = USER_FEEDBACK.get("error.invalid-uri");
        }
        return result;
    }

    private String extractResponseBody(HttpResponse<String> response) {
        String body;

        if (HttpStatus.valueOf(response.statusCode()).equals(HttpStatus.OK)) {
            String responseJson = response.body();
            try {
                String shortenedUrl = getShortenedUrlFromJson(responseJson);
                body =  USER_FEEDBACK.format("success.shortened-url", shortenedUrl);
            } catch (JsonProcessingException e) {
                body = USER_FEEDBACK.format("error.json-parsing", responseJson);
            }
        } else {
            LOGGER.warn("invalid response from api with status code {}", response.statusCode());
            body = USER_FEEDBACK.get("error.api");
        }
        return body;
    }

    private boolean isValidUri(String longUrl) {
        try {
            URI.create(longUrl); // create Uri to check validity
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private HttpRequest getRequest(String longUrl) {
        return HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .POST(BodyPublishers.ofString(longUrl))
                .build();
    }

    private String getShortenedUrlFromJson(String responseJson) throws com.fasterxml.jackson.core.JsonProcessingException {
        return new ObjectMapper()
                .readValue(responseJson, ObjectNode.class)
                .get(API_RESPONSE_JSON_URL_KEY)
                .asText();
    }

}
