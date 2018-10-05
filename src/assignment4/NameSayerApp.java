package assignment4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import assignment4.ui.MainMenuController;

import java.io.File;
import java.io.IOException;

/**
 * -- NameSayerApp Class --
 *
 * NameSayerApp is the main entry point of the NameSayer application, containing the main method
 * as well as the hook start method that first initialises the MainMenu scene on entry.
 *
 * The microphone icon in the main menu GUI was made by https://www.flaticon.com/authors/prosymbols from www.flaticon.com.
 *
 * NameSayer is an application that allows the user to choose from a provided database of names to practice. The user
 * may select any number of names, and for each name the user can choose to play the pronunciation, then record their own
 * recording and save. With their attempts, they can further compare the two files to perfect their pronunciation of
 * the names. Furthermore, files in the database can be marked as 'Bad Quality' to allow a different version to play.
 * Other than the effective tool of practicing names, a microphone testing tool is also featured for convenient access for
 * the user.
 *
 */
public class NameSayerApp extends Application {
	public static final String ROOT_DIR = System.getProperty("user.home") + File.separator + "NameSayer";

	Stage _primaryStage;
	Scene mainScene;

	/**
	 * The main entry point to the NameSayer application
	 */
	public static void main(String[] args) {

		// Create the initial directory for all attempts to be put under
		new File(ROOT_DIR + "/attempts/").mkdirs();

		// Starts the application
		launch(args);
	}

	/**
	 * Creates the window and set the scene to the main menu
	 */
	public void start(Stage primaryStage) throws IOException {
		primaryStage.setTitle("NameSayer");

		// Prevent the stage from being resized by the user
		primaryStage.setResizable(false);

		// Load the scene of the Player fxml file
		// Microphone icon was made by https://www.flaticon.com/authors/prosymbols from www.flaticon.com
		FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/resources/MainMenu.fxml"));
		Parent menuPane = menuLoader.load();
		MainMenuController controller = menuLoader.getController();

		// Passes in the current stage to be used as the main stage for all following windows
		controller.setPrimaryStage(primaryStage);

		// Add the scene of the main menu
		Scene menuScene = new Scene(menuPane);
		primaryStage.setScene(menuScene);
		primaryStage.show();

	}

}
