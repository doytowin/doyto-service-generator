package org.grs.generator.util;

import org.apache.commons.lang3.StringUtils;

/**
 * StringUtil
 *
 * @author f0rb on 2017-08-11.
 */
public class StringUtil {
    public static String capitalize(String str) {
        return capitalize(str, "_");
    }

    public static String capitalize(String str, String delimiter) {
        String[] arr = str.split(delimiter);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = StringUtils.capitalize(arr[i]);
        }
        return StringUtils.join(arr);
    }

    public static String camelize(String str) {
        return camelize(str, "_");
    }

    public static String camelize(String str, String delimiter) {
        String[] arr = str.split(delimiter);
        for (int i = 1; i < arr.length; i++) {
            arr[i] = StringUtils.capitalize(arr[i]);
        }
        return StringUtils.join(arr);
    }
}
