package id.tog.oauth2.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateFormat {
    public static final String PATTERN = "^(\\d{4}-\\d{2}-\\d{2})((T|\\s)(\\d{2}:\\d{2}:\\d{2})(\\.\\d{1,3})?([+-](\\d{1,4}))?)?$";

    public static boolean isDate(Object value) {
        if (value instanceof Date) {
            return true;
        }
        if (!(value instanceof String)) {
            return false;
        }

        Pattern pattern = Pattern.compile(PATTERN);
        return pattern.matcher((String) value).matches();
    }

    public static Date parse(String dateString) {
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(dateString);
        String dateFormat = "yyyy-MM-dd";
        Map<String, String> component = new HashMap<>();
        if (matcher.find() && matcher.groupCount() == 8) {
            component.put("input", matcher.group(0));
            component.put("date", matcher.group(1));
            if (null != matcher.group(3))
                component.put("separator", matcher.group(3));
            if (null != matcher.group(4))
                component.put("time", matcher.group(4));
            if (null != matcher.group(5))
                component.put("millisecond", matcher.group(5));
            if (null != matcher.group(6))
                component.put("timezone", matcher.group(6));
        }

        if (component.size() > 0) {
            if (component.containsKey("separator") && "T".equals(component.get("separator"))) {
                dateFormat += "'T'HH:mm:ss";
            }

            if (component.containsKey("millisecond")) {
                int milliSize = component.get("milliseconds").length() - 1;
                dateFormat += "." + (new String(new char[milliSize]).replace("\0", "S"));
            }

            if (component.containsKey("timezone")) {
                dateFormat += "Z";
            }
        }

        return DateFormat.parse(dateString, dateFormat);
    }

    public static Date parse(String dateString, Date time) {
        String timeString;
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        try {
            timeString = formatter.format(time);
        } catch (Exception e) {
            timeString = " 00:00:00";
        }

        return DateFormat.parse(dateString + " " + timeString);
    }

    public static Date parse(String dateString, String format) {
        if (null == format || "".equals(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        Date date;
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            date = null;
        }

        return date;
    }

    public static String format(Date date) {
        return DateFormat.format(date, "");
    }

    public static String format(Date date, String format) {
        if (null == format || "".equals(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        String result;
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            result = formatter.format(date);
        } catch (Exception e) {
            result = null;
        }

        return result;
    }
}

