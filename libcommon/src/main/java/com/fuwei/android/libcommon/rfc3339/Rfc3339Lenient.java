package com.fuwei.android.libcommon.rfc3339;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.TimeZone;

/**
 * Created by fuwei on 4/26/22.
 */
public class Rfc3339Lenient implements Rfc3339Parser{
    private static final String formatTemplateOffset = "yyyy-MM-dd'T'HH:mm:ssXXX";
    private static final String formatTemplateOffsetPrecise = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private static final String formatTemplateZulu = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String formatTemplateZuluPrecise = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * Parse a RFC 3339-compliant date time string into a {@link Date} instance with millisecond
     * precision. Time zone information is respected.
     * @param timeString a string to parse
     * @return a Date with the resulting date time
     * @throws ParseException if the date format does not conform to RFC 3339
     */
    public synchronized Date parse(String timeString) throws ParseException {
        // allow lowercase per https://tools.ietf.org/html/rfc3339#section-5.6
        timeString = timeString.toUpperCase();
        char tzStyle = timeString.charAt(19);

        // allow time-secfrac
        if('.' == tzStyle){
            try {
                tzStyle = getTimezoneStyle(timeString, 'Z', '+', '-');
            } catch (NoSuchElementException e) {
                throw new Rfc3339Exception(String.format("Invalid time zone notation from input <%s>", timeString), 19);
            }
            timeString = reducePrecision(timeString, tzStyle);
            tzStyle &=~ 3;
        }

        switch(tzStyle) {
            case '-':
            case '+':
                return parseInternal(timeString, formatTemplateOffset);
            case 'Z':
                return parseInternalZulu(timeString, formatTemplateZulu);
            case ',':
            case '(':
                return parseInternal(timeString, formatTemplateOffsetPrecise);
            case 'X':
                return parseInternalZulu(timeString, formatTemplateZuluPrecise);
        }
        throw new Rfc3339Exception(String.format("Invalid time format on input <%s>", timeString));
    }

    /**
     * Parse a RFC 3339-compliant time string and get time zone information
     * @param timeString a time string
     * @return a custom {@link TimeZone} with the correct offset
     * @throws Rfc3339Exception if timeString does not contain a RFC 3339 valid time zone
     */
    public TimeZone parseTimezone(String timeString) throws ParseException{
        // allow lowercase per https://tools.ietf.org/html/rfc3339#section-5.6
        timeString = timeString.toUpperCase();
        char tzStyle = timeString.charAt(19);

        // allow time-secfrac
        if('.' == tzStyle){
            try {
                tzStyle = getTimezoneStyle(timeString, 'Z', '+', '-');
            } catch (NoSuchElementException e) {
                throw new Rfc3339Exception(String.format("Invalid time zone notation from input <%s>", timeString));
            }
        }

        if(tzStyle == 'Z'){
            return TimeZone.getTimeZone("UTC");
        } else {
            String timeZoneId = "GMT" + timeString.substring(timeString.length()-6);
            TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);

            // returns GMT if custom time zone creation was unsuccessful.
            // GMT and other named time zones are not valid in RFC 3339.
            if(timeZone.getID().equals("GMT")){
                throw new Rfc3339Exception("Invalid time zone id");
            } else if(timeZone.getID().equals("GMT-00:00")){
                timeZone.setID("Etc/Unknown");
            }

            return timeZone;
        }
    }

    /**
     * Parse a RFC 3339-compliant date time string into a {@link Date} instance with millisecond
     * precision. Time zone information is respected and applied.
     * @param timeString a string to parse
     * @return a resulting calendar
     * @throws ParseException if the date format does not conform to RFC 3339
     */
    public synchronized Calendar parseCalendar(String timeString) throws ParseException {
        Date date = parse(timeString);
        TimeZone timeZone = parseTimezone(timeString);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(timeZone);
        return calendar;
    }

    /**
     * Get a arbitrary precision timestamp from an RFC 3339 time string
     * @param timeString a formatted time string
     * @return a {@link BigDecimal}
     * @throws ParseException if the date format does not conform to RFC 3339
     */
    public BigDecimal parsePrecise(String timeString) throws ParseException {
        timeString = timeString.toUpperCase();
        char delim;
        try {
            delim = getTimezoneStyle(timeString, 'Z', '-', '+');
        } catch (NoSuchElementException e){
            throw new Rfc3339Exception(String.format("Invalid time zone notation from input <%s>", timeString), 19);
        }
        long time = parse(timeString).getTime() / 1000;
        int index = timeString.lastIndexOf(delim);
        BigDecimal scaledFractional = new BigDecimal("0." + timeString.substring(20, index));
        BigDecimal timeStamp = new BigDecimal(time);
        return timeStamp.add(scaledFractional);
    }

    /**
     * Parse a date string with appropriate time zone template.
     * @param timeString time string to parse
     * @return a resulting date
     */
    private synchronized Date parseInternal(String timeString, String parseTemplate) throws ParseException{
        SimpleDateFormat timeFormat = new SimpleDateFormat(parseTemplate, Locale.getDefault());
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = timeFormat.parse(timeString);
        return parseOffset(date, timeString);
    }

    /**
     * Parse a date string as UTC/Zulu with appropriate formatting template.
     * @param timeString time string to parse
     * @return a resulting date
     */
    private synchronized Date parseInternalZulu(String timeString, String parseTemplate) throws ParseException{
        SimpleDateFormat formatterZulu = new SimpleDateFormat(parseTemplate, Locale.getDefault());
        formatterZulu.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = formatterZulu.parse(timeString);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return calendar.getTime();
    }

    /**
     * Parse a date string with appropriate time zone template.
     * @param timeString time string to parse
     * @return a resulting date
     * @throws Rfc3339Exception when the timezone could not be parsed
     */
    private synchronized Date parseOffset(Date date, String timeString) throws ParseException{
        String timeZoneId = "GMT" + timeString.substring(timeString.length()-6);
        TimeZone timeZone = parseTimezone(timeString);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(timeZone);
        return calendar.getTime();
    }

    private static char getTimezoneStyle(String timeString, char... styleIds){
        for (char c: styleIds) {
            int lastIndex = timeString.lastIndexOf(c);
            if(lastIndex != -1 && lastIndex >= 19){
                return c;
            }
        }
        throw new NoSuchElementException();
    }

    /**
     * Reduce a time string's fractional second precision to ensure correct parsing.
     * Required as per <a href="https://tools.ietf.org/html/rfc3339#section-5.6>
     *     RFC 3339 ??5.6</a> as consequence of <a
     * href="https://tools.ietf.org/html/rfc2234#section-3.6">RFC 2234 &sect;3.6</a>
     * which allows 1-n digits of decimal fractions while java's date only allows
     * up to millisecond precision.
     * @param timeString a non-null input string
     * @param delim a time zone delimiter to signal the next part (e.g. Z, +, -)
     * @return a time string with at most 3 fractional second digits
     */
    static String reducePrecision(String timeString, char delim) throws ParseException{
        int index = timeString.lastIndexOf(delim);
        int fracLength = index - 19;
        if(fracLength > 3){
            timeString = timeString.substring(0, 23) + timeString.substring(index, timeString.length());
        } else if(fracLength > 1){
            String padding = fracLength == 3 ? "0" : "00";
            timeString = timeString.substring(0, index) + padding + timeString.substring(index, timeString.length());
        } else {
            throw new Rfc3339Exception("Invalid delimiter");
        }
        return timeString;
    }
}
