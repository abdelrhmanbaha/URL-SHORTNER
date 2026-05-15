package urlshortener.exception;

public class LinkExpiredException extends RuntimeException {

    public LinkExpiredException(String shortCode) {
        super("Link has expired for short code: " + shortCode);
    }
}