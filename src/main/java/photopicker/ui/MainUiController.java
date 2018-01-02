package photopicker.ui;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import org.apache.commons.lang3.time.StopWatch;
import photopicker.config.PhotoPickerConfig;
import photopicker.copy.FileCopyManager;
import photopicker.imaging.ImageFile;
import photopicker.imaging.ImageLoader;
import photopicker.imaging.ImagingException;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainUiController implements Initializable, CopyTaskCreator {

    private static final KeyCombination KEY_NEXT_COMBINATION = new KeyCodeCombination(KeyCode.RIGHT);
    private static final KeyCombination KEY_PREV_COMBINATION = new KeyCodeCombination(KeyCode.LEFT);
    private static final KeyCombination KEY_COPY_COMBINATION = new KeyCodeCombination(KeyCode.C);

    private File targetDir;
    private int filesToCopy = 0;
    private int filesCopied = 0;
    private SimpleDoubleProperty filesToCopyProperty = new SimpleDoubleProperty(filesToCopy);
    private SimpleDoubleProperty filesCopiedProperty = new SimpleDoubleProperty(filesCopied);
    private FileCopyManager fileCopyWorker = new FileCopyManager(this);
    private ImageLoader imageLoader;
    private PhotoPickerConfig config;
    private StringProperty titleProperty;

    @FXML
    public TextField lbResizeY;

    @FXML
    public TextField lbResizeX;

    @FXML
    public Label lbProgress;

    @FXML
    public Label lbTarget;

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
        imageLoader.previous();
        updateImage();
    }

    public void nextAction(ActionEvent actionEvent) {
        next();
        actionEvent.consume();
    }

    private void next() {
        imageLoader.next();
        updateImage();
    }

    private void updateImage() {
        try {
            StopWatch watch = StopWatch.createStarted();
            ImageFile imageFile = imageLoader.current();
            Image image = SwingFXUtils.toFXImage(imageFile.getImage(), null);
            imageview.setImage(image);
            watch.stop();
            titleProperty.setValue(imageFile.getFile().getName() + " - PhotoPicker");
            System.out.println("Updating image in UI took " + watch.getTime());
        } catch (ImagingException e) {
            e.printStackTrace();
        }
    }

    public void outputAction(ActionEvent actionEvent) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Choose output directory");

        targetDir = dirChooser.showDialog(lbTarget.getScene().getWindow());
        String labelString = "-";
        if (targetDir != null) {
            labelString = targetDir.getAbsolutePath();
        }
        lbTarget.setText(labelString);

        actionEvent.consume();
    }

    public void sourceAction(ActionEvent event) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Set source directory");

        File sourceDir = dirChooser.showDialog(lbTarget.getScene().getWindow());
        String labelString = "-";
        if (sourceDir != null) {
            labelString = sourceDir.getAbsolutePath();

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

    private void copy() {
        if (targetDir == null) {
            System.out.println("OutputDirectory is null.");
            return;
        }
        fileCopyWorker.add(imageLoader.current());
        filesToCopy++;
        filesToCopyProperty.set(filesToCopy);
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
        return targetDir;
    }

    boolean readyForShutdown() {
        return fileCopyWorker.isReadyForShutdown();
    }

    void setTitleProperty(StringProperty titleProperty) {
        this.titleProperty = titleProperty;
    }

    public void setConfig(PhotoPickerConfig config) {
        this.config = config;
        targetDir = config.getTargetDir();
        File[] images = config.getSourceDir().listFiles(new ImageFileFilter());
        if (images == null || images.length < 1) {
            throw new IllegalArgumentException("Directory contains no images.");
        } else {
            imageLoader = new ImageLoader(images, config.getImageConfig());
            updateImage();
        }
        lbSource.setText(config.getSourceDir().getAbsolutePath());
        lbTarget.setText(config.getTargetDir().getAbsolutePath());
    }
}
