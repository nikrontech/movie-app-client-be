package lt.rildev.movieappclientbe.service;

import lt.rildev.movieappclientbe.model.Film;
import lt.rildev.movieappclientbe.model.City;
import lt.rildev.movieappclientbe.model.Screening;
import lt.rildev.movieappclientbe.repository.FilmRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;

@Service
public class FilmService {

    private static final ZoneId CINEMA_TIME_ZONE = ZoneId.of("Europe/Vilnius");

    private final FilmRepository filmRepository;
    private final int fuzzySearchRatio;

    public FilmService(FilmRepository filmRepository,
                       @Value("${fuzzysearch.ratio:70}") int fuzzySearchRatio) {
        this.filmRepository = filmRepository;
        this.fuzzySearchRatio = fuzzySearchRatio;
    }

    public List<Film> findFilms(
            LocalDateTime from,
            LocalDateTime to,
            String search,
            String cityName) {
        if (from == null || to == null) throw new IllegalArgumentException("from and to are required");
        if (from.isAfter(to)) throw new IllegalArgumentException("from must not be after to");

        var start = from.atZone(CINEMA_TIME_ZONE).toInstant();
        var end = to.atZone(CINEMA_TIME_ZONE).toInstant();
        City city = parseCity(cityName);

        return filmRepository.findDistinctByScreenings_StartsAtBetween(start, end).stream()
                .map(film -> city == null
                        ? withScreeningsInRange(film, start, end)
                        : withScreeningsInCity(film, city, start, end))
                .filter(film -> !film.screenings().isEmpty())
                .filter(film -> search == null || search.isBlank()
                        || FilmSearch.matches(search, fuzzySearchRatio, film.title(), film.originalTitle()))
                // Sort only after filtering so the count reflects this response.
                .sorted(byShowingCountThenSearchRelevance(search))
                .toList();
    }

    private Comparator<Film> byShowingCountThenSearchRelevance(String search) {
        return Comparator
                .comparingInt((Film film) -> film.screenings().size())
                .reversed()
                .thenComparing(
                        Comparator.comparingInt((Film film) ->
                                FilmSearch.score(search, film.title(), film.originalTitle()))
                                .reversed());
    }

    public Optional<Film> findFilm(String id, String cityName) {
        City city = parseCity(cityName);
        return filmRepository.findById(id)
                .map(film -> city == null ? film : withScreeningsInCity(film, city));
    }

    private Film withScreeningsInRange(Film film, java.time.Instant start, java.time.Instant end) {
        List<Screening> screenings = film.screenings() == null ? List.of() : film.screenings().stream()
                .filter(screening -> screening.startsAt() != null)
                .filter(screening -> !screening.startsAt().isBefore(start)
                        && !screening.startsAt().isAfter(end))
                .toList();
        return film.toBuilder()
                .screenings(screenings)
                .build();
    }

    private Film withScreeningsInCity(
            Film film,
            City city,
            java.time.Instant start,
            java.time.Instant end) {
        List<Screening> screenings = film.screenings() == null ? List.of() : film.screenings().stream()
                .filter(screening -> screening.cinema() != null)
                .filter(screening -> screening.cinema().city() == city)
                .filter(screening -> screening.startsAt() != null)
                .filter(screening -> !screening.startsAt().isBefore(start)
                        && !screening.startsAt().isAfter(end))
                .toList();
        return film.toBuilder().screenings(screenings).build();
    }

    private Film withScreeningsInCity(Film film, City city) {
        List<Screening> screenings = film.screenings() == null ? List.of() : film.screenings().stream()
                .filter(screening -> screening.cinema() != null)
                .filter(screening -> screening.cinema().city() == city)
                .toList();
        return film.toBuilder().screenings(screenings).build();
    }

    private City parseCity(String cityName) {
        if (cityName == null || cityName.isBlank()) return null;
        try {
            return City.valueOf(cityName);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
