package cvic.anirevo.model;

import cvic.anirevo.model.calendar.CalendarDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ViewingRoomManager {

    private static final int NUM_VIEWING_ROOMS = 2;

    private static ViewingRoomManager instance;

    private Map<CalendarDate, ObservableList<ViewingRoom>> viewingRooms;

    public static ViewingRoomManager getInstance() {
        if (instance == null) {
            instance = new ViewingRoomManager();
        }
        return instance;
    }

    private ViewingRoomManager() {
        viewingRooms = new HashMap<>();
    }

    public Set<CalendarDate> getKeys() {
        return viewingRooms.keySet();
    }

    public ObservableList<ViewingRoom> getViewingRooms(CalendarDate date) {
        if (viewingRooms.containsKey(date)) {
            return viewingRooms.get(date);
        }
        ObservableList<ViewingRoom> newList = FXCollections.observableArrayList();
        //Initialize viewing room with NUM_VIEWING_ROOMS rooms
        for (int i = 1; i <= NUM_VIEWING_ROOMS; i++) {
            ViewingRoom room = new ViewingRoom("Viewing Room " + String.valueOf(i));
            newList.add(room);
        }
        viewingRooms.put(date, newList);
        return newList;
    }

    public static class ViewingRoom {

        private String purpose;
        private ObservableList<ArShow> shows;

        public ViewingRoom(String purpose) {
            this.purpose = purpose;
            shows = FXCollections.observableArrayList();
        }

        public String getPurpose () {
            return purpose;
        }

        public ObservableList<ArShow> getShows() {
            return shows;
        }

        @Override
        public String toString() {
            return getPurpose();
        }
    }

    public void clear () {
        viewingRooms.clear();
    }

}
