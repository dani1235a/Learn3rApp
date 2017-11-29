package group7.tcss450.uw.edu.uilearner.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import group7.tcss450.uw.edu.uilearner.EventFragment;

/**
 * Used for formatting dates between backend and frontend
 */

public class DateUtil {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);


    /**
     *@return string array where index 0 is day entered at 0 o'clock, and index 1 is day after at 0 o'clock
     */
    public static String[] getWholeDayStartEnd(int year, int month, int day) {
        Log.d("EVENT", "" + month + "/" + day + "/" + year);
        String[] strings = new String[2];
        Calendar cal = Calendar.getInstance();
        cal.set(year, month-1, day, 0, 0, 0);
        Date startDate = cal.getTime();
        Log.d("EVENT", startDate.toString());
        cal.set(year, month, day, 23, 59, 59);
        Date endDate = cal.getTime();
        Log.d("EVENT", endDate.toString());
        strings[0] = FORMAT.format(startDate);
        strings[1] = FORMAT.format(endDate);

        return strings;
    }


    /**
     *
     * @param year year, 1 indexed
     * @param month 0 indexed
     * @param day 1 indexed
     * @param hour 1 indexed
     * @param minute 1 indexed
     * @return 0 index is current time formatted to date, 1 index is the end of the day.
     */
    public static String[] getRestOfDay(int year, int month, int day, int hour, int minute) {
        String[] strs = new String[2];
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, minute);
        Date start = cal.getTime();
        cal.set(year, month, day, 23, 59, 59);
        Date end = cal.getTime();
        strs[0] = FORMAT.format(start);
        strs[1] = FORMAT.format(end);
        return strs;
    }

    public static String getRfcString(int year, int month, int day, int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, minute);
        return FORMAT.format(cal.getTime());
    }

    public static Date getDateFromRfcString(String rfcString) {
        try {
            return FORMAT.parse(rfcString);
        } catch (ParseException e) {
            return null;
        }
    }
}
