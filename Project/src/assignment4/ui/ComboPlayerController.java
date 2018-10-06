package assignment4.ui;

import java.util.List;

import assignment4.model.Combination;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ComboPlayerController extends BaseController {

	//@formatter:off
	@FXML private TableView<Combination> namesTable;
	@FXML private TableColumn<Combination, String> nameColumn;
	@FXML private TableColumn<Combination, String> playingColumn;
	
	@FXML private Button playButton;
	@FXML private Button badQualityButton;
	@FXML private Label currentLabel;
	@FXML private CheckBox shuffleCheckBox;
	
	@FXML private Button recordButton;
	@FXML private Button saveButton;
	@FXML private Button listenButton;
	@FXML private Button compareButton;
	
	@FXML private Button prevButton;
	@FXML private Button nextButton;
	//@formatter:on
	
	List<Combination> playlist;
	
	@Override
	protected void init() {
		nameColumn.setCellValueFactory(new PropertyValueFactory<Combination, String>("combinedName"));
		playingColumn.setCellValueFactory(new PropertyValueFactory<Combination, String>("name"));
		namesTable.getItems().addAll(playlist);
		
		System.out.println(namesTable);
		
	}
	
	@FXML
	private void playPressed() {
		
	}
	
	@FXML
	private void badQualityPressed() {
		
	}
	
	@FXML
	private void recordPressed() {
		
	}
	
	@FXML
	private void savePressed() {
		
	}
	
	@FXML
	private void listenPressed() {
		
	}
	
	@FXML
	private void comparePressed() {
		
	}
	
	@FXML
	private void previousPressed() {
		
	}
	
	@FXML
	private void nextPressed() {
		
	}
	
	@FXML
	private void Pressed() {
		
	}
	
	
}
