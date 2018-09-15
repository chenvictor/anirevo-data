package cvic.anirevo.model.anirevo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import cvic.anirevo.model.calendar.CalendarEvent;

public class ArEvent {

    /**
     * Represents an event with a time, location, etc.
     */

    private TitleChangeListener mListener;

    private ArCategory category;
    private String title;
    private String location;
    private String desc;
    private int restriction = 0;

    private List<CalendarEvent> timeblocks;
    private Set<ArGuest> guests;
    private Set<ArTag> tags;

    public ArEvent(String title) {
        this.title = title;
        tags = new HashSet<>();
        timeblocks = new ArrayList<>();
        guests = new HashSet<>();
    }

    public void setListener(TitleChangeListener listener) {
        mListener = listener;
    }

    public void addTag(ArTag tag) {
        tags.add(tag);
    }

    public void setTitle(String title) {
        this.title = title;
        if (mListener != null) {
            mListener.titleChanged(title);
        }
    }

    public void clearTimeblocks() {
        timeblocks.clear();
    }

    public void addTimeblock(CalendarEvent calendarEvent) {
        timeblocks.add(calendarEvent);
    }

    public List<CalendarEvent> getTimeblocks() {
        return Collections.unmodifiableList(timeblocks);
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArEvent arEvent = (ArEvent) o;
        return  Objects.equals(title, arEvent.title) &&
                Objects.equals(location, arEvent.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location);
    }

    public ArCategory getCategory() {
        return category;
    }

    public void setCategory(ArCategory category) {
        this.category = category;
    }

    public int getRestriction() {
        return restriction;
    }

    public void setRestriction(int restriction) {
        this.restriction = restriction;
    }

    public Set<ArGuest> getGuests() {
        return guests;
    }

    @Override
    public String toString() {
        return getTitle();
    }

    public interface TitleChangeListener {

        void titleChanged(String newTitle);

    }

}
