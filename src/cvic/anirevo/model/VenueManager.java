package cvic.anirevo.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class VenueManager {

    private static VenueManager instance;

    private static ObservableList<MapVenue> venues;

    public static VenueManager getInstance() {
        if (instance == null) {
            instance = new VenueManager();
        }
        return instance;
    }

    private VenueManager () {
        venues = FXCollections.observableArrayList();
    }

    public ObservableList<MapVenue> getVenues() {
        return venues;
    }

    public MapVenue getVenue (String name) {
        for (MapVenue venue : venues) {
            if (venue.getName().equals(name)) {
                return venue;
            }
        }
        MapVenue newVenue = new MapVenue(name);
        venues.add(newVenue);
        return newVenue;
    }

    public void clear () {
        venues.clear();
    }

}
