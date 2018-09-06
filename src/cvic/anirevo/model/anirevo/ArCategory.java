package cvic.anirevo.model.anirevo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArCategory implements Iterable<ArEvent>{

    /**
     * Represents a general category for ArEvents
     */

    private String title;

    private List<ArEvent> events;

    public ArCategory(String title) {
        this.title = title;
        events = new ArrayList<>();
    }

    public void addEvent(ArEvent event) {
        events.add(event);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public int size() {
        return events.size();
    }

    @Override
    public Iterator<ArEvent> iterator() {
        return events.iterator();
    }
}
