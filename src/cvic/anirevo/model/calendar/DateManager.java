package cvic.anirevo.model.calendar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

public class DateManager implements Iterable<CalendarDate>{

    private static DateManager instance;

    private int year;

    private ObservableList<CalendarDate> dates;

    public static DateManager getInstance() {
        if (instance == null ){
            instance = new DateManager();
        }
        return instance;
    }

    private DateManager() {
        dates = FXCollections.observableArrayList();
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public void addDate(CalendarDate date) {
        dates.add(date);
    }

    public CalendarDate getDate(String dateString) {
        for (CalendarDate date : dates) {
            if (date.getName().equals(dateString)) {
                return date;
            }
        }
        CalendarDate newDate = new CalendarDate(dateString);
        dates.add(newDate);
        return newDate;
    }

    public ObservableList<CalendarDate> getDates() {
        return dates;
    }

    @Override
    public Iterator<CalendarDate> iterator() {
        return dates.iterator();
    }

    public void clear() {
        dates.clear();
    }

}
