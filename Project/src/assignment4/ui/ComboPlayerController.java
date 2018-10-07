package assignment4.ui;

import static assignment4.NameSayerApp.ROOT_DIR;

import java.io.File;
import java.util.Random;

import assignment4.model.Combination;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class ComboPlayerController extends BaseController {
	private static final Random random = new Random();

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

	Combination[] playlist;
	private int current = 0;
	private RecordTask recordTask;

	@Override
	public void init() {

        Tooltip tooltip = new Tooltip();
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
        helpButton.setTooltip(tooltip);


		namesList.setCellFactory(value -> new ListCell<Combination>() {
			@Override
			protected void updateItem(Combination item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					setText(item.getCombinedName());
				}
			}
		});

		namesList.getSelectionModel().selectedIndexProperty().addListener((observer, oldVal, newVal) -> {
			if (oldVal != newVal) {
				current = (int) newVal;
				nextCombination();
			}
		});

		namesList.getItems().addAll(playlist);

		// Disable next/previous buttons if only one name is selected
		if (playlist.length == 1) {
			nextButton.setDisable(true);
			prevButton.setDisable(true);
		} else {
			nextButton.setDisable(false);
			prevButton.setDisable(false);
		}

		nextCombination();
	}

	@FXML
	private void playPressed() {
		MediaPlayer player = new MediaPlayer(new Media(playlist[current].getPath()));
		player.setAutoPlay(true);
	}

	@FXML
	private void badQualityPressed() {

	}

	@FXML
	private void recordPressed() {
		if(recordButton.getText().equals("Record")) {
			File file = new File(ROOT_DIR + "attempts/" + playlist[current].getMergedName() + "/latest.wav");
			recordButton.setText("Stop");
			
			listenButton.setDisable(true);
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
		MediaPlayer player = new MediaPlayer(new Media(new File(ROOT_DIR + "attempts/" + playlist[current].getMergedName() + "/latest.wav").toURI().toString()));
		player.setAutoPlay(true);
	}

	@FXML
	private void comparePressed() {
		MediaPlayer player = new MediaPlayer(new Media(new File(ROOT_DIR + "attempts/" + playlist[current].getMergedName() + "/latest.wav").toURI().toString()));
		player.setAutoPlay(true);

		player.setOnEndOfMedia(() -> {
			playPressed();
		});
	}

	@FXML
	private void previousPressed() {
		current--;
		if (current < 0) {
			current = playlist.length - 1;
		}
		nextCombination();
	}

	@FXML
	private void nextPressed() {
		if (shuffleCheckBox.isSelected()) {
			current = random.nextInt(playlist.length);
		} else {
			current = (current + 1) % playlist.length;
		}
		nextCombination();
	}

	private void nextCombination() {
		currentLabel.setText(playlist[current].getCombinedName());

		if (new File(ROOT_DIR + "attempts/" + playlist[current].getMergedName() + "/latest.wav").exists()) {
			listenButton.setDisable(false);
			compareButton.setDisable(false);
		} else {
			listenButton.setDisable(true);
			compareButton.setDisable(true);
		}

		namesList.getSelectionModel().select(current);
	}
}
