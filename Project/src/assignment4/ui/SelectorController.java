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
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

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
		searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			namesList.setItems(FXCollections.observableArrayList(namesDB.getNames(newValue)));
		});

		namesList.setCellFactory(Value -> new ListCell<Name>() {
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

		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Text", "*.txt"),
				new FileChooser.ExtensionFilter("All", "*.*")
		);
	}

	@FXML
	private void backPressed() {
		showScene("/resources/MainMenu.fxml", false);
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

		outer: 
		for (String line : lines) {
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
			System.out.println(invalid + " does not exist"); // TODO Error popup
			return;
		}

		for (Combination combination : playlist) {
			combination.process();
		}
		
		showScene("/resources/ComboPlayer.fxml", true, c -> {
			ComboPlayerController controller = (ComboPlayerController) c;
			controller.playlist = playlist.toArray(new Combination[playlist.size()]);
		});

	}

	@FXML
	private void helpPressed() {
		//TODO show help popup
	}
}
