package photopicker.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import photopicker.config.PhotoPickerConfig;
import photopicker.ui.config.ConfigController;

import java.io.IOException;
import java.util.Optional;

public class Application extends javafx.application.Application {

    private Stage primaryStage;
    private PhotoPickerConfig config;
    private MainUiController mainController;
    private ConfigController configController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        Stage mainUI = loadMainStage();
        Stage configUI = loadConfigStage(primaryStage);

        mainUI.show();
        configUI.showAndWait();
        System.out.println("closing config dialog");
        Optional<PhotoPickerConfig> configOpt = configController.getConfig();
        if (configOpt.isPresent()) {
            mainController.setConfig(configOpt.get());
        } else {
            System.out.println("No config received.");
        }
    }

    private Stage loadMainStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/mainui.fxml"));
        Parent root = loader.load();
        mainController = loader.getController();
        mainController.setTitleProperty(primaryStage.titleProperty());
        primaryStage.setTitle("PhotoPicker");
        primaryStage.setScene(new Scene(root));

        primaryStage.setOnCloseRequest(event -> {
            if (!mainController.readyForShutdown()) {
                boolean close = showCloseDialog();
                if (!close) {
                    event.consume();
                }
            }
        });
        return primaryStage;
    }

    private Stage loadConfigStage(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/configui.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();

        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(new Scene(root));
        stage.setTitle("Config");

        configController = loader.getController();

        stage.setOnCloseRequest(event -> {
            System.out.println("Close request for config ui");
            Optional<PhotoPickerConfig> configOpt = configController.getConfig();
            if (!configOpt.isPresent()) {
                if (configController.showCloseConfigDialog()) {
                    primaryStage.close();
                } else {
                    event.consume();
                }
            }
        });
        return stage;
    }

    private boolean showCloseDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Close application?");
        alert.setHeaderText("Operation in progress");
        alert.setContentText("File copy operation in progress. Sure?");

        ButtonType buttonYes = new ButtonType("Yes, please close");
        ButtonType buttonNo = new ButtonType("No");

        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() != buttonNo;
    }
}