package assignment4.ui;

import static assignment4.NameSayerApp.ROOT_DIR;

import java.io.File;
import java.util.Random;

import assignment4.model.Combination;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class ComboPlayerController extends BaseController {
	private static final Random random = new Random();

	//@formatter:off
	@FXML private TableView<Combination> namesTable;
	@FXML private TableColumn<Combination, String> nameColumn;
	@FXML private TableColumn<Combination, String> playingColumn;
	
	@FXML private Button playButton;
	@FXML private Button badQualityButton;
	@FXML private Label currentLabel;
	@FXML private CheckBox shuffleCheckBox;
	
	@FXML private Button recordButton;
	@FXML private Button listenButton;
	@FXML private Button compareButton;
	
	@FXML private Button prevButton;
	@FXML private Button nextButton;
	//@formatter:on

	Combination[] playlist;
	private int current;

	@Override
	protected void init() {
		nameColumn.setCellValueFactory(new PropertyValueFactory<Combination, String>("combinedName"));
		playingColumn.setCellValueFactory(new PropertyValueFactory<Combination, String>("name"));

		namesTable.widthProperty().addListener(new HideHeader(namesTable));
		namesTable.setStyle("-fx-table-cell-border-color: transparent;");
		namesTable.getItems().addAll(playlist);

		nextCombination(playlist[0]);
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
		File file = new File(ROOT_DIR + "attempts/" + playlist[current].getMergedName() + "/latest.wav");

		RecordTask recordTask = new RecordTask(file, 5000, () -> {
			listenButton.setDisable(false);
			compareButton.setDisable(false);
		});
		recordTask.start();
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
		if (current <= 0) {
			current = playlist.length - 1;
		}
		nextCombination(playlist[current]);
	}

	@FXML
	private void nextPressed() {
		if (shuffleCheckBox.isSelected()) {
			current = random.nextInt(playlist.length);
		} else {
			current = (current + 1) % playlist.length;
		}
		nextCombination(playlist[current]);
	}

	private void nextCombination(Combination combination) {
		currentLabel.setText(playlist[current].getCombinedName());

		if (new File(ROOT_DIR + "attempts/" + playlist[current].getMergedName() + "/latest.wav").exists()) {
			listenButton.setDisable(false);
			compareButton.setDisable(false);
		}
	}
}
