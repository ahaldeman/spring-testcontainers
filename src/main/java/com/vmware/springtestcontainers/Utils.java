package com.vmware.springtestcontainers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static boolean isDateInISOFormat(String dateString) {
        try {
            new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static Date parseDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return new Date(dateFormat.parse(dateString).getTime());
        } catch (ParseException e) {
            throw new RuntimeException("Unable to parse date: " + dateString, e);
        }
    }
}
