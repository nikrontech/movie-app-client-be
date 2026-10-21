package lt.rildev.movieappclientbe.repository;

import lt.rildev.movieappclientbe.model.Film;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FilmRepository extends MongoRepository<Film, String> {
}
