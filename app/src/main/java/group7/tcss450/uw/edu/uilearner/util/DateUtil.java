package group7.tcss450.uw.edu.uilearner.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Used for formatting dates between backend and frontend
 */

public class DateUtil {


    /**
     *
     */
    public static String[] getWholeDayStartEnd(int year, int month, int day) {
        String[] strings = new String[2];
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, 0, 0);
        Date startDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = cal.getTime();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);
        strings[0] = formatter.format(startDate);
        strings[1] = formatter.format(endDate);

        return strings;
    }
}
