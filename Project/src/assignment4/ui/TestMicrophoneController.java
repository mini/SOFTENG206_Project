package assignment4.ui;

import assignment4.utils.PermanentTooltip;
import assignment4.utils.TestMicrophone;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.shape.Line;

/**
 * -- TestMicrophoneController Class --
 *
 * TestMicrophoneController acts as the controller for the TestMicrophone GUI, where events are listened and
 * handled appropriately with this class. It ensures that the back button links to its corresponding
 * event handler method handleBack().
 *
 * The class further handles the byte and RMS information from the audio input to focus on SOUND VISUALISATION
 * as the main interface for the test microphone GUI.
 *
 * @author Dhruv Phadnis, Vanessa Ciputra
 */
public class TestMicrophoneController extends BaseController {

	@FXML private Button backButton, helpButton;
	@FXML private Line soundAmp, leftAmp1, leftAmp2, rightAmp1, rightAmp2;

	public void init() {

		new TestMicrophone().setController(this);

		Tooltip tooltip = new Tooltip();
		tooltip.setText("Test Microphone: \n\n" +
				"* This module allows you to test your microphone levels. \n" +
				"* As you speak, the levels should rise up and down according to your microphone levels. \n" +
				"* Flat levels or no movement mean your microphone is not working/disconnected. \n" +
				"* Click the MAIN MENU button to go back");
		PermanentTooltip.setTooltipTimers(0, 99999,0);

		Tooltip.install(helpButton, tooltip);

	}
	
	/**
	 * Switches scene back to the main menu
	 */
	@FXML
	private void handleBack() {
		showScene("MainMenu.fxml", false, false);
	}

	/**
	 * Vary the levels of the sound visuals when recording voice by varying each level of the
	 * visualisation accordingly. A threshold is used as a maximum for the levels in case
	 * of extreme levels of amplitudes.
	 * 
	 * @param RMS
	 *            the RMS value of the sound input
	 */
	public void varySound(int RMS) {
		// Dynamically change the levels of the visuals using a threshold
		soundAmp.setEndY(-Math.min(RMS * 2, 90));
		leftAmp1.setEndY(-Math.min(RMS, 90));
		rightAmp1.setEndY(-Math.min(RMS, 90));
		leftAmp2.setEndY(-Math.min(RMS / 2, 90));
		rightAmp2.setEndY(-Math.min(RMS / 2, 90));
	}
}
