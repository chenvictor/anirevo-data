package cvic.anirevo.parser;

import cvic.anirevo.Log;
import cvic.anirevo.model.ArShow;
import cvic.anirevo.model.ViewingRoomManager;
import cvic.anirevo.model.ViewingRoomManager.ViewingRoom;
import cvic.anirevo.model.calendar.CalendarDate;
import cvic.anirevo.model.calendar.DateManager;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewingRoomParser {

    private static final String TAG = "anirevo.VRParser";

    public static void parseViewingRoom(JSONArray array) {
        Log.i(TAG, array.length() + " day(s) to parse");
        for (int i = 0; i < array.length(); i++) {
            try {
                parseDay(array.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i(TAG, "JSONError hit, skipping day");
            }
        }
    }

    private static void parseDay(JSONObject day) throws JSONException {
        CalendarDate date = DateManager.getInstance().getDate(day.getString("day"));
        if (day.has("start")) {
            date.setShowStartHour(day.getString("start"));
        }
        ObservableList<ViewingRoom> roomsList = ViewingRoomManager.getInstance().getViewingRooms(date);
        JSONArray rooms = day.getJSONArray("rooms");
        for (int i = 0; i < rooms.length(); i++) {
            try {
                parseRoom(rooms.getJSONObject(i), roomsList);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i(TAG, "JSONError hit, skipping room");
            }
        }
    }

    private static void parseRoom(JSONObject room, ObservableList<ViewingRoom> roomsList) throws JSONException {
        String purpose = room.getString("purpose");
        JSONArray shows = room.getJSONArray("shows");

        ViewingRoom viewingRoom = null;
        for (ViewingRoom vRoom : roomsList) {
            if (vRoom.getPurpose().equals(purpose)) {
                viewingRoom = vRoom;
                break;
            }
        }
        if (viewingRoom == null) {
            //add new viewing room if not found
            viewingRoom = new ViewingRoom(purpose);
            roomsList.add(viewingRoom);
        }

        for (int i = 0; i < shows.length(); i++) {
            try {
                parseShow(shows.getJSONObject(i), viewingRoom.getShows());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i(TAG, "JSONError hit, skipping show");
            }
        }
    }

    private static void parseShow(JSONObject show, ObservableList<ArShow> showList) throws JSONException {
        String title = show.getString("title");
        ArShow arShow = new ArShow(title);
        if (show.has("age")) {
            arShow.setAge(show.getInt("age"));
        }
        if (show.has("desc")) {
            arShow.setDescription(show.getString("desc"));
        }
        showList.add(arShow);
    }

}
