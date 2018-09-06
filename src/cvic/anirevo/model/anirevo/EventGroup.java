package cvic.anirevo.model.anirevo;

import java.util.ArrayList;
import java.util.List;

class EventGroup {

    private final int headerColor;
    private final String headerText;

    private List<ArEvent> events;

    public EventGroup(int headerColor, String headerText) {
        this.headerColor = headerColor;
        this.headerText = headerText;
        events = new ArrayList<>();
    }


    public int getHeaderColor() {
        return headerColor;
    }

    public String getHeaderText() {
        return headerText;
    }

    public List<ArEvent> getEvents() {
        return events;
    }

    public void setEvents(List<ArEvent> events) {
        this.events = events;
    }
}
