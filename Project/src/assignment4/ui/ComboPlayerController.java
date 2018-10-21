package assignment4.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import assignment4.model.Combination;
import assignment4.model.Name;
import assignment4.utils.PermanentTooltip;
import assignment4.utils.RecordTask;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class ComboPlayerController extends BaseController {	
	private static final Random random = new Random();
	private static final Combination LOADING = new Combination("Loading...");
	private static final int COMPARE_LOOP_COUNT = 3;
	
	private boolean all;
	private String inputString;

	//@formatter:off
	@FXML private ListView<Combination> namesList;

	@FXML private Button playButton;
	@FXML private Button badQualityButton;
	@FXML private Label currentLabel;
	@FXML private CheckBox shuffleCheckBox;
	@FXML private Slider volSlider;
	
	@FXML private Button recordButton;
	@FXML private Button listenButton;
	@FXML private Button compareButton;

	@FXML private Button backButton;
	@FXML private Button prevButton;
	@FXML private Button nextButton;
	@FXML private Button helpButton;

	//@formatter:on

	private ObservableList<Combination> playlist;
	private ArrayList<Combination> notPlayedInPass;
	private Stack<Combination> history;
	private Combination current;
	private RecordTask recordTask;

	private boolean pauseHistory = false;
	private int loopCount = 0;
	private AtomicInteger combosProcessed = new AtomicInteger(0);
	private MediaPlayer player;

	@Override
	public void init() {
		history = new Stack<Combination>();

		// Prevented Truncating
		currentLabel.setMaxWidth(250);
		currentLabel.setMaxHeight(60);
		currentLabel.setTextOverrun(OverrunStyle.CLIP);
		currentLabel.setWrapText(true);

		final Tooltip tooltip = new Tooltip();
		tooltip.setText("Player: \n\n" +
				"* Select name(s) to practise and click play. \n" +
				"* The screen will iterate through your selection one by one, where you can go to the next name by clicking NEXT or PREVIOUS. \n" +
				"* The names database is shown on the left list. \n" +
				"* Change the volume of playback recordings by using the slider below. \n" +
				"* For each name: \n" +
				"-- PLAY to listen to the pronunciation from the database \n" +
				"-- BAD QUALITY to mark the recording as bad quality \n" +
				"-- RECORD to record your own pronunciation (up to 5 seconds) \n" +
				"-- LISTEN to select and listen to an audio recording of your selected attempt \n" +
				"-- COMPARE to subsequently play your attempts three times with the database pronunciation straight after each attempt \n" +
				"* Exit this screen to go back to the main menu");

		PermanentTooltip.setTooltipTimers(0, 99999, 0);
		Tooltip.install(helpButton, tooltip);

		namesList.setCellFactory(value -> new ListCell<Combination>() {
			@Override
			protected void updateItem(Combination item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					setText(item.getDisplayName());
				}
			}
		});

		// Switch to selected combos on the list
		namesList.getSelectionModel().selectedItemProperty().addListener((observer, oldVal, newVal) -> {
			if (oldVal != newVal) {
				if (!pauseHistory && oldVal != null && newVal != LOADING) {
					history.push(oldVal);
				}
				current = newVal;
				nextCombination();
			}
		});

		// Disable next/previous buttons if only one name is selected
		if (playlist.size() == 1) {
			nextButton.setDisable(true);
			prevButton.setDisable(true);
		}

		// Reset names available to shuffle
		shuffleCheckBox.selectedProperty().addListener((observer, oldVal, newVal) -> {
			if (!oldVal && newVal) {
				newPass();
			}
		});

		volSlider.valueProperty().addListener((observer, oldVal, newVal) -> {
			if (player != null) {
				player.setVolume((double) newVal / volSlider.getMax());
			}
		});
	}

	public void setPlaylist(List<Combination> newPlaylist) {
		int target = newPlaylist.size();

		// Need thread safe collection
		playlist = FXCollections.<Combination>synchronizedObservableList(FXCollections.<Combination>observableArrayList());
		
		for (int i = 0; i < target; i++) { //Insert placeholders
			playlist.add(LOADING);
		}
		namesList.setItems(playlist);

		for (int i = 0; i < target; i++) {
			int index = i;
			Combination combo = newPlaylist.get(i); // Not just adding to preserve order 
			combo.process(namesDB, (success) -> {

				combosProcessed.incrementAndGet();
				if (success) {
					Platform.runLater(() -> {
						playlist.set(index, combo);

						if (current == null) { // Auto-select the first processed combo
							current = combo;
							nextCombination();
						}
					});
				}
				if (combosProcessed.get() == target) { // Remove any failed combos
					Platform.runLater(() -> {
						playlist.removeIf(c -> c == LOADING);
					});
				}
				return null;
			});
		}
	}

	@FXML
	private void playPressed() {
		play(current.getPath());
		badQualityButton.setDisable(false);
	}

	@FXML
	private void badQualityPressed() {
		Alert bqAlert = new Alert(AlertType.CONFIRMATION, "Choose which name is of bad qualilty. A different version will be used if found.", ButtonType.CANCEL);

		LinkedHashMap<ButtonType, Name> buttons = new LinkedHashMap<ButtonType, Name>();
		for (Name name : current.getNameSet()) { // Generate named buttons and link to related Name object
			ButtonType button = new ButtonType(name.getName());
			buttons.put(button, name);
			bqAlert.getButtonTypes().add(button);
		}

		bqAlert.showAndWait();

		if (bqAlert.getResult() == ButtonType.CANCEL) {
			return;
		}

		// Generate combo again with new versions if they exist
		buttons.get(bqAlert.getResult()).getBestVersion().notifyBadQuality();
		Combination c = namesList.getSelectionModel().getSelectedItem();
		current = LOADING;
		nextCombination();
		c.process(namesDB, (success) -> {
			current = c;
			Platform.runLater(() -> nextCombination());
			return null;
		});
	}

	@FXML
	private void recordPressed() {
		if (recordButton.getText().equals("Record")) { // Start
			File dest = new File(ROOT_DIR + "attempts/" + current.getMergedName() + ".wav");
			recordButton.setText("Stop");

			listenButton.setDisable(true); // Disable until file has stopped being written to
			compareButton.setDisable(true);

			recordTask = new RecordTask(dest, () -> {
				Platform.runLater(() -> {
					stats.incrementRecords();
					recordButton.setText("Record");
					listenButton.setDisable(false);
					compareButton.setDisable(false);
				});
			});

			recordTask.start();
			backButton.setDisable(true);

		} else { // Stop
			recordTask.stop();
			backButton.setDisable(false);
			listenButton.setDisable(false);
			compareButton.setDisable(false);
		}
	}

	@FXML
	private void listenPressed() {
		play(new File(ROOT_DIR + "attempts/" + current.getMergedName() + ".wav").toURI().toString());
	}

	@FXML
	private void comparePressed() {
		compareLoop();
		stats.incrementCompares();
		loopCount = 0;
	}

	// Repeat the user attempt as well as the database pronunciation 3 times each
	private void compareLoop() {
		play(new File(ROOT_DIR + "attempts/" + current.getMergedName() + ".wav").toURI().toString()).setOnEndOfMedia(() -> {
			play(current.getPath()).setOnEndOfMedia(() -> {
				if (++loopCount < COMPARE_LOOP_COUNT) {
					compareLoop();
				}
			});
		});
	}

	/**
	 * Stops any currently playing clips and plays the specified one.
	 * @param path to audio file
	 * @return the MediaPlayer if any extra settings should be applied
	 */
	private MediaPlayer play(String path) {
		if (player != null) {
			player.stop();
			player.dispose();
		}
		player = new MediaPlayer(new Media(path));
		player.setVolume(volSlider.getValue() / volSlider.getMax());
		player.setAutoPlay(true);

		return player;
	}

	@FXML
	private void previousPressed() {
		if (history.empty()) { // Go up the list if no history
			int index = playlist.indexOf(current) - 1 + playlist.size();
			current = playlist.get(index % playlist.size()); // Wraps to bottom
		} else {
			current = history.pop();
		}
		pauseHistory = true; // This will trigger a select event and we don't want to add prev name to history
		nextCombination(); 
		pauseHistory = false;
	}

	@FXML
	private void nextPressed() {
		if (shuffleCheckBox.isSelected()) { // Ensure all are played before playing same one again
			current = notPlayedInPass.remove(random.nextInt(notPlayedInPass.size()));
			if (notPlayedInPass.isEmpty()) {
				newPass();
			}
		} else { // Move down the list
			int index = playlist.indexOf(current) + 1;
			current = playlist.get(index % playlist.size());
		}

		nextCombination();
	}

	/**
	 * Resets available names to choose from if shuffling
	 */
	private void newPass() {
		notPlayedInPass = new ArrayList<Combination>(playlist);
		notPlayedInPass.remove(current);
	}

	@FXML
	private void backPressed() {
		if (player != null) { // Stop any playing clips
			player.stop();
			player.dispose();
		}

		if (all) {
			showScene("MainMenu.fxml", false, false); // Return to where we came from
		} else {
			showScene("NameSelector.fxml", false, true, c -> {
				((SelectorController) c).setTextContent(inputString);
			});
		}
	}

	private void nextCombination() {
		currentLabel.setText(current.getDisplayName());
		if (current == LOADING) {
			playButton.setDisable(true); // Don't want user performing actions on dummy Combo
			recordButton.setDisable(true);
			nextButton.setDisable(true);
			prevButton.setDisable(true);
		} else {
			playButton.setDisable(false);
			recordButton.setDisable(false);
			nextButton.setDisable(playlist.size() <= 1);
			prevButton.setDisable(playlist.size() <= 1);

			// Check for existing recording
			if (new File(ROOT_DIR + "attempts/" + current.getMergedName() + ".wav").exists()) {
				listenButton.setDisable(false);
				compareButton.setDisable(false);
			} else {
				listenButton.setDisable(true);
				compareButton.setDisable(true);
			}
			namesList.getSelectionModel().select(current);
		}
	}

	void setInputString(String inputString) {
		this.inputString = inputString;
	}

	void setAllNames() {
		all = true;
	}
}
