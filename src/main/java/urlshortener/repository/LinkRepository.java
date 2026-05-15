package urlshortener.repository;

import urlshortener.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LinkRepository extends JpaRepository<Link, UUID> {

    Optional<Link> findByFullUrl(String fullUrl);

    Optional<Link> findByShortCode(String shortCode);

}