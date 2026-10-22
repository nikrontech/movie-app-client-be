package lt.rildev.movieappclientbe.model;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;
import java.util.List;

@Document(collection = "merged_movies")
@Builder(toBuilder = true)
public record Film(
        @Id String id,
        String title,
        String originalTitle,
        Integer durationMinutes,
        String description,
        String shortDescription,
        String director,
        String cast,
        String writer,
        String country,
        String language,
        String distributor,
        String imdbId,
        Instant releaseDate,
        Integer releaseYear,
        List<Genre> genres,
        AgeRating ageRating,
        @JsonIgnore String externalPosterUrl,
        @JsonIgnore String externalThumbnailUrl,
        @JsonIgnore String externalTrailerUrl,
        String posterPath,
        String trailerPath,
        String thumbnailPath,
        Rating rating,
        List<String> sourceMovieIds,
        List<String> providers,
        List<Screening> screenings
) {
}
