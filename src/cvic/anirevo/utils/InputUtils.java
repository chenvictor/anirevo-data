package cvic.anirevo.utils;

import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class InputUtils {

    /**
     *
     * @param defaultValue  Default value to show
     * @return              The title String inputted, or null if no response
     */
    public static String categoryTitleInput(String defaultValue) {
        return textInput("Category", "Enter title String", defaultValue);
    }

    /**
     * Basic text prompt dialog
     * @param title         Title to show
     * @param message       Message to show
     * @param defaultValue  Default value to show
     * @return              The String inputted, or null if no response
     */
    private static String textInput(String title, String message, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(message);
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

}
