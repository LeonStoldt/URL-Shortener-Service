package de.cloud.fundamentals.UrlShortenerService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.cloud.fundamentals.UrlShortenerService.userfeedback.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GooLnkConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(GooLnkConnector.class);
    private static final I18n USER_FEEDBACK = new I18n();
    private static final String API_URL = "https://goolnk.com/api/v1/shorten";
    private static final String API_DATA_URL_KEY = "url";
    private static final String API_RESPONSE_JSON_URL_KEY = "result_url";

    public String getShortenedUrl(String longUrl) {
        String result;
        try {
            String responseJson = WebClient
                    .create(API_URL)
                    .post()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(BodyInserters.fromFormData(API_DATA_URL_KEY, longUrl))
                    .retrieve()
                    .onStatus(status -> status.equals(HttpStatus.OK), e -> {
                        LOGGER.warn("invalid response ({}) from api", e.statusCode());
                        return Mono.empty();
                    })
                    .bodyToMono(String.class)
                    .block(); //does not work in this context. find solution to extract string

            String shortenedUrl = getShortenedUrlFromJson(responseJson);
            result = USER_FEEDBACK.format("success.shortened-url", shortenedUrl);
        } catch (Exception e) {
            result = USER_FEEDBACK.get("error.api");
        }
        return result;
    }

    private String getShortenedUrlFromJson(String responseJson) throws com.fasterxml.jackson.core.JsonProcessingException {
        return new ObjectMapper()
                .readValue(responseJson, ObjectNode.class)
                .get(API_RESPONSE_JSON_URL_KEY)
                .asText();
    }

}
