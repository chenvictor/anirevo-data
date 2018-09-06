package cvic.anirevo.parser;

import java.util.Calendar;

import cvic.anirevo.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cvic.anirevo.model.calendar.CalendarDate;
import cvic.anirevo.model.calendar.DateManager;

public class InfoParser {

    private static final String TAG = "cvic.anirevo.InfoParser";

    public static void parseInfo(JSONObject info) {
        int year;
        try {
            year = info.getInt("year");
        } catch (JSONException e) {
            year = Calendar.getInstance().get(Calendar.YEAR);
            Log.i(TAG, "Year not found, defaulting to " + String.valueOf(year));
        }
        DateManager.getInstance().setYear(year);
        try {
            parseDates(info.getJSONArray("dates"));
        } catch (JSONException e) {
            Log.i(TAG, "Dates not found, defaulting to empty initialization");
        }
    }

    private static void parseDates(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            try {
                CalendarDate date = DateManager.getInstance().getDate(array.getString(i));
                Log.i(TAG, "Added date: " + date.getName());
            } catch (JSONException e) {
                Log.i(TAG, "Error Parsing, skipping date with idx " + String.valueOf(i));
            }
        }
    }

}
