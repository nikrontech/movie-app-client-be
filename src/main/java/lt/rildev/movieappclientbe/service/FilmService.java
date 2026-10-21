package lt.rildev.movieappclientbe.service;

import lt.rildev.movieappclientbe.model.Film;
import lt.rildev.movieappclientbe.model.Screening;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

@Service
public class FilmService {

    private final RestClient movieBackendClient;

    public FilmService(RestClient movieBackendClient) {
        this.movieBackendClient = movieBackendClient;
    }

    public List<Film> findFilms(LocalDate from, LocalDate to, String search) {
        if (from == null || to == null) throw new IllegalArgumentException("from and to are required");
        if (from.isAfter(to)) throw new IllegalArgumentException("from must not be after to");

        var start = from.atStartOfDay().toInstant(ZoneOffset.UTC);
        var end = to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        Film[] films = movieBackendClient.get().uri("/api/films").retrieve().body(Film[].class);
        if (films == null) return List.of();

        return Arrays.stream(films)
                .map(film -> withScreeningsInRange(film, start, end))
                .filter(film -> !film.screenings().isEmpty())
                .filter(film -> FuzzySearch.matches(search, film.title(), film.originalTitle()))
                .toList();
    }

    private Film withScreeningsInRange(Film film, java.time.Instant start, java.time.Instant end) {
        List<Screening> screenings = film.screenings() == null ? List.of() : film.screenings().stream()
                .filter(screening -> screening.startsAt() != null)
                .filter(screening -> !screening.startsAt().isBefore(start) && screening.startsAt().isBefore(end))
                .toList();
        return new Film(film.id(), film.title(), film.originalTitle(), film.durationMinutes(), film.description(),
                film.shortDescription(), film.releaseDate(), film.releaseYear(), film.genres(), film.ageRating(),
                film.externalPosterUrl(), film.externalThumbnailUrl(), film.externalTrailerUrl(), film.thumbnailPath(),
                film.rating(), film.sourceMovieIds(), film.providers(), screenings);
    }
}
