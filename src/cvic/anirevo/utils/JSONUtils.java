package cvic.anirevo.utils;

import cvic.anirevo.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public class JSONUtils {

    private static final int INDENT = 2;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String getString(String path) throws FileNotFoundException{
        StringBuilder json = new StringBuilder();
        String line;
        if (!(new File(path).exists())) {
            throw new FileNotFoundException();
        }
        try {
            FileReader reader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(reader);

            while ((line = bufferedReader.readLine()) != null) {
                json.append(line).append("\n");
            }
            bufferedReader.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public static JSONObject getObject(String path) throws JSONException, FileNotFoundException{
        return new JSONObject(JSONUtils.getString(path));
    }

    public static JSONArray getArray(String path) throws JSONException, FileNotFoundException {
        return new JSONArray(JSONUtils.getString(path));
    }

    public static void writeJSON(String path, JSONObject object) {
        writeFile(path, object.toString(INDENT));
    }

    public static void writeJSON(String path, JSONArray array) {
        writeFile(path, array.toString(INDENT));
    }

    private static void writeFile(String path, String contents) {
        File file = new File(path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        try {
            FileWriter writer = new FileWriter(path);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            bufferedWriter.write(contents);
            bufferedWriter.close();
            writer.close();
            Log.notify("JSON Utils", "Wrote to file: " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
