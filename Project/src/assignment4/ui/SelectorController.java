package assignment4.ui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import assignment4.model.AchievementStats;
import assignment4.model.Combination;
import assignment4.model.Name;
import assignment4.utils.FileUtils;
import assignment4.utils.PermanentTooltip;
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
import javafx.stage.FileChooser;

/**
 * Playlist selector controller
 *
 * @author Dhruv Phadnis, Vanessa Ciputra
 */
public class SelectorController extends BaseController {
	private static final SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	private static final File TEMP_PLAYLIST = new File(ROOT_DIR + "temp/temp_playlist.txt"); 
	
	static {
		TEMP_PLAYLIST.deleteOnExit();
	}
	
	private FileChooser fileChooser = new FileChooser();
	private File lastSelected;
	//@formatter:off
	@FXML private TextField searchTextField;
	@FXML private ListView<Name> namesList;
	
	@FXML private TextArea textInput;
	
	@FXML private Button backButton;
	@FXML private Button playButton;
	@FXML private Button loadButton;
	@FXML private Button saveButton;

	@FXML private Button helpButton;
	//@formatter:on

	@Override
	public void init() {
		Tooltip tooltip = new Tooltip();
		tooltip.setText("Selection Menu:  \n\n" +
				"* Insert each name that you would like to practice in the text area above. \n" +
				"\t* You can double click names from to list to add them instead.\n" +
				"\t* Pushing enter moves down a line.\n" +
				"* Multiple names on one line will be concatenated into one merged name. \n" +
				"* To practise multiple separate names, type each full name on separate lines. \n" +
				"* You can load a txt file or save your current input into a txt file with LOAD and SAVE respectively. \n" +
				"* Click the BACK button to go back to the main screen.");
		helpButton.setTooltip(tooltip);
		PermanentTooltip.setTooltipTimers(0, 99999, 0);

		Tooltip.install(helpButton, tooltip);
		// Name filtering on listview
		searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			namesList.setItems(FXCollections.observableArrayList(namesDB.getNames(newValue)));
		});

		// Append clicked name to text area
		namesList.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				Name selected = namesList.getSelectionModel().getSelectedItem();
				textInput.appendText(selected.getName() + " ");
			}
		});

		namesList.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				textInput.appendText("\n");
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

		textInput.textProperty().addListener((obs, oldVal, newVal) -> {
			boolean hasNoText = newVal.trim().isEmpty();
			playButton.setDisable(hasNoText);
			saveButton.setDisable(hasNoText);
		});

		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Text", "*.txt"),
				new FileChooser.ExtensionFilter("All", "*"));
		
		if(TEMP_PLAYLIST.exists()) {
			textInput.setText(FileUtils.readFile(TEMP_PLAYLIST).trim() + " ");
		}
	}

	@FXML
	private void backPressed() {
		saveTemp();
		showScene("MainMenu.fxml", false, false);
	}

	/**
	 * Loads the selected file into the text area
	 */
	@FXML
	private void loadPressed() {
		fileChooser.setTitle("Select playlist file");
		File file = fileChooser.showOpenDialog(primaryStage);
		if (file != null) {
			String content = FileUtils.readFile(file);
			textInput.setText(content.trim() + " ");
			lastSelected = file;
			fileChooser.setInitialDirectory(lastSelected.getParentFile());
		}
	}

	/**
	 * Saves the contents of the textarea to a file
	 */
	@FXML
	private void savePressed() {
		fileChooser.setTitle("Save playlist to");
		if (lastSelected != null) {
			fileChooser.setInitialDirectory(lastSelected.getParentFile());
			fileChooser.setInitialFileName(lastSelected.getName());
		} else {
			fileChooser.setInitialFileName(fileDateFormat.format(new Date()));
		}
		File file = fileChooser.showSaveDialog(primaryStage);
		if (file != null) {
			try {
				Files.write(file.toPath(), textInput.getText().getBytes(), StandardOpenOption.CREATE);
				lastSelected = file;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Parses textarea content and switches to player view
	 */
	@FXML
	private void playPressed() {
		saveTemp();
		LinkedHashMap<String, Combination> playlist = new LinkedHashMap<String, Combination>();
		ArrayList<String> invalid = new ArrayList<String>();

		// Parsing
		String input = textInput.getText();
		String[] lines = input.split("\n");

		for (String line : lines) {
			line = line.trim();
			if (line.isEmpty() || playlist.containsKey(line)) { // Skip empty or duplicate lines
				continue;
			}

			String[] names = line.replace("-", " ").split(" "); // Split names, treat '-' as a space
			Combination combination = new Combination(line);
			for (String name : names) {
				Name existing = namesDB.getName(name);
				if (existing == null) {
					invalid.add(name);
				}
				combination.addName(existing);

				// Special unique name of lecturer for last achievement. User must practice this particular name in order
				// to obtain the achievement.
				if (combination.getMergedName().equals("catherinewatson")) {
					// Show notification and obtain trophy for special feature
					stats.incrementSpecial(AchievementStats.SpecialFeature.CATHERINEWATSON);
				}
			}
			playlist.put(line, combination);
		}

		// Error message
		if (!invalid.isEmpty()) {
			boolean plural = invalid.size() != 1;
			String errorText = String.format("The name%s %s do%s not exist in the Database. Please remove these names from the playlist to advance.", plural ? "s" : "", invalid, plural ? "" : "es");
			errorText = errorText.replaceAll("\\[|\\]", "\"").replaceAll(", ", "\", \"");

			Alert alert = new Alert(AlertType.WARNING, errorText, ButtonType.OK);
			alert.initOwner(primaryStage);
			alert.showAndWait();
			return;
		}

		showScene("ComboPlayer.fxml", false, true, c -> { // Pass playlist to player
			ComboPlayerController controller = (ComboPlayerController) c;
			controller.setPlaylist(new ArrayList<Combination>(playlist.values()));
		});
	}
	
	private void saveTemp() {
		try {
			Files.write(TEMP_PLAYLIST.toPath(), textInput.getText().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
