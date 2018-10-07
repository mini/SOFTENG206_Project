package assignment4.ui;

import static assignment4.NameSayerApp.ROOT_DIR;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import assignment4.model.Name;
import assignment4.model.Version;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * -- PractiseController Class --
 *
 * PractiseController acts as the controller for the Player GUI, where events are listened and handled appropriately
 * with this class. It ensures that the numerous buttons link to its corresponding event handler methods as listed
 * below. This is the main screen where the majority of functionality of the application is handled, where each user
 * event is handled accordingly.
 *
 * The names database is populated into a list, and after user selection of any number of names to be practiced, the
 * attempts of each selected name is also populated. The class allows the user to play the pronunciation, mark any file
 * as bad quality if necessary, record, listen and delete their own recordings and attempts. Furthermore, the controller
 * also allows the comparison of their own recording as well as the original pronunciation.
 *
 */
public class PractiseController extends BaseController {
	private static final SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	private static final SimpleDateFormat labelDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	//@formatter:off
	@FXML private TextField searchTextField;
	@FXML private Button selectAllButton;
	@FXML private TableView<Name> namesTable;
	@FXML private TableColumn<Name, CheckBox> selectColumn;
	@FXML private TableColumn<Name, String> nameColumn;
	@FXML private TableColumn<Name, String> playingColumn;
	
	@FXML private Button playButton;
	@FXML private Button badQualityButton;
	@FXML private Label currentLabel;
	@FXML private CheckBox shuffleCheckBox;
	
	@FXML private TableView<Version> attemptsTable;
	@FXML private TableColumn<Version, String> attemptsColumn;

	@FXML private Button recordButton;
	@FXML private Button saveButton;
	@FXML private Button listenButton;
	@FXML private Button compareButton;
	@FXML private Button deleteButton;
	@FXML private Button helpButton;
	
	@FXML private Button mainMenuButton;
	@FXML private Button nextButton;
	//@formatter:on

	private Name current;
	private int numSelected = 0;
	private RecordTask recordTask;

