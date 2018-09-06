package cvic.anirevo.model.anirevo;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ArGuest {

    private final int id;

    private final String name;
    private String title;
    private String japanese;

    private Set<ArEvent> events;

    ArGuest(String name, int id) {
        this.id = id;
        this.name = name;
        events = new HashSet<>();
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

    public int getId() {
        return id;
    }
}
