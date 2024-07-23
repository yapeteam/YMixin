package cn.yapeteam.ymixin.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class StringUtil {
    public static String[] split(@NotNull String str, String splitter) {
        if (!str.contains(splitter))
            return new String[]{};

        ArrayList<String> result = new ArrayList<>();
        int startIndex = 0;
        int splitterLength = splitter.length();

        while (true) {
            int index = str.indexOf(splitter, startIndex);
            if (index == -1) {
                result.add(str.substring(startIndex));
                break;
            }
            result.add(str.substring(startIndex, index));
            startIndex = index + splitterLength;
        }

        return result.toArray(new String[0]);
    }
}
