package cvic.anirevo.model.anirevo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class LocationManager implements Iterable<ArLocation>{

    private static LocationManager instance;

    private ObservableList<ArLocation> locations;

    public static LocationManager getInstance() {
        if(instance == null) {
            instance = new LocationManager();
        }
        return instance;
    }

    private LocationManager() {
        locations = FXCollections.observableArrayList();
    }

    public ArLocation getLocation(String purpose) {
        for(ArLocation loc : locations) {
            if (loc.getPurpose().equals(purpose)) {
                return loc;
            }
        }
        ArLocation newLoc = new ArLocation(purpose);
        locations.add(newLoc);
        return newLoc;
    }

    public ObservableList<ArLocation> getLocations() {
        return locations;
    }

    public int size() {
        return locations.size();
    }

    public ArLocation getLocation(int id) {
        return locations.get(id);
    }

    public void clear() {
        locations.clear();
    }

    @Override
    public Iterator<ArLocation> iterator() {
        return locations.iterator();
    }

    public List<ArLocation> getScheduleEvents() {
        List<ArLocation> list = new ArrayList<>();
        for (ArLocation loc : locations) {
            if (loc.isSchedule()) {
                list.add(loc);
            }
        }
        return Collections.unmodifiableList(list);
    }

}
