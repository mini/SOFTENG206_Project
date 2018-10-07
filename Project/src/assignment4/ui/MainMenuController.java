package assignment4.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

/**
 * -- MainMenuController Class --
 *
 * MainMenuController acts as the controller for the MainMenu GUI, where events are listened and handled appropriately
 * with this class. It ensures that the two buttons link to its corresponding event handler methods startPressed() and
 * testMic(). Due to being a main menu, the function of the screen is to direct the user action of each button to switch
 * to its appropriate scene.
 *
 */
public class MainMenuController extends BaseController{

	@FXML
	private Button helpButton;

	@Override
	public void init() {
		Tooltip tooltip = new Tooltip();
		tooltip.setText("Welcome to NameSayer. In this main menu, you can click: \n\n" +
				"* START to begin choosing your playlist to start learning your chosen names. \n" +
				"* PRACTISE to practise any name on the database without needing to select a list.\n" +
				"* TEST MICROPHONE to measure your microphone levels.\n" +
				"* The Trophy icon to show your current achievements.");
		helpButton.setTooltip(tooltip);
	}

	@FXML
	private void startPressed() {
		showScene("/resources/NameSelector.fxml", false, true);
	}

	/**
	 * Switches scene to the Player GUI
	 */
	@FXML
	private void practisePressed() {
        PractiseController.addName = false;
		showScene("/resources/Practise.fxml", false, true);
	}

	/**
	 * Switches scene to the TestMicrophone GUI
	 */
	@FXML
	private void testMic() {
		showScene("/resources/TestMicrophone.fxml", false, false);
	}

	/**
	 * Switches scene to the Reward GUI
	 */
	@FXML
	private void rewardPressed() {
		showScene("/resources/Rewards.fxml", false, false);
	}

	/**
	 * Switches scene to the Add Names GUI
	 */
	@FXML
	private void addNamePressed() {
        PractiseController.addName = true;
		showScene("/resources/AddName.fxml", false, false);
	}

}
