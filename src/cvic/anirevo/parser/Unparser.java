package cvic.anirevo.parser;

import cvic.anirevo.editor.DataPaths;
import cvic.anirevo.model.calendar.CalendarDate;
import cvic.anirevo.model.calendar.DateManager;
import cvic.anirevo.utils.JSONUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Unparser {

    public static void info() {
        DateManager manager = DateManager.getInstance();
        JSONObject output = new JSONObject();
        output.put("year", manager.getYear());
        JSONArray dates = new JSONArray();
        for (CalendarDate date : manager) {
            dates.put(date.getName());
        }
        output.put("dates", dates);
        JSONUtils.writeJSON(DataPaths.JSON_INFO, output);
    }

}
