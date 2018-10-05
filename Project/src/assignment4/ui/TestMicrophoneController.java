package assignment4.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
public class TestMicrophoneController implements Initializable {

	Stage primaryStage;
	Scene menuScene;
	FXMLLoader menuLoader;
	TestMicrophone testMic;

	@FXML
	Button backButton;

	@FXML
	Line soundAmp, leftAmp1, leftAmp2, rightAmp1, rightAmp2;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	/**
	 * Sets the current Stage to be linked to the previous stage that was used in the previous menu
	 * 
	 * @param primaryStage
	 *            the main stage being used to switch scenes
	 */
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	/**
	 * Sets the controller of the TestMicrophone to this class for mutual coupling
	 * 
	 * @param mic
	 *            the TestMicrophone object
	 */
	public void setMic(TestMicrophone mic) {
		testMic = mic;
		testMic.setController(this);
	}

	/**
	 * Switches scene back to the main menu
	 */
	@FXML
	private void handleBack() {

		try {

			// Load the scene of the Main Menu fxml file
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/MainMenu.fxml"));
			Parent root = loader.load();

			MainMenuController controller = (MainMenuController) loader.getController();

			// Set the stage to use the current stage in order to switch scenes with the same stage
			controller.setPrimaryStage(primaryStage);

			// End the line as soon as the user exits the Test Microphone GUI to stop audio input
			testMic.endLine();

			// Switch scenes to main menu
			primaryStage.setScene(new Scene(root));
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}

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
