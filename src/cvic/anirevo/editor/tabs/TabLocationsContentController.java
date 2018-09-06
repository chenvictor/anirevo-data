package cvic.anirevo.editor.tabs;

import cvic.anirevo.model.anirevo.ArLocation;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class TabLocationsContentController implements TabLocationsNavController.LocationsNavListener {

    private ArLocation mLocation;

    @FXML
    private TextField textFieldPurpose, textFieldLocation;

    @FXML
    private CheckBox checkBoxSchedule;

    public void initialize() {
        sync();
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
    }

    @Override
    public void itemSelected(ArLocation location) {
        save(); //save the previous item
        mLocation = location;
        sync();
    }

    @Override
    public void save() {
        saveLocation();
    }
}
