package assignment4.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import assignment4.model.Combination;
import assignment4.model.Name;
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
import javafx.stage.Modality;

public class SelectorController extends BaseController {
	private static final SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

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

		searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			namesList.setItems(FXCollections.observableArrayList(namesDB.getNames(newValue)));
		});
		
		namesList.setOnKeyPressed(event -> {
			if(event.getCode() == KeyCode.ENTER) {
				textInput.appendText("\n");
			}
		});

		namesList.setOnMouseClicked(event -> {
			if(event.getClickCount() == 2) {
				Name selected = namesList.getSelectionModel().getSelectedItem();
				textInput.appendText(selected.getName() + " ");
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
				new FileChooser.ExtensionFilter("All", "*.*"));
	}

	@FXML
	private void backPressed() {
		showScene("/resources/MainMenu.fxml", false, false);
	}

	@FXML
	private void loadPressed() {
		fileChooser.setTitle("Select playlist file");
		File file = fileChooser.showOpenDialog(primaryStage);
		if (file != null) {
			try (Scanner scanner = new Scanner(file).useDelimiter("\\Z")) {
				textInput.setText(scanner.next());
				lastSelected = file;
				fileChooser.setInitialDirectory(lastSelected.getParentFile());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

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

	@FXML
	private void playPressed() {
		List<Combination> playlist = new ArrayList<Combination>();
		List<String> invalid = new ArrayList<String>();

		String input = textInput.getText();
		input = input.replace("-", " ");
		String[] lines = input.split("\n");

		outer: for (String line : lines) {
			line = line.trim();
			if (line.isEmpty()) {
				continue;
			}

			String[] names = line.split(" ");
			Combination combination = new Combination();
			for (String name : names) {
				Name existing = namesDB.getName(name);
				if (existing == null) {
					invalid.add(name);
					break outer;
				}
				combination.addName(existing);
			}
			playlist.add(combination);
		}

		if (!invalid.isEmpty()) {
			Alert alert = new Alert(AlertType.WARNING,"The name(s) " + invalid + " do not exist.", ButtonType.OK);
			alert.showAndWait();
			return;
		}

		for (Combination combination : playlist) {
			combination.process();
		}

		showScene("/resources/ComboPlayer.fxml", true, true, c -> {
			ComboPlayerController controller = (ComboPlayerController) c;
			controller.playlist = playlist.toArray(new Combination[playlist.size()]);
			controller.primaryStage.initModality(Modality.NONE);
		});
	}
}
