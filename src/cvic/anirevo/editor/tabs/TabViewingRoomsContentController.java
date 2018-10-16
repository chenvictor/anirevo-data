package cvic.anirevo.editor.tabs;

import cvic.anirevo.editor.TabInteractionHandler.ContentController;
import cvic.anirevo.model.ArShow;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.util.Timer;
import java.util.TimerTask;

public class TabViewingRoomsContentController extends ContentController {

    private ArShow mShow;

    @FXML
    private TextField textFieldShow;

    @FXML
    private ChoiceBox<Integer> choiceBoxAge;

    @FXML
    private TextArea textAreaDescription;

    private Timer timer;

    public void initialize() {
        timer = new Timer();
        sync();
        initChoiceBox();
        textFieldShow.textProperty().addListener((observable, oldValue, newValue) -> changed());
        choiceBoxAge.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> changed());
        textAreaDescription.textProperty().addListener((observable, oldValue, newValue) -> changed());
    }
    private void initChoiceBox() {
        choiceBoxAge.getItems().addAll(0, 13, 18);
        choiceBoxAge.getSelectionModel().select(0);
        choiceBoxAge.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> changed());
        choiceBoxAge.setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                if (object == 0) {
                    return "NONE";
                }
                return String.valueOf(object) + "+";
            }

            @Override
            public Integer fromString(String string) {
                switch (string) {
                    case "13+":
                        return 13;
                    case "18+":
                        return 18;
                    default:
                        return 0;
                }
            }
        });
    }
    private void saveShow() {
        if (mShow != null) {
            mShow.setTitle(textFieldShow.getText());
            mShow.setAge(choiceBoxAge.getSelectionModel().getSelectedItem());
            mShow.setDescription(textAreaDescription.getText());
        }
    }

    private void sync() {
        if (mShow != null) {
            setDisable(false);
            textFieldShow.setText(mShow.getTitle());
            choiceBoxAge.getSelectionModel().select(getIndex(mShow.getAge()));
            textAreaDescription.setText(mShow.getDescription());
        } else {
            setDisable(true);
            textFieldShow.setText("");
            textAreaDescription.setText("");
            choiceBoxAge.getSelectionModel().select(0);
        }
    }

    private void setDisable(boolean disable) {
        textFieldShow.setDisable(disable);
        choiceBoxAge.setDisable(disable);
        textAreaDescription.setDisable(disable);
    }

    private int getIndex(int age) {
        switch (age) {
            case 13: return 1;
            case 18: return 2;
            default: return 0;
        }
    }

    @FXML
    private void changed() {
        timer.purge();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                saveShow();
            }
        }, 500);
    }

    @Override
    public void itemSelected(Object show) {
        if (show == null || show instanceof ArShow) {
            mShow = (ArShow) show;
            sync();
        }
    }

}
