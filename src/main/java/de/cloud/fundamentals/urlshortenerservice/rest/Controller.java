package de.cloud.fundamentals.urlshortenerservice.rest;

import de.cloud.fundamentals.urlshortenerservice.GooLnkConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    private static final String JSON = MediaType.APPLICATION_JSON_VALUE;

    private final GooLnkConnector gooLnkConnector;

    @Autowired
    public Controller(GooLnkConnector gooLnkConnector) {
        this.gooLnkConnector = gooLnkConnector;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/", produces = JSON)
    public String getStatus() {
        return "UrlShortener is active.";
    }

    @PostMapping("/api")
    public ResponseEntity<String> receiveRequest(@RequestBody String message) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gooLnkConnector.getShortenedUrl(message.trim()));
    }
}
