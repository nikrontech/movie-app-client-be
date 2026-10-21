package lt.rildev.movieappclientbe.service;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;

final class FuzzySearch {

    private FuzzySearch() {
    }

    static boolean matches(String query, String... values) {
        String normalizedQuery = normalize(query);
        if (normalizedQuery.isBlank()) return true;

        String[] queryTokens = normalizedQuery.split(" ");
        return Arrays.stream(values)
                .filter(value -> value != null && !value.isBlank())
                .map(FuzzySearch::normalize)
                .anyMatch(value -> matchesValue(normalizedQuery, queryTokens, value));
    }

    private static boolean matchesValue(String query, String[] queryTokens, String value) {
        if (value.contains(query)) return true;

        String[] valueTokens = value.split(" ");
        return Arrays.stream(queryTokens).allMatch(queryToken ->
                Arrays.stream(valueTokens).anyMatch(valueToken ->
                        valueToken.contains(queryToken)
                                || levenshtein(queryToken, valueToken) <= threshold(queryToken.length())));
    }

    private static int threshold(int length) {
        if (length <= 3) return 0;
        return Math.max(1, length / 4);
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ")
                .trim();
    }

    private static int levenshtein(String left, String right) {
        int[] previous = new int[right.length() + 1];
        for (int index = 0; index <= right.length(); index++) previous[index] = index;

        for (int row = 1; row <= left.length(); row++) {
            int[] current = new int[right.length() + 1];
            current[0] = row;
            for (int column = 1; column <= right.length(); column++) {
                current[column] = Math.min(
                        Math.min(current[column - 1] + 1, previous[column] + 1),
                        previous[column - 1] + (left.charAt(row - 1) == right.charAt(column - 1) ? 0 : 1));
            }
            previous = current;
        }
        return previous[right.length()];
    }
}
