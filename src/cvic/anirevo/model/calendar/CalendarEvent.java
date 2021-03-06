package cvic.anirevo.model.calendar;

import cvic.anirevo.model.anirevo.ArEvent;
import cvic.anirevo.utils.TimeUtils;

public class CalendarEvent {

    private final ArEvent event;

    private CalendarDate date;
    private EventTime start;
    private EventTime end;

    public CalendarEvent(ArEvent event) {
        this.event = event;
    }

    public ArEvent getEvent() {
        return event;
    }

    public String getName() {
        return event.getTitle();
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

    @Override
    public String toString() {
        return String.format("%s: %s - %s", date.getName(), start, end);
    }
}
