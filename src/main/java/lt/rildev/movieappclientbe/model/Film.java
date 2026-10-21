package lt.rildev.movieappclientbe.model;

import java.time.Instant;
import java.util.List;

public record Film(
        String id,
        String title,
        String originalTitle,
        Integer durationMinutes,
        String description,
        String shortDescription,
        Instant releaseDate,
        Integer releaseYear,
        List<Genre> genres,
        AgeRating ageRating,
        String externalPosterUrl,
        String externalThumbnailUrl,
        String externalTrailerUrl,
        String thumbnailPath,
        Rating rating,
        List<String> sourceMovieIds,
        List<String> providers,
        List<Screening> screenings
) {
}
