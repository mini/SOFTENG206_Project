package assignment4.ui;

import javafx.fxml.FXML;

/**
 * -- MainMenuController Class --
 *
 * MainMenuController acts as the controller for the MainMenu GUI, where events are listened and handled
 * appropriately with this class. It ensures that the two buttons link to its corresponding event
 * handler methods startPressed() and testMic(). Due to being a main menu, the function of the screen
 * is to direct the user action of each button to switch to its appropriate scene.
 *
 */
public class MainMenuController extends BaseController {

	@FXML
	private void startPressed() {
		showScene("/resources/NameSelector.fxml", false);
	}

	/**
	 * Switches scene to the Player GUI
	 */
	@FXML
	private void practisePressed() {
		showScene("/resources/Practise.fxml", false);
	}

	/**
	 * Switches scene to the TestMicrophone GUI
	 */
	@FXML
	private void testMic() {
		showScene("/resources/TestMicrophone.fxml", false);
	}
}
