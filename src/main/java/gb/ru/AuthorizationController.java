package gb.ru;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AuthorizationController {

    @FXML
    public Button move;

    @FXML
    public void copyFile(ActionEvent actionEvent) {
        //логика работы
    }

    public void clickToClose(ActionEvent actionEvent) {
        Platform.runLater(() -> {
           Stage stage =(Stage) move.getScene().getWindow();
           stage.close();
        });
    }


}
