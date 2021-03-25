package org.arkngbot.services.impl;

import org.springframework.stereotype.Component;

@Component
public class ArticleSupport {

    private static final String VOWEL_REGEX = "[AEIOUaeiou]";
    private static final String CONSONANT_REGEX = "[BCDFGHJKLMNPQRSTVWXYZbcdfhgjklmnpqrstvwxyz]";
    private static final String AN = "an";
    private static final String A = "a";

    /**
     * Returns the indefinite article that would precede a given string. In case of U defaults to "an"
     * (will not always be correct)
     * @param string The string to evaluate.
     * @return The indefinite article that would precede the string. If the first character of the string
     * is not a letter of the English alphabet, null is returned.
     */
    public String determineIndefiniteArticle(String string) {
        if (string != null && string.length() > 0) {
            String firstLetter = string.substring(0, 1);

            if (firstLetter.matches(VOWEL_REGEX)) {
                return AN;
            }
            else if (firstLetter.matches(CONSONANT_REGEX)) {
                return A;
            }
        }
        return null;
    }
}
