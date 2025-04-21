package me.rejomy.money.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtil {

    public String format(String text, String... placeholders) {
        if (placeholders.length > 1) {
            for (byte a = 1; a < placeholders.length; a+=2) {
                text = text.replace("$" + placeholders[a - 1], placeholders[a]);
            }
        }

        return text;
    }
}
