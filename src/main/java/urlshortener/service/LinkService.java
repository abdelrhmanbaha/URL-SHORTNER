package urlshortener.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import urlshortener.entity.Link;
import urlshortener.exception.LinkExpiredException;
import urlshortener.exception.LinkNotFoundException;
import urlshortener.repository.LinkRepository;
import urlshortener.util.ShortCodeGenerator;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final RedisTemplate<String, String> redisTemplate;


    // CREATE SHORT LINK
    public String createShortLink(String fullUrl) {
        // Remove surrounding quotes if present
        final String cleanUrl = fullUrl.replace("\"", "").trim();

        String shortCode = shortCodeGenerator.generate(cleanUrl);
        return linkRepository.findByFullUrl(cleanUrl)
                .map(Link::getShortCode)
                .orElseGet(() -> saveNewLink(cleanUrl, shortCode));
    }

    //  FULL URL
    public String getFullUrl(String shortCode) {

        //  link exists in database
        Link link = linkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new LinkNotFoundException(shortCode));

        //  expired  Valkey
        Boolean exists = redisTemplate.hasKey(shortCode);
        if (exists == null || !exists) {
            throw new LinkExpiredException(shortCode);
        }

        return link.getFullUrl();
    }


    // valkey use
    private String saveNewLink(String fullUrl, String shortCode) {

        // Calculate expiry time - 10 minutes from now
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(10);

        // Save to PostgreSQL
        Link link = Link.builder()
                .fullUrl(fullUrl)
                .shortCode(shortCode)
                .createdAt(now)
                .expiresAt(expiresAt)
                .build();

        linkRepository.save(link);

        // Save to Valkey with 10 minute TTL
        redisTemplate.opsForValue().set(
                shortCode,
                fullUrl,
                10,
                TimeUnit.MINUTES
        );

        return shortCode;
    }
}