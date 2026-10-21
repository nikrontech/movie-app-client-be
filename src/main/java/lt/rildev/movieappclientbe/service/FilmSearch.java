package lt.rildev.movieappclientbe.service;

import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.text.Normalizer;
import java.util.Locale;

final class FilmSearch {

    private FilmSearch() {
    }

    static int score(String query, String... values) {
        String normalizedQuery = normalize(query);
        int score = 0;
        for (String value : values) {
            if (value != null) {
                score = Math.max(score, FuzzySearch.partialRatio(normalize(value), normalizedQuery));
            }
        }
        return score;
    }

    static boolean matches(String query, int minimumScore, String... values) {
        return score(query, values) >= minimumScore;
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .replaceAll("\\s+", " ");
    }
}
