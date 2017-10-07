package com.tagmycode.plugin;

import java.text.DecimalFormat;

public class FileSize {

    private static final double BYTE_FRACTION = 1024.0;
    private static DecimalFormat dec = new DecimalFormat("0.00");

    public static String toHumanReadable(double bytes) {
        String humanReadable;
        double kilo = bytes / BYTE_FRACTION;
        double mega = ((bytes / BYTE_FRACTION) / BYTE_FRACTION);
        double giga = (((bytes / BYTE_FRACTION) / BYTE_FRACTION) / BYTE_FRACTION);
        double tera = ((((bytes / BYTE_FRACTION) / BYTE_FRACTION) / BYTE_FRACTION) / BYTE_FRACTION);

        if (tera > 1) {
            humanReadable = concatUnit(decimalToString(tera), "TB");
        } else if (giga > 1) {
            humanReadable = concatUnit(decimalToString(giga), "GB");
        } else if (mega > 1) {
            humanReadable = concatUnit(decimalToString(mega), "MB");
        } else if (kilo > 1) {
            humanReadable = concatUnit(decimalToString(kilo), "KB");
        } else {
            humanReadable = concatUnit(String.valueOf((int) bytes), "Bytes");
        }

        return humanReadable;
    }

    private static String decimalToString(double tera) {
        return dec.format(tera);
    }

    private static String concatUnit(String value, String unit) {
        return value.concat(unit);
    }
}
