package assignment4.ui;

import java.io.File;

import assignment4.model.Name;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class NamesDatabaseController extends BaseController {
	private static final File TEMP_RECORDING = new File(ROOT_DIR + "temp/new.wav");

	//@formatter:off
	@FXML private Label currentLabel;
    @FXML private TextField searchTextField;
    @FXML private ListView<Name> namesList;
    @FXML private TextField textInput;
    @FXML private Button mainMenuButton;
    @FXML private Button playButton;
    @FXML private Button saveButton;
    @FXML private Button listenButton;
    @FXML private Button deleteButton;
    @FXML private Button recordButton;
    @FXML private Button helpButton;
    //@formatter:on

	private Name current;
	private RecordTask recordTask;

	private boolean validName, hasRecording;
	
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

		namesList.getSelectionModel().selectedItemProperty().addListener((observer, oldVal, newVal) -> {
			switchCurrent(newVal);
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
		namesList.setItems(FXCollections.observableArrayList(namesDB.getAllNames()));

		textInput.textProperty().addListener((observable, oldVal, newVal) -> {
			validName = newVal.length() >= 2 && namesDB.getName(newVal) == null;
			updateSaveButton();
		});

	}


	private void switchCurrent(Name newVal) {
		current = newVal;
		if (current != null) {
			currentLabel.setText(current.getName());
			deleteButton.setDisable(false);
		} else {
			currentLabel.setText("Names Database");
			deleteButton.setDisable(true);
		}
	}

	@FXML
	private void mainMenuPressed() {
		showScene("MainMenu.fxml", false, false);
	}

	@FXML
	private void recordPressed() {
		if (recordButton.getText().equals("Record")) {
			recordButton.setText("Stop");

			listenButton.setDisable(true);
			saveButton.setDisable(true);

			recordTask = new RecordTask(TEMP_RECORDING, () -> {
				Platform.runLater(() -> {
					recordButton.setText("Record");
					listenButton.setDisable(false);
					hasRecording = true;
					updateSaveButton();
				});
			});
			recordTask.start();
		} else {
			recordTask.stop();
		}
	}

	private void updateSaveButton() {
		saveButton.setDisable(!hasRecording || !validName);
	}


	/**
	 * Saves the temporary attempt and adds it to the table
	 */
	@FXML
	private void savePressed() {
		String name = textInput.getText().substring(0, 1).toUpperCase() + textInput.getText().substring(1);
		File saved = new File(ROOT_DIR + "names/" + name + ".wav");
		TEMP_RECORDING.renameTo(saved);

		namesDB.addName(new Name(name).addVersion(saved.getName(), false));
		namesList.setItems(FXCollections.observableArrayList(namesDB.getAllNames()));
		namesList.refresh();
		
		saveButton.setDisable(true);
		listenButton.setDisable(true);
		validName = false;
		hasRecording = false;
		textInput.clear();
		TEMP_RECORDING.delete();
		
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
		// Select first attempt if nothing is selected
		if (current == null) {
			namesList.getSelectionModel().clearAndSelect(0);
			current = namesList.getSelectionModel().getSelectedItem();
			deleteButton.setDisable(false);
		}

		// Play the pronunciation from the database
		MediaPlayer mp = current.getBestVersion().getMediaPlayer();
		mp.setAutoPlay(true);
	}

	@FXML
	private void listenPressed() {
		String URI = TEMP_RECORDING.toURI().toString();
		MediaPlayer mp = new MediaPlayer(new Media(URI));
		mp.setAutoPlay(true);
	}

	@FXML
	private void deletePressed() {
		current = namesList.getSelectionModel().getSelectedItem();
		if (current != null) {

			// Provide a confirmation window to check if the user is sure of deleting the file
			Alert confirmation = new Alert(AlertType.WARNING,
					"Delete \"" + current.getName() + "\" from the Database?",
					ButtonType.CANCEL, ButtonType.OK);
			confirmation.showAndWait();

			// Only remove the file if the OK Button is pressed
			if (confirmation.getResult() == ButtonType.OK) {
				namesDB.deleteName(current);
				namesList.getSelectionModel().clearSelection();
				namesList.setItems(FXCollections.observableArrayList(namesDB.getAllNames()));
			}
		}
	}

}
