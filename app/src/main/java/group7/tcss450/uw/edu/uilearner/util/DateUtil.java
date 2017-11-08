package group7.tcss450.uw.edu.uilearner.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Used for formatting dates between backend and frontend
 */

public class DateUtil {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);


    /**
     *@return string array where index 0 is day entered at 0 o'clock, and index 1 is day after at 0 o'clock
     */
    public static String[] getWholeDayStartEnd(int year, int month, int day) {
        String[] strings = new String[2];
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, 0, 0);
        Date startDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = cal.getTime();
        strings[0] = FORMAT.format(startDate);
        strings[1] = FORMAT.format(endDate);

        return strings;
    }

    public static Date getDateFromRfcString(String rfcString) {
        try {
            return FORMAT.parse(rfcString);
        } catch (ParseException e) {
            return null;
        }
    }
}
