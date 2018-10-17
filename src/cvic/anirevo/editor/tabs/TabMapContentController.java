package cvic.anirevo.editor.tabs;

import cvic.anirevo.model.MapVenue;
import cvic.anirevo.editor.DataPaths;
import cvic.anirevo.editor.TabInteractionHandler.ContentController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class TabMapContentController extends ContentController {

    @FXML
    private TextField textFieldName;

    @FXML
    private ImageView imageView;

    @FXML
    private Button buttonChangeImage;

    private MapVenue mVenue;

    private Timer timer;

    private FileChooser fileChooser;

    public void initialize() {
        timer = new Timer();
        sync();
        textFieldName.textProperty().addListener((observable, oldValue, newValue) -> changed());
        fileChooser = new FileChooser();
        fileChooser.setTitle("Select a Venue Image");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));
        fileChooser.setInitialDirectory(new File(DataPaths.IMAGES));
    }

    @FXML
    private void changeImage() {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            File baseDirectory = new File(DataPaths.IMAGES);
            if (!file.getParentFile().getAbsolutePath().equals(baseDirectory.getAbsolutePath())) {
                System.out.println("Copy file over");
                //file must be copied over
                try {
                    Files.copy(file.toPath(), baseDirectory.toPath().resolve(file.getName()), REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mVenue.setImagePath(file.getName());
            sync();
        }
    }

    private void save() {
        if (mVenue != null) {
            mVenue.setName(textFieldName.getText());
        }
    }

    private void sync() {
        if (mVenue != null) {
            setDisable(false);
            textFieldName.setText(mVenue.getName());
            if (mVenue.getImagePath() != null && mVenue.getImagePath().length() != 0) {
                try {
                    File file = new File(DataPaths.IMAGES + mVenue.getImagePath());
                    Image image = new Image(file.toURI().toString());
                    imageView.setImage(image);
                } catch (Exception e) {
                    //something wrong with portrait path, remove it
                    mVenue.setImagePath(null);
                }
            } else {
                imageView.setImage(null);
            }
        } else {
            setDisable(true);
            textFieldName.setText("");
            imageView.setImage(null);
        }
    }

    private void setDisable(boolean disable) {
        textFieldName.setDisable(disable);
        imageView.setDisable(disable);
        buttonChangeImage.setDisable(disable);
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
    public void itemSelected(Object mapVenue) {
        if (mapVenue == null || mapVenue instanceof MapVenue) {
            mVenue = (MapVenue) mapVenue;
            sync();
        }
    }
}