	/**
	 * Sets up the left names table and the right attempts table to ensure that all names from the database are included, as
	 * well as configuring listeners for user selection and access.
	 */
	@Override
	public void init() {

		Tooltip tooltip = new Tooltip();
		tooltip.setText("Practice Module: \n\n" +
				"* Select name(s) to practise and click play. \n" +
				"* The screen will iterate through your selection one by one, where you can go to the next name by clicking NEXT. \n" +
				"* The names database is shown on the left, while your attempts of each name are shown on the right list \n" +
				"* For each name: \n" +
				"-- PLAY to listen to the pronunciation from the database \n" +
				"-- BAD QUALITY to mark the recording as bad quality \n" +
				"-- RECORD to record your own pronunciation (up to 5 seconds) \n" +
				"-- SAVE to permanently save your latest attempt \n" +
				"-- LISTEN to select and listen to an audio recording of your selected attempt \n" +
				"-- DELETE to delete your selected recording \n" +
				"-- COMPARE to subsequently play your attempt with the database pronunciation straight after \n" +
				"* Click the MAIN MENU button to go back");
		helpButton.setTooltip(tooltip);

		// Left names table

		// Refresh visible names as search string changes
		searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			namesTable.setItems(FXCollections.observableArrayList(namesDB.getNames(newValue)));
		});

		nameColumn.setCellValueFactory(new PropertyValueFactory<Name, String>("name"));

		selectColumn.setCellValueFactory(arg0 -> {
			Name name = ((TableColumn.CellDataFeatures<Name, CheckBox>) arg0).getValue();
			CheckBox checkBox = new CheckBox();
			checkBox.selectedProperty().setValue(name.isSelected());

			// Add listeners to the list
			checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
				name.setSelected(newValue);

				if (numSelected == 0 && newValue) {
					getNext();
				}

				if (numSelected >= 1 && !newValue) {
					getNext();
				}

				numSelected = namesDB.getNumSelected();

				selectAllButton.setText(numSelected == 0 ? "Select All" : "Deselect All");

				// Automatically switch to the first one selected
				if (current == null && numSelected == 1) {
					getNext();
				}

				// Disable if there are no new names to switch to
				nextButton.setDisable(numSelected == 0 || (current.isSelected() && numSelected == 1));
			});

			return new SimpleObjectProperty<CheckBox>(checkBox);
		});

		playingColumn.setCellValueFactory(arg0 -> {
			Name name = ((TableColumn.CellDataFeatures<Name, String>) arg0).getValue();
			return name.getPlayingProperty();
		});

		// Populates the left table with all the names from the database with original files
		namesTable.widthProperty().addListener(new HideHeader(namesTable));
		namesTable.setStyle("-fx-table-cell-border-color: transparent;");
		namesTable.setSelectionModel(null);
		namesTable.getItems().addAll(namesDB.getAllNames());

		// Right attempts table

		attemptsColumn.setCellValueFactory(new PropertyValueFactory<Version, String>("label"));

		// Add listeners to the list
		attemptsTable.widthProperty().addListener(new HideHeader(attemptsTable));
		attemptsTable.setStyle("-fx-table-cell-border-color: transparent;");

		// Only enable the delete button if something is selected
		attemptsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			deleteButton.setDisable(newValue == null);
		});
	}

	/**
	 * Hides the header of provided tables
	 */
	private class HideHeader implements ChangeListener<Number> {
		private TableView<? extends Object> table;

		private HideHeader(TableView<? extends Object> table) {
			this.table = table;
		}

		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			Pane header = (Pane) table.lookup("TableHeaderRow");
			System.out.println(header);
			// Hide the headers of the tables
			if (header != null && header.isVisible()) {
				header.setMaxHeight(0);
				header.setMinHeight(0);
				header.setPrefHeight(0);
				header.setVisible(false);
				header.setManaged(false);
			}
		}
	}

	/**
	 * Plays the currently selected name
	 */
	@FXML
	private void playPressed() {
		// Play the pronunciation from the database
		MediaPlayer mp = current.getBestVersion().getMediaPlayer();
		badQualityButton.setText(current.getLastVersion().isBadQuality() ? "Good Quality" : "Bad Quality");
		mp.setAutoPlay(true);
	}

	/**
	 * Switches to the next name
	 */
	private void getNext() {
		Name old = current;

		// If shuffle is selected, the next name will be chosen randomly
		if (shuffleCheckBox.isSelected()) {
			current = namesDB.getRandSelected();
		} else {
			// Otherwise, the next name on the list from the original listview is selected
			current = namesDB.getNextSelected(current);
		}

		if (current == null && old != null) {
			current = old;
		}

		// Configure buttons
		boolean noAttempts = current.getAttempts().isEmpty();
		currentLabel.setText("Name: " + current.getName());
		nextButton.setDisable(numSelected == 1);
		listenButton.setDisable(noAttempts);
		compareButton.setDisable(noAttempts);
		attemptsTable.setItems(current.getAttempts());
		recordButton.setDisable(false);
		playButton.setDisable(false);
		saveButton.setDisable(true);
		badQualityButton.setText(current.getLastVersion().isBadQuality() ? "Good Quality" : "Bad Quality");
		badQualityButton.setDisable(false);

		// Move the "current" icon on the list
		if (old != null) {
			old.getPlayingProperty().set(" ");
		}
		current.getPlayingProperty().set("  ◀");

	}

	/**
	 * Switches scene back to the main menu
	 */
	@FXML
	private void mainMenuPressed() {
		namesDB.setSelectedAll(false);
		if(current != null) {
			current.getPlayingProperty().set(" ");
		}
		showScene("/resources/MainMenu.fxml", false, false);
	}

	/**
	 * Changes to the next name that is being practiced
	 * 
	 * @see #getNext()
	 */
	@FXML
	private void nextPressed() {
		getNext();
	}

	/**
	 * Marks the current version as bad quality
	 */
	@FXML
	private void badQualityPressed() {
		current.getLastVersion().toggleBadQuality();
		badQualityButton.setText(current.getLastVersion().isBadQuality() ? "Good Quality" : "Bad Quality");
	}

	/**
	 * Selects and de-selects all names. If there are any selected it will deselect all first. If none are selected then it
	 * will select all.
	 */
	@FXML
	private void selectAllPressed() {
		ArrayList<Name> names = namesDB.getAllNames();
		boolean next = numSelected == 0;

		namesDB.setSelectedAll(next);

		// Configure button to switch from 'Select All' to 'Deselect All'
		if (next) {
			selectAllButton.setText("Deselect All");
			if (numSelected == 0) { // If first thing user does, then switch to first name
				getNext();
			}
		} else {
			selectAllButton.setText("Select All");
		}

		nextButton.setDisable(!next);
		numSelected = next ? names.size() : 0;
		namesTable.refresh();
	}

	/**
	 * Starts recording
	 */
	@FXML
	private void recordPressed() {

		if (recordButton.getText().equals("Record")) {
			// Remove any temporary files that are unsaved before starting
			current.removeTemp();

			// Define temp file for this recording
			File tempFile = new File(ROOT_DIR + "attempts/" + current.getName() + "/temp.wav");
			tempFile.deleteOnExit();

			recordButton.setText("Stop");


			recordTask = new RecordTask(tempFile, () -> {
				Platform.runLater(() -> {
					recordButton.setText("Record");

					// Can enable these since we have something to play
					saveButton.setDisable(false);
					listenButton.setDisable(false);
					compareButton.setDisable(false);

					// Label temp file as an Unsaved Attempt in the list
					current.addAttempt(tempFile, "Unsaved Attempt");
					attemptsTable.getSelectionModel().clearAndSelect(0);
				});
			});

			recordTask.start();
		} else {
			recordTask.stop();
		}

	}

	/**
	 * Saves the temporary attempt and adds it to the table
	 */
	@FXML
	private void savePressed() {
		saveButton.setDisable(true);

		// Getting new filename with the timestamp of when the user clicks save
		Date now = new Date();
		String timestamp = fileDateFormat.format(now);
		String filename = timestamp + "-" + current.getName() + ".wav";

		// Rename file from temp.wav to the corresponding name with the timestamp
		String nameFolder = ROOT_DIR + "attempts/" + current.getName() + "/";
		new File(nameFolder + "temp.wav").renameTo(new File(nameFolder + filename));

		// Remove the temporary file (temp.wav)
		current.removeTemp();

		// Save the new filename to the list
		current.addAttempt(new File(ROOT_DIR + "attempts/" + current.getName() + "/" + filename), labelDateFormat.format(now));

		RewardsController.saves++;
	}

	/**
	 * Plays the selected attempt, if nothing is selected it will play the first one on the list (will play the temporary
	 * attempt if it exists).
	 * 
	 * @return attemptPlayer the attempt's media player
	 */
	@FXML
	private MediaPlayer listenPressed() {
		final MediaPlayer player;
		Version attempt = attemptsTable.getSelectionModel().getSelectedItem();

		// Select first attempt if nothing is selected
		if (attempt == null) {
			attemptsTable.getSelectionModel().clearAndSelect(0);
			attempt = (Version) attemptsTable.getSelectionModel().getSelectedItem();
		}

		player = attempt.getMediaPlayer();
		player.seek(new Duration(-1.0));
		player.play();

		return player; // Used for #comparePressed
	}

	/**
	 * Plays the selected attempt and then the current name by utilising previous methods
	 * 
	 * @see #listenPressed()
	 * @see #playPressed()
	 */
	@FXML
	private void comparePressed() {
		// Calls the listenPressed() and playPressed() methods to first play the selected user recording
		MediaPlayer attemptPlayer = listenPressed();
		// At the end of the file, the file in the database is played afterwards
		attemptPlayer.setOnEndOfMedia(() -> {
			MediaPlayer mp = current.getLastVersion().getMediaPlayer();
			mp.setOnReady(() -> {
				playPressed();
			});

			attemptPlayer.setOnEndOfMedia(null);
		});
	}

	/**
	 * Deletes the selected attempt, will confirm first
	 */
	@FXML
	private void deletePressed() {
		// Obtain selection from the list
		Version attempt = (Version) attemptsTable.getSelectionModel().getSelectedItem();
		if (attempt != null) {

			// Provide a confirmation window to check if the user is sure of deleting the file
			Alert confirmation = new Alert(AlertType.WARNING,
					"Delete " + attempt.getLabel() + " from " + current.getName(),
					ButtonType.CANCEL, ButtonType.OK);
			confirmation.showAndWait();

			// Only remove the file if the OK Button is pressed
			if (confirmation.getResult() == ButtonType.OK) {
				boolean noAttemptsLeft = current.getAttempts().size() == 0;
				current.removeAttempt(attempt);
				listenButton.setDisable(noAttemptsLeft);
				compareButton.setDisable(noAttemptsLeft);
				saveButton.setDisable(saveButton.isDisabled() || attempt.getLabel().equals("Unsaved Attempt"));
				attemptsTable.getSelectionModel().clearSelection();
			}
		}
	}
}
