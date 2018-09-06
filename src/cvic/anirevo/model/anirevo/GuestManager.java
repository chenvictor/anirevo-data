package cvic.anirevo.model.anirevo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cvic.anirevo.exceptions.InvalidIdException;

public class GuestManager {

    /**
     * Singleton Manager to keep track of ArGuests
     */

    private static GuestManager instance;

    private List<ArGuest> guests;

    public static GuestManager getInstance() {
        if (instance == null) {
            instance = new GuestManager();
        }
        return instance;
    }

    private GuestManager() {
        guests = new ArrayList<>();
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
        ArGuest newGuest = new ArGuest(name, guests.size());
        guests.add(newGuest);
        return newGuest;
    }

    public List<ArGuest> getGuests() {
        return Collections.unmodifiableList(guests);
    }

    public int size(){
        return guests.size();
    }

    public void clear() {
        guests.clear();
    }
}
