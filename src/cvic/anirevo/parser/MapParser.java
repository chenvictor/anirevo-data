package cvic.anirevo.parser;

import cvic.anirevo.Log;
import cvic.anirevo.model.MapVenue;
import cvic.anirevo.model.VenueManager;
import org.json.JSONArray;
import org.json.JSONObject;

public class MapParser {

    public static void parse(JSONArray venues) {
        Log.i("MapParser", "Parsing " + venues.length() + " venue(s)");
        for (int i = 0; i < venues.length(); i++) {
            parseVenue(venues.getJSONObject(i));
        }
    }

    private static void parseVenue(JSONObject venue) {
        MapVenue mapVenue = VenueManager.getInstance().getVenue(venue.getString("name"));
        mapVenue.setImagePath(venue.getString("image"));
    }

}
