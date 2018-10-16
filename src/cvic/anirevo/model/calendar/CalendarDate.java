package cvic.anirevo.model.calendar;

import java.util.Objects;

public class CalendarDate {

    private String name;

    private String showStartHour;

    public CalendarDate(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalendarDate that = (CalendarDate) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getShowStartHour() {
        return showStartHour;
    }

    public void setShowStartHour(String showStartHour) {
        this.showStartHour = showStartHour;
    }
}
