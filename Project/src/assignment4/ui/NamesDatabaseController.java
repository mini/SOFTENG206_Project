package assignment4.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import assignment4.model.Combination;
import assignment4.model.Name;
import assignment4.model.Version;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.util.Duration;

import static assignment4.NameSayerApp.ROOT_DIR;

public class NamesDatabaseController extends BaseController {
    private static final SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

    private FileChooser fileChooser = new FileChooser();
    private File lastSelected;
    //@formatter:off
    @FXML private TextField searchTextField;
    @FXML private ListView<Name> namesList;
    @FXML private TextField textInput;
    @FXML private Button mainMenuButton;
    @FXML private Button playButton;
    @FXML private Button saveButton;
    @FXML private Button listenButton;
    @FXML private Button deleteButton;

    @FXML private Button helpButton;

    private Name current;
    private String addName;
    //@formatter:on

    @Override
    public void init() {

        Tooltip tooltip = new Tooltip();
        tooltip.setText("Names Database:  \n\n" +
                "* Select a name from the database on the left to LISTEN or DELETE. \n\n" +
                "* To add a new name to the database: \n" +
                "\t* Type the name being added and click RECORD to start the recording.\n" +
                "\t* Click STOP to end the recording.\n" +
                "\t* Click LISTEN to hear your new recording.\n" +
                "\t* If unsatisfactory, click RECORD to overwrite your recording.\n" +
                "\t* Otherwise, click SAVE to add the new recording into the database for practice.\n\n" +
                "* Click the MAIN MENU button to go back to the main screen.");
        helpButton.setTooltip(tooltip);

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            namesList.setItems(FXCollections.observableArrayList(namesDB.getNames(newValue)));
        });


        namesList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Name selected = namesList.getSelectionModel().getSelectedItem();
            }
        });

        namesList.setCellFactory(value -> new ListCell<Name>() {
            @Override
            protected void updateItem(Name item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
        namesList.getItems().addAll(namesDB.getAllNames());
    }

    @FXML
    private void mainMenuPressed() {
        showScene("/resources/MainMenu.fxml", false, false);
    }

    /**
     * Starts recording
     */
    @FXML
    private void recordPressed() {



        // Define temp file for this recording
        File tempFile = new File(ROOT_DIR + "names/temp.wav");
        tempFile.deleteOnExit();

        RecordTask recordTask = new RecordTask(tempFile, () -> {
            // Can enable these since we have something to play
            saveButton.setDisable(false);
            listenButton.setDisable(false);

            // Label temp file as an Unsaved Attempt in the list
            current.addAttempt(tempFile, "Unsaved Attempt");
        });

        recordTask.start();

        RewardsController.records++;
    }

    /**
     * Saves the temporary attempt and adds it to the table
     */
    @FXML
    private void savePressed() {
        saveButton.setDisable(true);

        // Obtain user input
        addName = textInput.getText();

        // Rename file from temp.wav to the corresponding name with the timestamp
        String nameFolder = ROOT_DIR + "names/";
        new File(nameFolder + "temp.wav").renameTo(new File(nameFolder + "sec1_sec2_" + addName + ".wav"));

        // Remove the temporary file (temp.wav)
        current.removeTemp();

        RewardsController.saves++;
    }

    /**
     * Plays the selected attempt, if nothing is selected it will play the first one on the list (will play the temporary
     * attempt if it exists).
     *
     * @return attemptPlayer the attempt's media player
     */
    @FXML
    private void playPressed() {
        Name current =  namesList.getSelectionModel().getSelectedItem();

        // Select first attempt if nothing is selected
        if (current == null) {
            namesList.getSelectionModel().clearAndSelect(0);
            current = (Name) namesList.getSelectionModel().getSelectedItem();
        }

        // Play the pronunciation from the database
        MediaPlayer mp = current.getBestVersion().getMediaPlayer();
        mp.setAutoPlay(true);

    }

    @FXML
    private void listenPressed() {

        File tempFile = new File(ROOT_DIR + "names/temp.wav");

        String URI = tempFile.toURI().toString();

        MediaPlayer mp = new MediaPlayer(new Media(URI));
        mp.setAutoPlay(true);
    }

    @FXML
    private void deletePressed() {



        Name current =  namesList.getSelectionModel().getSelectedItem();

        if (current != null) {

            // Provide a confirmation window to check if the user is sure of deleting the file
            Alert confirmation = new Alert(AlertType.WARNING,
                    "Delete " + current.getName() + " from the Database?",
                    ButtonType.CANCEL, ButtonType.OK);
            confirmation.showAndWait();

            // Only remove the file if the OK Button is pressed
            if (confirmation.getResult() == ButtonType.OK) {
                current.getLastVersion().deleteFile();
                namesList.getSelectionModel().clearSelection();
            }
        }

    }


}
