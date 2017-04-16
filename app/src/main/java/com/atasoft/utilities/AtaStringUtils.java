package com.atasoft.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ataboo on 4/14/2017.
 */

public class AtaStringUtils {
    /**
     * Capitalize the first letter in the `word` and lower case the rest.
     *
     * @param word
     * @return
     */
    public static String capitalizeWord(String word) {
        if (word.length() <= 1) {
            return word.toUpperCase();
        }

        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }

    /**
     * Combine `strings` with `glue` separator.
     *
     * @param strings
     * @param glue
     * @return
     */
    public static String implode(String[] strings, String glue) {
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<strings.length; i++) {
            builder.append(strings[i]);
            if (i != strings.length - 1) {
                builder.append(glue);
            }
        }

        return builder.toString();
    }

    /**
     * Determine if any of the needles are in the haystack as individual words
     * (Surrounded by white space, '.', ',', end of sting, etc.)
     *
     * @param needles Strings to search for
     * @param haystack String to search inside
     * @return true if contains any of the needles.
     */
    public static boolean containsWord(String[] needles, String haystack) {
        Pattern pattern = Pattern.compile("(^|\\b)("+implode(needles, "|")+")(\\b|$)");
        Matcher matcher = pattern.matcher(haystack);
        return matcher.find();
    }
}
