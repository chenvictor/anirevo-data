package cvic.anirevo.model.anirevo;

import cvic.anirevo.exceptions.InvalidIdException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Iterator;

public class GuestManager implements Iterable<ArGuest>{

    /**
     * Singleton Manager to keep track of ArGuests
     */

    private static GuestManager instance;

    private ObservableList<ArGuest> guests;

    public static GuestManager getInstance() {
        if (instance == null) {
            instance = new GuestManager();
        }
        return instance;
    }

    private GuestManager() {
        guests = FXCollections.observableArrayList();
    }

    public ArGuest getGuest(int id) throws InvalidIdException {
        if (id < 0 || id >= guests.size())
            throw new InvalidIdException();
        return guests.get(id);
    }

    public ArGuest getGuest(String name) {
        for (ArGuest guest : guests) {
            if (guest.getName().equals(name)) {
                return guest;
            }
        }
        ArGuest newGuest = new ArGuest(name);
        guests.add(newGuest);
        return newGuest;
    }

    public ObservableList<ArGuest> getGuests() {
        return guests;
    }

    public int size(){
        return guests.size();
    }

    public void clear() {
        guests.clear();
    }

    @Override
    public Iterator<ArGuest> iterator() {
        return guests.iterator();
    }
}
