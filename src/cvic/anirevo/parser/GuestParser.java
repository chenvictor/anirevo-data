package cvic.anirevo.parser;

import cvic.anirevo.Log;
import cvic.anirevo.model.anirevo.LocationManager;
import cvic.anirevo.model.calendar.DateManager;
import cvic.anirevo.model.calendar.LocatedCalendarEvent;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cvic.anirevo.model.anirevo.ArGuest;
import cvic.anirevo.model.anirevo.GuestManager;

public class GuestParser {

    private static final String TAG = "anirevo.GuestParser";

    public static void parseGuests(JSONArray guests) {
        Log.i(TAG, guests.length() + " guests(s) to parse");
        for(int i = 0; i < guests.length(); i++) {
            try {
                parseGuest(guests.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i(TAG, "JSONError hit.");
            }
        }
    }

    private static void parseGuest(JSONObject guest) throws JSONException {
        String name = guest.getString("name");
        String title = guest.getString("title");
        ArGuest arGuest = GuestManager.getInstance().getGuest(name);
        arGuest.setTitle(title);
        if (guest.has("japanese")) {
            String japanese = guest.getString("japanese");
            arGuest.setJapanese(japanese);
        }
        if (guest.has("image")) {
            String image = guest.getString("image");
            arGuest.setPortraitPath(image);
        }
        if (guest.has("autographs")) {
            JSONArray autographs = guest.getJSONArray("autographs");
            parseGuestEvents(arGuest.getAutograph(), autographs);
        }
        if (guest.has("photobooth")) {
            JSONArray photobooth = guest.getJSONArray("photobooth");
            parseGuestEvents(arGuest.getPhotobooth(), photobooth);
        }
    }

    private static void parseGuestEvents(ObservableList<LocatedCalendarEvent> listToAddTo, JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            JSONObject event = array.getJSONObject(i);
            parseEvent(listToAddTo, event);
        }
    }

    private static void parseEvent(ObservableList<LocatedCalendarEvent> listToAddTo, JSONObject event) {
        try {
            LocatedCalendarEvent lcEvent = new LocatedCalendarEvent();
            lcEvent.setDate(DateManager.getInstance().getDate(event.getString("date")));
            lcEvent.setLocation(LocationManager.getInstance().getLocation(event.getString("location")));
            lcEvent.setStart(EventTimeParser.parse(event.getString("start")));
            lcEvent.setEnd(EventTimeParser.parse(event.getString("end")));

            listToAddTo.add(lcEvent);
        } catch (JSONException e) {
            Log.i(TAG, "JSON Error hit, skipping event");
            e.printStackTrace();
        }
    }

}
