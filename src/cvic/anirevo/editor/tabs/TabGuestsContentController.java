package cvic.anirevo.editor.tabs;

import cvic.anirevo.editor.TabInteractionHandler.ContentController;
import cvic.anirevo.model.anirevo.ArGuest;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Timer;
import java.util.TimerTask;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class TabGuestsContentController extends ContentController {

    private static final String BASE_PORTRAIT_PATH = "data/images/";

    @FXML
    private TextField textFieldName, textFieldTitle, textFieldJapanese;

    @FXML
    private ImageView imagePortrait;

    private ArGuest mGuest;

    private Timer timer;

    private FileChooser fileChooser;

    public void initialize() {
        timer = new Timer();
        sync();
        textFieldName.textProperty().addListener((observable, oldValue, newValue) -> {
            changed();
        });
        textFieldTitle.textProperty().addListener((observable, oldValue, newValue) -> {
            changed();
        });
        textFieldJapanese.textProperty().addListener((observable, oldValue, newValue) -> {
            changed();
        });
        fileChooser = new FileChooser();
        fileChooser.setTitle("Select a Guest Portrait");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));
        fileChooser.setInitialDirectory(new File(BASE_PORTRAIT_PATH));
    }

    @FXML
    private void changeImage() {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            File baseDirectory = new File(BASE_PORTRAIT_PATH);
            if (!file.getParentFile().getAbsolutePath().equals(baseDirectory.getAbsolutePath())) {
                System.out.println("Copy file over");
                //file must be copied over
                try {
                    Files.copy(file.toPath(), baseDirectory.toPath().resolve(file.getName()), REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mGuest.setPortraitPath(file.getName());
            sync();
        }
    }

    private void save() {
        if (mGuest != null) {
            mGuest.setName(textFieldName.getText());
            mGuest.setTitle(textFieldTitle.getText());
            mGuest.setJapanese(textFieldJapanese.getText());
        }
    }

    private void sync() {
        if (mGuest != null) {
            textFieldName.setText(mGuest.getName());
            textFieldTitle.setText(mGuest.getTitle());
            textFieldJapanese.setText(mGuest.getJapanese());
            if (mGuest.getPortraitPath() != null && mGuest.getPortraitPath().length() != 0) {
                try {
                    File file = new File(BASE_PORTRAIT_PATH + mGuest.getPortraitPath());
                    Image image = new Image(file.toURI().toString());
                    imagePortrait.setImage(image);
                } catch (Exception e) {
                    //something wrong with portrait path, remove it
                    mGuest.setPortraitPath(null);
                }
            } else {
                imagePortrait.setImage(null);
            }
        }
    }

    private void changed() {
        timer.purge();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                save();
            }
        }, 500);
    }

    @Override
    public void itemSelected(Object guest) {
        if (guest == null || guest instanceof ArGuest) {
            mGuest = (ArGuest) guest;
            sync();
        }
    }
}
