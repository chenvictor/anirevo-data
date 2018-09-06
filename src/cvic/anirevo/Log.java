package cvic.anirevo;

import javafx.scene.control.Alert;

public class Log {

    //Simulate android Log.i
    public static void i(String tag, String message) {
        System.out.println(String.format("[%s]: %s", tag, message));
    }

    public static void notify(String tag, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle(tag);
        alert.show();
    }

}
