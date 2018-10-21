package assignment4.ui;

import java.util.ArrayList;

import assignment4.model.AchievementStats;
import assignment4.model.Combination;
import assignment4.model.Name;
import assignment4.utils.PermanentTooltip;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

/**
 * -- MainMenuController Class --
 *
 * Used to switch scenes
 */
public class MainMenuController extends BaseController {

	@FXML
	private Button helpButton;

	@Override
	public void init() {
		Tooltip tooltip = new Tooltip();
		tooltip.setText("Welcome to NameSayer. In this main menu, you can click: \n\n" +
				"* PRACTICE CUSTOM to begin choosing your playlist to start learning your chosen customisable names. \n" +
				"* PRACTISE ALL NAMES to practise any name on the database without needing to select a list.\n" +
				"* TEST MICROPHONE to measure your microphone levels.\n" +
				"* The trophy icon to show your current achievements.");
		PermanentTooltip.setTooltipTimers(0, 99999, 0);

		Tooltip.install(helpButton, tooltip);
	}

	@FXML
	private void customPressed() {
		showScene("NameSelector.fxml", false, true);
	}

	/**
	 * Switches scene to the Player GUI
	 */
	@FXML
	private void allPressed() {
		ArrayList<Name> all = namesDB.getAllNames(); // Need to pass all names as combos
		ArrayList<Combination> combos = new ArrayList<Combination>(all.size());
		for (Name name : all) {
			Combination combo = new Combination(name.getName()).addName(name);
			combos.add(combo);
		}

		showScene("ComboPlayer.fxml", false, true, c -> {
			ComboPlayerController controller = (ComboPlayerController) c;
			controller.setPlaylist(combos);
			controller.setAllNames();
		});
	}

	/**
	 * Switches scene to the TestMicrophone GUI
	 */
	@FXML
	private void testMic() {
		showScene("TestMicrophone.fxml", false, false);

		// SPECIAL FEATURE!!!
		stats.incrementSpecial(AchievementStats.SpecialFeature.MICROPHONE);
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
