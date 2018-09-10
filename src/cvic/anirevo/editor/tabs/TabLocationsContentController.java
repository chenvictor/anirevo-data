package cvic.anirevo.editor.tabs;

import cvic.anirevo.editor.TabInteractionHandler.ContentController;
import cvic.anirevo.model.anirevo.ArLocation;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.util.Timer;
import java.util.TimerTask;

public class TabLocationsContentController extends ContentController {

    private ArLocation mLocation;

    @FXML
    private TextField textFieldPurpose, textFieldLocation;

    @FXML
    private CheckBox checkBoxSchedule;

    private Timer timer;

    public void initialize() {
        timer = new Timer();
        sync();
        textFieldLocation.textProperty().addListener((observable, oldValue, newValue) -> {
            changed();
        });
        textFieldPurpose.textProperty().addListener((observable, oldValue, newValue) -> {
            changed();
        });
    }

    private void saveLocation() {
        if (mLocation != null) {
            mLocation.setLocation(textFieldLocation.getText());
            mLocation.setSchedule(checkBoxSchedule.isSelected());
            mLocation.setPurpose(textFieldPurpose.getText());
        }
    }

    private void sync() {
        if (mLocation != null) {
            textFieldPurpose.setText(mLocation.getPurpose());
            textFieldLocation.setText(mLocation.getLocation());
            checkBoxSchedule.setSelected(mLocation.isSchedule());
        }
    }

    @FXML
    private void changed() {
        timer.purge();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                saveLocation();
            }
        }, 500);
    }

    @Override
    public void itemSelected(Object location) {
        if (location == null || location instanceof ArLocation) {
            mLocation = (ArLocation) location;
            sync();
        }
    }

}
