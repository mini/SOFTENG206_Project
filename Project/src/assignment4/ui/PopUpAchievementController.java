package assignment4.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PopUpAchievementController extends BaseController {

    @FXML
    Button closeButton;

    @FXML
    private void closePressed() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }



}
