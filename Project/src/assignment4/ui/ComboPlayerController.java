package assignment4.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import assignment4.model.Combination;
import assignment4.utils.PermanentTooltip;
import assignment4.utils.RecordTask;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Tooltip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class ComboPlayerController extends BaseController {
	private static final Random random = new Random();
	private boolean all;
	private String inputString;

	//@formatter:off
	@FXML private ListView<Combination> namesList;

	@FXML private Button playButton;
	@FXML private Button badQualityButton;
	@FXML private Label currentLabel;
	@FXML private CheckBox shuffleCheckBox;
	
	@FXML private Button recordButton;
	@FXML private Button listenButton;
	@FXML private Button compareButton;
	
	@FXML private Button prevButton;
	@FXML private Button nextButton;
	@FXML private Button helpButton;

	//@formatter:on

	private ArrayList<Combination> playlist;
	private ArrayList<Combination> notPlayedInPass;
	private Stack<Combination> history;
	private Combination current;
	private RecordTask recordTask;

	private boolean pauseHistory = false;


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
				"* For each name: \n" +
				"-- PLAY to listen to the pronunciation from the database \n" +
				"-- BAD QUALITY to mark the recording as bad quality \n" +
				"-- RECORD to record your own pronunciation (up to 5 seconds) \n" +
				"-- LISTEN to select and listen to an audio recording of your selected attempt \n" +
				"-- COMPARE to subsequently play your attempt with the database pronunciation straight after \n" +
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
				if (!pauseHistory && oldVal != null) {
					history.push(oldVal);
				}
				current = newVal;
				nextCombination();
			}
		});

		namesList.getItems().addAll(playlist);

		// Disable next/previous buttons if only one name is selected
		if (playlist.size() == 1) {
			nextButton.setDisable(true);
			prevButton.setDisable(true);
		}

		shuffleCheckBox.selectedProperty().addListener((observer, oldVal, newVal) -> {
			if (!oldVal && newVal) {
				newPass();
			}
		});

		nextCombination();
	}

	public void setPlaylist(ArrayList<Combination> playlist) {
		this.playlist = playlist;
		current = playlist.get(0);
	}

	@FXML
	private void playPressed() {
		MediaPlayer player = new MediaPlayer(new Media(current.getPath()));
		player.setAutoPlay(true);
		badQualityButton.setDisable(false);
	}

	@FXML
	private void badQualityPressed() {
		namesDB.toggleBadCombo(current);
		current.toggleBadQuality();
		badQualityButton.setText(current.isBadQuality() ? "Good Quality" : "Bad Quality");
	}

	@FXML
	private void recordPressed() {
		if (recordButton.getText().equals("Record")) {
			File file = new File(ROOT_DIR + "attempts/" + current.getMergedName() + ".wav");
			recordButton.setText("Stop");

			listenButton.setDisable(true); // Disable until file has stopped being written to
			compareButton.setDisable(true);

			recordTask = new RecordTask(file, () -> {
				Platform.runLater(() -> {
					recordButton.setText("Record");
					listenButton.setDisable(false);
					compareButton.setDisable(false);
				});
			});

			recordTask.start();
		} else {
			recordTask.stop();
		}
	}

	@FXML
	private void listenPressed() {
		MediaPlayer player = new MediaPlayer(new Media(new File(ROOT_DIR + "attempts/" + current.getMergedName() + ".wav").toURI().toString()));
		player.setAutoPlay(true);
	}

	@FXML
	private void comparePressed() {
		MediaPlayer player = new MediaPlayer(new Media(new File(ROOT_DIR + "attempts/" + current.getMergedName() + ".wav").toURI().toString()));
		player.setAutoPlay(true);

		player.setOnEndOfMedia(() -> {
			playPressed();
		});

		RewardsController.compares++;
	}

	@FXML
	private void previousPressed() {
		if (history.empty()) {
			System.out.println("Emp");
			int index = playlist.indexOf(current) - 1 + playlist.size();
			current = playlist.get(index % playlist.size());
		} else {
			System.out.println("pop");
			current = history.pop();
		}
		pauseHistory = true;
		nextCombination(); // As this will trigger select event and we dont want to add prev to history
		pauseHistory = false;
	}

	@FXML
	private void nextPressed() {
		// history.push(current);
		if (shuffleCheckBox.isSelected()) {
			current = notPlayedInPass.remove(random.nextInt(notPlayedInPass.size()));
			if (notPlayedInPass.isEmpty()) {
				newPass();
			}
		} else {
			int index = playlist.indexOf(current) + 1;
			current = playlist.get(index % playlist.size());
		}

		nextCombination();
	}
	
	private void newPass() {
		notPlayedInPass = new ArrayList<Combination>(playlist);
		notPlayedInPass.remove(current);
	}

	@FXML
	private void backPressed() {
		if(all) {
			showScene("MainMenu.fxml", false, false);
		} else {
			showScene("NameSelector.fxml", false, true, c -> {
				((SelectorController)c).setTextContent(inputString);
			});
		}
	}

	private void nextCombination() {
		currentLabel.setText(current.getDisplayName());
		badQualityButton.setText(current.isBadQuality() ? "Good Quality" : "Bad Quality");
		badQualityButton.setDisable(!current.isBadQuality()); // Require combo to be played first

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

	void setInputString(String inputString) {
		this.inputString = inputString;
	}
	
	void setAllNames() {
		all = true;
	}
}
