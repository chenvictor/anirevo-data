package cvic.anirevo.utils;

import java.util.Locale;

import cvic.anirevo.exceptions.InvalidTimeException;
import cvic.anirevo.model.calendar.EventTime;

public class TimeUtils {

    public static String formatHourString(int hour) {
        //Format into a 12hr AM/PM with wraparound. eg(hour 25 = 1AM next day)
        int hourNum = hour % 12;
        if (hourNum == 0)
            hourNum = 12;
        String period = ((hour) % 24 < 12) ? "AM" : "PM";
        return String.valueOf(hourNum) + period;
    }

    public static String formatTimeString(int hour, int minute) {
        int hourNum = hour % 12;
        if (hourNum == 0)
            hourNum = 12;
        String period = ((hour) % 24 < 12) ? "AM" : "PM";
        return String.format(Locale.CANADA,"%d:%02d%s", hourNum, minute, period);
    }

    public static void checkMinute(int minute) throws InvalidTimeException {
        if (minute < 0 || minute > 59) {
            throw new InvalidTimeException();
        }
    }

    /**
     * Compare two EventTimes
     * @param time1 time1
     * @param time2 time2
     * @return      True if time1 is after time2
     */
    public static boolean timeAfter(EventTime time1, EventTime time2) {
        if (time1.getHour() > time2.getHour()) {
            return true;
        } else if(time1.getHour() == time2.getHour()) {
            return time1.getMinute() > time2.getMinute();
        } else {
            return false;
        }
    }

}
