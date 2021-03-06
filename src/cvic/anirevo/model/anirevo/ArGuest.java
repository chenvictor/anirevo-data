package cvic.anirevo.model.anirevo;

import cvic.anirevo.model.calendar.LocatedCalendarEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ArGuest {

    private String name;
    private String title;
    private String japanese;

    private String portraitPath;

    private Set<ArEvent> events;

    private ObservableList<LocatedCalendarEvent> autograph;
    private ObservableList<LocatedCalendarEvent> photobooth;

    private ChangeListener mListener;

    public ArGuest(String name) {
        this.name = name;
        events = new HashSet<>();
        autograph = FXCollections.observableArrayList();
        photobooth = FXCollections.observableArrayList();
    }

    public ObservableList<LocatedCalendarEvent> getAutograph() {
        return autograph;
    }

    public ObservableList<LocatedCalendarEvent> getPhotobooth() {
        return photobooth;
    }

    public void setListener(ChangeListener listener) {
        mListener = listener;
    }

    public void setName(String name) {
        this.name = name;
        if (mListener != null) {
            mListener.update();
        }
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArGuest guest = (ArGuest) o;
        return Objects.equals(name, guest.name) &&
                Objects.equals(title, guest.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, title);
    }

    public void addEvent(ArEvent arEvent) {
        events.add(arEvent);
    }

    public boolean hasJapanese() {
        return japanese != null;
    }

    public String getJapanese() {
        return japanese;
    }

    public void setJapanese(String japanese) {
        this.japanese = japanese;
    }

    public String getPortraitPath() {
        return portraitPath;
    }

    public void setPortraitPath(String portraitPath) {
        this.portraitPath = portraitPath;
    }

    public Set<ArEvent> getEvents() {
        return events;
    }

    @Override
    public String toString() {
        return getName();
    }

    public interface ChangeListener {
        void update();
    }

}
