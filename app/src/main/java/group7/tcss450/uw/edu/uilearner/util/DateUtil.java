package group7.tcss450.uw.edu.uilearner.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import group7.tcss450.uw.edu.uilearner.EventFragment;

/**
 * Used for formatting dates between backend and frontend.
 *
 * @author Myles, Connor
 */

public class DateUtil {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

    static {
        FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }


    /**
     *@return string array where index 0 is day entered at 0 o'clock, and index 1 is day after at 0 o'clock.
     *
     * @author Myles, Connor
     */
    public static String[] getWholeDayStartEnd(int year, int month, int day) {
        String[] strings = new String[2];
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, 0, 0, 0);
        Date startDate = cal.getTime();
        cal.set(year, month, day, 23, 59, 59);
        Date endDate = cal.getTime();
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
     * @author Myles
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

    /**
     *
     * @param year year, 1 indexed
     * @param month 0 indexed
     * @param day 1 indexed
     * @param startHour 1 indexed
     * @param startMinute 1 indexed
     * @param endHour 1 indexed
     * @param endMinute 1 indexed
     * @return 0 index is current time formatted to date, 1 index is the end of the day.
     * @author Connor
     */
    public static String[] getStartAndEndDate(int year, int month, int day, int startHour, int startMinute, int endHour, int endMinute) {
        String[] strs = new String[2];
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, startHour, startMinute);
        Date start = cal.getTime();
        cal.set(year, month, day, endHour, endMinute);
        Date end = cal.getTime();
        strs[0] = FORMAT.format(start);
        strs[1] = FORMAT.format(end);
        return strs;
    }

    /**
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @return
     * @author Myles
     */
    public static String getRfcString(int year, int month, int day, int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, minute);
        return FORMAT.format(cal.getTime());
    }


    /**
     * Parses the time string coming from the backend and converts it to something
     * more readable for the user than RFC format.
     *
     * @param rfc1
     * @param rfc2
     * @return
     * @author Myles
     */
    public static String getCardStartEnd(String rfc1, String rfc2) {
        try {
            Date start = FORMAT.parse(rfc1);
            Date end = FORMAT.parse(rfc2);
            Calendar cal = Calendar.getInstance();
            cal.setTime(start);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);
            int startHour = cal.get(Calendar.HOUR_OF_DAY);
            int startMinute = cal.get(Calendar.MINUTE);
            cal.setTime(end);
            int endHour = cal.get(Calendar.HOUR_OF_DAY);
            int endMinute = cal.get(Calendar.MINUTE);

            String endAmPm = (endHour >= 12) ? "PM" : "AM";
            endHour = (endHour > 12) ? endHour - 12 : endHour;
            endHour = (endHour == 0) ? 12 : endHour;

            String startAmPm = (startHour >= 12) ? "PM" : "AM";
            startHour = (startHour >= 12) ? startHour - 12 : startHour;
            startHour = (startHour == 0) ? 12 : startHour;

            return String.format(Locale.US ,"%02d/%02d/%d @ %d:%02d%s - %d:%02d%s"
                    , month, day, year,
                    startHour, startMinute, startAmPm
                    , endHour, endMinute, endAmPm);

        } catch(ParseException e) {
            return "Failed to parse dates - check dateUtil";
        }
    }

    /**
     *
     * @param rfcString
     * @return
     * @author Myles
     */
    public static Date getDateFromRfcString(String rfcString) {
        try {
            return FORMAT.parse(rfcString);
        } catch (ParseException e) {
            return null;
        }
    }
}
