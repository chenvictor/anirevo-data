package cvic.anirevo.model.calendar;

import cvic.anirevo.exceptions.InvalidTimeException;
import cvic.anirevo.utils.TimeUtils;

import java.util.Locale;

public class EventTime {

    private int hour;
    private int minute;

    public EventTime(int hour, int minute) throws InvalidTimeException {
        TimeUtils.checkMinute(minute);
        this.hour = hour;
        this.minute = minute;
    }

    public int getMinute() {
        return minute;
    }

    public int getHour() {
        return hour;
    }

    @Override
    public String toString() {
        return TimeUtils.formatTimeString(hour, minute);
    }

    public void increment() {
        this.hour += 24;
    }

    public String shortString() {
        int hourNum = hour % 12;
        if (hourNum == 0)
            hourNum = 12;
        String period = ((hour) % 24 < 12) ? "AM" : "PM";
        if (minute == 0) {
            return String.format(Locale.CANADA, "%d%s", hourNum, period);
        }
        return String.format(Locale.CANADA,"%d:%02d%s", hourNum, minute, period);
    }
}
