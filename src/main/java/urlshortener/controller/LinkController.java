package urlshortener.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener.service.LinkService;

@RestController
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;

    // ENDPOINT 1 Shorten a URL
    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@RequestBody String fullUrl) {

        String shortCode = linkService.createShortLink(fullUrl);
        String shortUrl = "http://localhost:8080/" + shortCode;

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(shortUrl);
    }

    // ENDPOINT 2 Get full URL from short code
    // GET /links/{code}
    @GetMapping("/links/{code}")
    public ResponseEntity<String> getFullUrl(@PathVariable String code) {

        String fullUrl = linkService.getFullUrl(code);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(fullUrl);
    }

    // ENDPOINT 3 Redirect browser to full URL

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {

        String fullUrl = linkService.getFullUrl(code);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", fullUrl);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .headers(headers)
                .build();
    }
}