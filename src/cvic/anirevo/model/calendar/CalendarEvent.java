package cvic.anirevo.model.calendar;

import cvic.anirevo.model.anirevo.ArEvent;
import cvic.anirevo.utils.TimeUtils;

/**
 * Created by FRAMGIA\pham.van.khac on 07/07/2016.
 * Adapted for AniRevo by chenvictor
 */

public class CalendarEvent {

    private static int defaultColor = -1;

    private final ArEvent event;

    private CalendarDate date;
    private EventTime start;
    private EventTime end;

    private int color = defaultColor;

    public static void setDefaultColor(int color) {
        defaultColor = color;
    }

    public CalendarEvent(ArEvent event) {
        this.event = event;
    }

    public ArEvent getEvent() {
        return event;
    }

    public String getName() {
        return event.getTitle();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public EventTime getStartTime() {
        return start;
    }

    public EventTime getEndTime() {
        return end;
    }

    public void setStart(EventTime start) {
        this.start = start;
    }

    public void setEnd(EventTime end) {
        //if end time 'before' start, add 24 hours
        if (TimeUtils.timeAfter(start, end)) {
            end.increment();
        }
        this.end = end;
    }

    public CalendarDate getDate() {
        return date;
    }

    public void setDate(CalendarDate date) {
        this.date = date;
    }

    //Extra Helpers to assist with Calendar Fragment
    public int getStartHour() {
        return start.getHour();
    }

    public int getEndHour() {
        if (end.getMinute() == 0) {
            return end.getHour();
        } else {
            return end.getHour() + 1;
        }
    }

    @Override
    public String toString() {
        return String.format("%s: %s - %s", date.getName(), start, end);
    }
}
