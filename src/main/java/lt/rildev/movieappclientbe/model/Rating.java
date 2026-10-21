package lt.rildev.movieappclientbe.model;

import java.util.List;

public record Rating(String imdbId, String imdbRating, String imdbVotes,
                     String metascore, List<RatingSource> sources) {
}
