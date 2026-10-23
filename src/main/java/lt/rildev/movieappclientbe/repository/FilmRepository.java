package lt.rildev.movieappclientbe.repository;

import lt.rildev.movieappclientbe.model.Film;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface FilmRepository extends MongoRepository<Film, String> {
    List<Film> findDistinctByScreenings_StartsAtBetween(Instant from, Instant to);
}
