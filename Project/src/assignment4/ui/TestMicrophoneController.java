package assignment4.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
 */
public class TestMicrophoneController extends BaseController {

	@FXML
	Button backButton;

	@FXML
	Line soundAmp, leftAmp1, leftAmp2, rightAmp1, rightAmp2;

	protected void init() {
		new TestMicrophone().setController(this);
	}
	
	
	/**
	 * Switches scene back to the main menu
	 */
	@FXML
	private void handleBack() {
		showScene("/resources/MainMenu.fxml", false, false);
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
