package com.dudu.aios.ui.utils;

/**
 * Created by zha on 2016/2/20.
 */
public class StringUtil {

    //大小转换
    public static String changeUpper(String b) {
        char letters[] = new char[b.length()];
        for (int i = 0; i < b.length(); i++) {

            char letter = b.charAt(i);
            if (letter >= 'a' && letter <= 'z')
                letter = (char) (letter - 32);
            else if (letter >= 'A' && letter <= 'Z')
                letter = (char) (letter + 32);
            letters[i] = letter;
        }

        return new String(letters);
    }
}
