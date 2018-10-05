package assignment4.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * -- MainMenuController Class --
 *
 * MainMenuController acts as the controller for the MainMenu GUI, where events are listened and handled
 * appropriately with this class. It ensures that the two buttons link to its corresponding event
 * handler methods startPressed() and testMic(). Due to being a main menu, the function of the screen
 * is to direct the user action of each button to switch to its appropriate scene.
 *
 */
public class MainMenuController implements Initializable {

	Stage primaryStage;
	Scene secondScene;
	Scene testMicScene;
	Scene mainScene;

	Parent secondPane;
	Parent testMicPane;

	FXMLLoader secondPageLoader;
	FXMLLoader testMicLoader;

	// Represents the main entry into the player screen
	@FXML
	Button startButton;

	// Represents the entry into the practise module
	@FXML
	Button practiseButton;

	// Represents the button that allows the user to test
	// their microphone in the following GUI
	@FXML
	Button testMicButton;

	// Override the abstract initialize method to load the player screen when required
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


	@FXML
	private void startPressed() {

		try {

			// Load the scene of the Player fxml file
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/MainPlayer.fxml"));
			Parent root = loader.load();

			PlayController controller = loader.getController();

			// Set the stage to use the current stage in order to switch scenes with the same stage
			controller.setPrimaryStage(primaryStage);

			// Change scenes to the Player GUI
			primaryStage.setScene(new Scene(root));

			// Allow the user to resize the window of the Player menu for usability
			primaryStage.setResizable(true);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Switches scene to the Player GUI
	 */
	@FXML
	private void practisePressed() {

		try {

			// Load the scene of the Player fxml file
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Player.fxml"));
			Parent root = loader.load();

			PractiseController controller = (PractiseController) loader.getController();

			// Set the stage to use the current stage in order to switch scenes with the same stage
			controller.setPrimaryStage(primaryStage);

			// Change scenes to the Player GUI
			primaryStage.setScene(new Scene(root));

			// Allow the user to resize the window of the Player menu for usability
			primaryStage.setResizable(true);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Switches scene to the TestMicrophone GUI
	 */
	@FXML
	private void testMic() {
		try {

			// Load the scene of the Player fxml file
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/TestMicrophone.fxml"));
			Parent root = loader.load();

			TestMicrophone testMic = new TestMicrophone();

			TestMicrophoneController controller = (TestMicrophoneController) loader.getController();

			// Set the stage to use the current stage in order to switch scenes with the same stage
			controller.setPrimaryStage(primaryStage);

			// Set the controller for the TestMicrophone class to refer to this stage
			controller.setMic(testMic);

			// Change scenes to TestMicrophone GUI
			primaryStage.setScene(new Scene(root));
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
