package cvic.anirevo.model.calendar;

import cvic.anirevo.model.anirevo.ArEvent;
import cvic.anirevo.model.anirevo.ArLocation;

public class LocatedCalendarEvent extends CalendarEvent {

    private ArLocation mLocation;

    public LocatedCalendarEvent() {
        super(null);
    }

    public void setLocation(ArLocation location) {
        mLocation = location;
    }

    public ArLocation getLocation() {
        return mLocation;
    }

    @Override
    public ArEvent getEvent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return mLocation.getPurpose() + " " + super.toString();
    }
}
