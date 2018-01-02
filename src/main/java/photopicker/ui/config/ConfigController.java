package photopicker.ui.config;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import photopicker.config.ImageConfig;
import photopicker.config.PhotoPickerConfig;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ConfigController implements Initializable {

    @FXML
    public TextField lbSource;
    @FXML
    public TextField lbTarget;
    @FXML
    public TextField lbWidth;
    @FXML
    public TextField lbHeight;

    public void selectSource(ActionEvent event) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Choose image directory");

        File dir = dirChooser.showDialog(lbSource.getScene().getWindow());
        String labelString = "";
        if (dir != null) {
            labelString = dir.getAbsolutePath();
        }
        lbSource.setText(labelString);
        event.consume();
    }

    public void selectTarget(ActionEvent event) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Choose output directory");

        File dir = dirChooser.showDialog(lbTarget.getScene().getWindow());
        String labelString = "";
        if (dir != null) {
            labelString = dir.getAbsolutePath();
        }
        lbTarget.setText(labelString);
        event.consume();
    }

    public Optional<PhotoPickerConfig> getConfig() {
        try {
            String width = lbWidth.getText();
            String height = lbHeight.getText();
            String targetDir = lbTarget.getText();
            String sourceDir = lbSource.getText();

            if (stringEmpty(width) || stringEmpty(height) || stringEmpty(targetDir) || stringEmpty(sourceDir)) {
                return Optional.empty();
            }
            ImageConfig imageConfig = new ImageConfig(Double.parseDouble(height), Double.parseDouble(width));
            PhotoPickerConfig config = new PhotoPickerConfig();
            config.setImageConfig(imageConfig);
            config.setTargetDir(new File(targetDir));
            config.setSourceDir(new File(sourceDir));
            return Optional.of(config);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private boolean stringEmpty(String s) {
        if (s == null)
            return true;
        if (s.trim().isEmpty())
            return true;
        return false;
    }

    public void textChanged(ActionEvent event) {

    }

    public void saveConfig() {
        lbWidth.getScene().getWindow().hide();
    }

    public boolean showCloseConfigDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Close application?");
        alert.setHeaderText("Operation in progress");
        alert.setContentText("File copy operation in progress. Sure?");

        ButtonType buttonYes = new ButtonType("Yes, please close");
        ButtonType buttonNo = new ButtonType("No");

        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() != buttonNo;
    }

    private boolean isValidDir(String s) {
        try {
            File dir = new File(s);
            if (!dir.exists())
                return false;
            if (!dir.isDirectory())
                return false;
            return true;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
