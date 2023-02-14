package main.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class ASCIIProgressbar {

    private static final Logger logger = LoggerFactory.getLogger(ASCIIProgressbar.class);
    private static final boolean isUTF8 = new OutputStreamWriter(System.out).getEncoding().equals("UTF8");

    public static void display() {
        String pos = "#■|◆";
        String neg = "-.◦ ";
        ArrayList<String> strings = new ArrayList<>();
//        strings.add("[####------]");
//        strings.add("[####◦◦◦◦◦◦]");
//        strings.add("[####......]");
//        strings.add("[####      ]");
//        strings.add("[■■■■------]");
//        strings.add("[■■■■◦◦◦◦◦◦]");
//        strings.add("[■■■■......]");
//        strings.add("[■■■■      ]");
//        strings.add("[||||------]");
//        strings.add("[||||......]");
//        strings.add("[||||◦◦◦◦◦◦]");
//        strings.add("[||||      ]");
//        strings.add("[◆◆◆◆------]");
//        strings.add("[◆◆◆◆◦◦◦◦◦◦]");
//        strings.add("[◆◆◆◆......]");
//        strings.add("[◆◆◆◆      ]");
        strings.add("[▨▨▨▨▢▢▢▢▢▢]");
        strings.forEach(logger::info);

        for (char aChar : pos.toCharArray()) {
            String p = String.valueOf(aChar);
            for (char cChar : neg.toCharArray()) {
                String c = String.valueOf(cChar);
                logger.info("[{}{}]",p.repeat(4), c.repeat(6));
            }
        }
    }

    public static String consoleBar(int value, int maxValue) {
        if (isUTF8)
            return bar(value, maxValue, 10, "■", ".", true, true);
        return bar(value, maxValue, 10, "#", " ", true, true);
    }

    public static String bar(int value, int maxValue, int length) {
        return bar(value, maxValue, length, "■", ".", true, true);
    }

    public static String bar(int value, int maxValue, int length, String symbols) {
        if (symbols.length() <= 2)
            return bar(value, maxValue, length, "■", ".", true, true);
        return bar(value, maxValue, length, symbols.substring(0,1), symbols.substring(1,2), true, true);
    }

    public static String bar(int value, int maxValue, int length, String completed, String placeHolder, boolean withPercent, boolean withAmount) {
        double percent;
        if (maxValue != 0)
            percent = value / maxValue;
        else
            percent = 1.D;
        int amountPos = (int) Math.round(length * percent);
        int amountRest = length - amountPos;
        String output = "[" + completed.repeat(amountPos) + placeHolder.repeat(amountRest) + "] ";
        if (withPercent)
            output += percent * 100 + "% ";
        if (withAmount)
            output += value + "/" + maxValue;
        return output;
    }
}
