package urlshortener.exception;

public class LinkNotFoundException extends RuntimeException {

    public LinkNotFoundException(String shortCode) {
        super("Link not found for short code: " + shortCode);
    }
}