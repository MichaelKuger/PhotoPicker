package photopicker.ui;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import photopicker.copy.FileCopyManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainUiController implements Initializable, CopyTaskCreator {

    private static final KeyCombination KEY_NEXT_COMBINATION = new KeyCodeCombination(KeyCode.RIGHT);
    private static final KeyCombination KEY_PREV_COMBINATION = new KeyCodeCombination(KeyCode.LEFT);
    private static final KeyCombination KEY_COPY_COMBINATION = new KeyCodeCombination(KeyCode.C);

    private int counter;
    private File[] imageFiles;
    private File outputDirectory;
    private int filesToCopy = 0;
    private int filesCopied = 0;
    private SimpleDoubleProperty filesToCopyProperty = new SimpleDoubleProperty(filesToCopy);
    private SimpleDoubleProperty filesCopiedProperty = new SimpleDoubleProperty(filesCopied);
    private FileCopyManager fileCopyWorker = new FileCopyManager(this);

    @FXML
    public Label lbProgress;

    @FXML
    public Label lbOutput;

    @FXML
    private ImageView imageview;

    @FXML
    private ProgressBar progressbar;

    @FXML
    private Pane imagepane;

    public void prevAction(ActionEvent actionEvent) {
        prev();
        actionEvent.consume();
    }

    private void prev() {
        counter--;
        if (counter < 0) {
            counter = imageFiles.length - 1;
        }
        updateImage();
    }

    public void nextAction(ActionEvent actionEvent) {
        next();
        actionEvent.consume();
    }

    private void next() {
        counter++;
        if (counter >= imageFiles.length) {
            counter = 0;
        }
        updateImage();
    }

    private void updateImage() {
        try {
            Image image = new Image(new FileInputStream(imageFiles[counter]));
            imageview.setImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void outputAction(ActionEvent actionEvent) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Choose output directory");

        outputDirectory = dirChooser.showDialog(lbOutput.getScene().getWindow());
        String labelString = "-";
        if (outputDirectory != null) {
            labelString = outputDirectory.getAbsolutePath();
        }
        lbOutput.setText(labelString);

        actionEvent.consume();
    }

    public void init(File directory) {
        imageFiles = directory.listFiles(new ImageFileFilter());
        if (imageFiles == null || imageFiles.length < 1) {
            throw new IllegalArgumentException("Directory contains no images.");
        } else {
            updateImage();
        }
        System.out.println("set");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("init");
        StringExpression progressLabelExpression = Bindings.concat(filesCopiedProperty, new SimpleStringProperty(" / "), filesToCopyProperty);
        lbProgress.textProperty().bind(progressLabelExpression);
        NumberBinding progressBinding = filesCopiedProperty.divide(filesToCopyProperty);
        progressbar.progressProperty().bind(progressBinding);
        imageview.fitWidthProperty().bind(imagepane.widthProperty());
        imageview.fitHeightProperty().bind(imagepane.heightProperty());
    }

    public void copyAction(ActionEvent actionEvent) {
        copy();
        actionEvent.consume();
    }

    public void copy() {
        if (outputDirectory == null) {
            System.out.println("OutputDirectory is null.");
            return;
        }
        filesToCopy++;
        filesToCopyProperty.set(filesToCopy);
        fileCopyWorker.add(imageFiles[counter]);
    }

    public void fileCopyFinished(File file) {
        filesCopied++;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                filesCopiedProperty.set(filesCopied);
                System.out.println(progressbar.progressProperty().getValue());
            }
        });
    }

    public void keyboardEvent(KeyEvent e) {
        if (KEY_NEXT_COMBINATION.match(e)) {
            next();
            e.consume();
        } else if (KEY_PREV_COMBINATION.match(e)) {
            prev();
            e.consume();
        } else if (KEY_COPY_COMBINATION.match(e)) {
            copy();
            e.consume();
        }
    }

    @Override
    public File getTarget() {
        return outputDirectory;
    }

    public boolean readyForShutdown() {
       return fileCopyWorker.isReadyForShutdown();
    }
}
