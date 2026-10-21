package lt.rildev.movieappclientbe.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FuzzySearchTest {

    @Test
    void matchesSmallTitleMistakes() {
        assertTrue(FilmSearch.matches("avatr", 70, "Avatar: The Way of Water"));
    }

    @Test
    void matchesShortenedTitleToken() {
        assertTrue(FilmSearch.matches("odyse", 70, "The Odyssey"));
    }

    @Test
    void matchesAccentsAndCaseInsensitively() {
        assertTrue(FilmSearch.matches("zalgiris", 70, "Žalgiris"));
    }

    @Test
    void rejectsUnrelatedTitles() {
        assertFalse(FilmSearch.matches("avatar", 70, "Dune"));
    }
}
