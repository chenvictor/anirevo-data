package cvic.anirevo.model.anirevo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ArLocation implements Iterable<ArEvent>{

    /**
     * Represents a Location for an ArEvent
     */

    private String purpose;
    private String location;
    private boolean schedule = false;   //true if the location should be shown in the schedule fragment

    private List<ArEvent> events;

    private UpdateListener mListener;

    public ArLocation(String purpose) {
        this.purpose = purpose;
        events = new ArrayList<>();
    }

    public void setListener(UpdateListener listener) {
        mListener = listener;
    }

    public void addEvent(ArEvent event) {
        if (!events.contains(event)) {
            events.add(event);
        }
    }

    public void setSchedule(boolean schedule) {
        this.schedule = schedule;
    }

    public boolean isSchedule() {
        return schedule;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        //rename any events with the old location name
        for (ArEvent event : EventManager.getInstance()) {
            if (event.getLocation().equals(this.purpose)) {
                event.setLocation(purpose);
            }
        }
        if (mListener != null) {
            mListener.update();
        }
        this.purpose = purpose;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArLocation that = (ArLocation) o;
        return Objects.equals(purpose, that.purpose) &&
                Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(purpose);
    }

    @Override
    public Iterator<ArEvent> iterator() {
        return events.iterator();
    }

    public interface UpdateListener {
        void update();
    }

    @Override
    public String toString() {
        return getPurpose() + " - " + getLocation();
    }

}
