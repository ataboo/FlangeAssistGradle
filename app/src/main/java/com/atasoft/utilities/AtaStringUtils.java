package com.atasoft.utilities;

/**
 * Created by ataboo on 4/14/2017.
 */

public class AtaStringUtils {
    public static String capitalizeWord(String word) {
        if (word.length() <= 1) {
            return word.toUpperCase();
        }

        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }

    public static String implode(String[] array, String glue) {
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<array.length; i++) {
            builder.append(array[i]);
            if (i != array.length - 1) {
                builder.append(glue);
            }
        }

        return builder.toString();
    }
}
