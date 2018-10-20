package assignment4.ui;

import java.util.ArrayList;

import assignment4.model.Combination;
import assignment4.model.Name;
import assignment4.utils.PermanentTooltip;
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
public class MainMenuController extends BaseController {

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
		PermanentTooltip.setTooltipTimers(0, 99999, 0);

		Tooltip.install(helpButton, tooltip);
	}

	@FXML
	private void startPressed() {
		showScene("NameSelector.fxml", false, true);
	}

	/**
	 * Switches scene to the Player GUI
	 */
	@FXML
	private void practisePressed() {
		ArrayList<Name> all = namesDB.getAllNames();
		ArrayList<Combination> combos = new ArrayList<Combination>(all.size());
		for (Name name: all) {
			Combination combo = new Combination(name.getName()).addName(name);
			combo.process(namesDB);
			combos.add(combo);
		}
		
		showScene("ComboPlayer.fxml", false, true, c -> {
			ComboPlayerController controller = (ComboPlayerController) c;
			controller.setPlaylist(combos);
		});
	}

	/**
	 * Switches scene to the TestMicrophone GUI
	 */
	@FXML
	private void testMic() {
		showScene("TestMicrophone.fxml", false, false);

		// SPECIAL FEATURE!!!
		RewardsController.special++;
	}

	/**
	 * Switches scene to the Reward GUI
	 */
	@FXML
	private void rewardPressed() {
		showScene("Rewards.fxml", false, false);
	}

	/**
	 * Switches scene to the Add Names GUI
	 */
	@FXML
	private void addNamePressed() {
		showScene("AddName.fxml", false, false);
	}

}
