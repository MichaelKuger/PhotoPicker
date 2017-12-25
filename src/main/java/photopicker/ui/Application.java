package photopicker.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/mainui.fxml"));
            Parent root = loader.load();
            final MainUiController ctrl = loader.getController();
            ctrl.setTitleProperty(primaryStage.titleProperty());
            primaryStage.setTitle("PhotoPicker");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            primaryStage.setOnCloseRequest(event -> {
                if (!ctrl.readyForShutdown()) {
                    boolean close = showCloseDialog();
                    if (!close) {
                        event.consume();
                    }
                }
            });
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