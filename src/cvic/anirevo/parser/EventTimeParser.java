package cvic.anirevo.parser;

import cvic.anirevo.exceptions.InvalidTimeException;
import cvic.anirevo.model.calendar.EventTime;

public class EventTimeParser {

    /**
     * Parse a time string into EventTime
     * @param timeString    String in the format HH[:MM]PM/AM
     * @return              EventTime
     */
    public static EventTime parse(String timeString) {
        //last two chars are AM/PM
        String period = timeString.substring(timeString.length() - 2);
        timeString = timeString.substring(0, timeString.length() - 2);
        String[] time = timeString.split(":");
        int hour = Integer.parseInt(time[0]);
        int minute = 0;
        if (hour == 12) {
            hour = 0;
        }
        if (time.length > 1) {
            minute = Integer.parseInt(time[1]);
        }
        if (period.equals("PM")) {
            hour += 12;
        }
        try {
            return new EventTime(hour, minute);
        } catch (InvalidTimeException e) {
            return null;
        }
    }

}
