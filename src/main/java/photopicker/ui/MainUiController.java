package photopicker.ui;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import photopicker.imaging.ImageLoader;
import photopicker.imaging.ImagingException;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainUiController implements Initializable, CopyTaskCreator {

    private static final KeyCombination KEY_NEXT_COMBINATION = new KeyCodeCombination(KeyCode.RIGHT);
    private static final KeyCombination KEY_PREV_COMBINATION = new KeyCodeCombination(KeyCode.LEFT);
    private static final KeyCombination KEY_COPY_COMBINATION = new KeyCodeCombination(KeyCode.C);

    private int counter;
    private File outputDirectory;
    private int filesToCopy = 0;
    private int filesCopied = 0;
    private SimpleDoubleProperty filesToCopyProperty = new SimpleDoubleProperty(filesToCopy);
    private SimpleDoubleProperty filesCopiedProperty = new SimpleDoubleProperty(filesCopied);
    private FileCopyManager fileCopyWorker = new FileCopyManager(this);
    private ImageLoader imageLoader;

    @FXML
    public Label lbProgress;

    @FXML
    public Label lbOutput;

    @FXML
    public Label lbSource;

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
            counter = imageLoader.size() - 1;
        }
        updateImage();
    }

    public void nextAction(ActionEvent actionEvent) {
        next();
        actionEvent.consume();
    }

    private void next() {
        counter++;
        if (counter >= imageLoader.size()) {
            counter = 0;
        }
        updateImage();
    }

    private void updateImage() {
        try {
            Image image = SwingFXUtils.toFXImage(imageLoader.imageAt(counter).getImage(), null);
            imageview.setImage(image);
        } catch (ImagingException e) {
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

    public void sourceAction(ActionEvent event) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Set source directory");

        File sourceDir = dirChooser.showDialog(lbOutput.getScene().getWindow());
        String labelString = "-";
        if (sourceDir != null) {
            labelString = sourceDir.getAbsolutePath();
            File[] images = sourceDir.listFiles(new ImageFileFilter());
            if (images == null || images.length < 1) {
                throw new IllegalArgumentException("Directory contains no images.");
            } else {
                imageLoader = new ImageLoader(images);
                updateImage();
            }
            System.out.println("set");
        }
        lbSource.setText(labelString);

        event.consume();
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
        try {
            if (outputDirectory == null) {
                System.out.println("OutputDirectory is null.");
                return;
            }
            fileCopyWorker.add(imageLoader.imageAt(counter));
            filesToCopy++;
            filesToCopyProperty.set(filesToCopy);
        } catch (ImagingException e) {
            e.printStackTrace();
        }
    }

    public void fileCopyFinished(File file) {
        filesCopied++;
        Platform.runLater(() -> {
            filesCopiedProperty.set(filesCopied);
            System.out.println(progressbar.progressProperty().getValue());
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
