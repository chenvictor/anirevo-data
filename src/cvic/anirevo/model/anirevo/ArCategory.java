package cvic.anirevo.model.anirevo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Iterator;

public class ArCategory implements Iterable<ArEvent>{

    /**
     * Represents a general category for ArEvents
     */

    private String title;

    private ObservableList<ArEvent> events;

    public ArCategory(String title) {
        this.title = title;
        events = FXCollections.observableArrayList();
    }

    public ObservableList<ArEvent> getEvents() {
        return events;
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
