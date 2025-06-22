package com.example.frontend.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static Date convertirStringADate(String dateString, String formatString) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(formatString);
            return format.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
