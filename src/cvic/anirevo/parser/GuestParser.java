package cvic.anirevo.parser;

import cvic.anirevo.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cvic.anirevo.model.anirevo.ArGuest;
import cvic.anirevo.model.anirevo.GuestManager;

public class GuestParser {

    private static final String TAG = "anirevo.GuestParser";

    public static void parseGuests(JSONArray guests) {
        //Clear GuestManager
        GuestManager.getInstance().clear();
        //
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
    }

}
