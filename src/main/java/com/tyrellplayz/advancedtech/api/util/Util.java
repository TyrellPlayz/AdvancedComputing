package com.tyrellplayz.advancedtech.api.util;

import net.minecraft.client.Minecraft;

public class Util {

    private Util() {}

    public static String[] addToArray(String[] strings, String s) {
        String[] newStrings = new String[strings.length + 1];

        for(int i = 0; i < newStrings.length; ++i) {
            if (i == newStrings.length - 1) {
                newStrings[i] = s;
            } else {
                newStrings[i] = strings[i];
            }
        }

        return newStrings;
    }

    public static int findNumberOfChars(char character, String text) {
        int num = 0;
        char[] var3 = text.toCharArray();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            char c = var3[var5];
            if (c == character) {
                ++num;
            }
        }

        return num;
    }

    public static int longestString(String[] strings) {
        int width = 0;
        String[] var2 = strings;
        int var3 = strings.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String string = var2[var4];
            int stringWidth = Minecraft.getInstance().font.width(string);
            if (stringWidth > width) {
                width = stringWidth;
            }
        }

        return width;
    }

}
