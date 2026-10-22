package lt.rildev.movieappclientbe.service;

import lt.rildev.movieappclientbe.model.Film;
import lt.rildev.movieappclientbe.model.Screening;
import lt.rildev.movieappclientbe.repository.FilmRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;

@Service
public class FilmService {

    private final FilmRepository filmRepository;
    private final int fuzzySearchRatio;

    public FilmService(FilmRepository filmRepository,
                       @Value("${fuzzysearch.ratio:70}") int fuzzySearchRatio) {
        this.filmRepository = filmRepository;
        this.fuzzySearchRatio = fuzzySearchRatio;
    }

    public List<Film> findFilms(LocalDate from, LocalDate to, String search) {
        if (from == null || to == null) throw new IllegalArgumentException("from and to are required");
        if (from.isAfter(to)) throw new IllegalArgumentException("from must not be after to");

        var start = from.atStartOfDay().toInstant(ZoneOffset.UTC);
        var end = to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        return filmRepository.findAll().stream()
                .map(film -> withScreeningsInRange(film, start, end))
                .filter(film -> !film.screenings().isEmpty())
                .filter(film -> search == null || search.isBlank()
                        || FilmSearch.matches(search, fuzzySearchRatio, film.title(), film.originalTitle()))
                .sorted((left, right) -> {
                    int showingCount = Integer.compare(
                            right.screenings().size(), left.screenings().size());
                    if (showingCount != 0) return showingCount;
                    return Integer.compare(
                            FilmSearch.score(search, right.title(), right.originalTitle()),
                            FilmSearch.score(search, left.title(), left.originalTitle()));
                })
                .toList();
    }

    private Film withScreeningsInRange(Film film, java.time.Instant start, java.time.Instant end) {
        List<Screening> screenings = film.screenings() == null ? List.of() : film.screenings().stream()
                .filter(screening -> screening.startsAt() != null)
                .filter(screening -> !screening.startsAt().isBefore(start) && screening.startsAt().isBefore(end))
                .toList();
        return film.toBuilder()
                .screenings(screenings)
                .build();
    }
}
