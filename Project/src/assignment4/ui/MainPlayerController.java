package assignment4.ui;

import assignment4.model.Name;
import assignment4.model.Version;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class MainPlayerController {

    private Stage primaryStage;

    @FXML
    private TextField searchTextField;
    @FXML private Button selectAllButton;
    @FXML private TableView<Name> namesTable;
    @FXML private TableColumn<Name, CheckBox> selectColumn;
    @FXML private TableColumn<Name, String> nameColumn;
    @FXML private TableColumn<Name, String> playingColumn;

    @FXML private Button playButton;
    @FXML private Button badQualityButton;
    @FXML private Label currentLabel;
    @FXML private CheckBox shuffleCheckBox;

    @FXML private Button recordButton;
    @FXML private Button saveButton;
    @FXML private Button listenButton;
    @FXML private Button compareButton;
    @FXML private Button deleteButton;

    @FXML private Button mainMenuButton;
    @FXML private Button playlistButton;
    @FXML private Button nextButton;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void selectAllPressed() {

    }

    @FXML
    private void playPressed() {

    }

    @FXML
    private void badQualityPressed() {

    }

    @FXML
    private void recordPressed() {

    }

    @FXML
    private void savePressed() {

    }

    @FXML
    private void listenPressed() {

    }

    @FXML
    private void comparePressed() {

    }

    @FXML
    private void mainMenuPressed() {

        try {

            // Load the scene of the Main Menu fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/MainMenu.fxml"));
            Parent root = loader.load();

            MainMenuController controller = (MainMenuController) loader.getController();

            // Set the stage to use the current stage in order to switch scenes with the same stage
            controller.setPrimaryStage(primaryStage);

            primaryStage.setScene(new Scene(root));
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void playlistPressed() {

        try {

            // Load the scene of the Main Menu fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Playlist.fxml"));
            Parent root = loader.load();

            PlaylistController controller = (PlaylistController) loader.getController();

            // Set the stage to use the current stage in order to switch scenes with the same stage
            controller.setPrimaryStage(primaryStage);

            primaryStage.setScene(new Scene(root));
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void nextPressed() {

    }






}
