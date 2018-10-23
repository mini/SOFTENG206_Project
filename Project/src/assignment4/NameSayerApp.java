package assignment4;

import java.io.File;
import java.io.IOException;

import assignment4.model.AchievementStats;
import assignment4.model.NamesDB;
import assignment4.ui.BaseController;
import assignment4.utils.FileUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * -- NameSayerApp Class --
 *
 * NameSayerApp is the main entry point of the NameSayer application, containing the main method as well as the hook
 * start method that first initialises the MainMenu scene on entry.
 *
 * The microphone icon in the main menu GUI was made by https://www.flaticon.com/authors/prosymbols from
 * www.flaticon.com.
 *
 * NameSayer is an application that allows the user to choose from a provided database of names to practice. The user
 * may select any number of names, and for each name the user can choose to play the pronunciation, then record their
 * own recording and save. With their attempts, they can further compare the two files to perfect their pronunciation of
 * the names. Furthermore, files in the database can be marked as 'Bad Quality' to allow a different version to play.
 * Other than the effective tool of practicing names, a microphone testing tool is also featured for convenient access
 * for the user.
 * 
 * @author Dhruv Phadnis, Vanessa Ciputra
 */
public class NameSayerApp extends Application {
	public static final String ROOT_DIR = System.getProperty("user.home") + File.separator + "NameSayer/";

	/**
	 * The main entry point to the NameSayer application
	 */
	public static void main(String[] args) {

		// Create the required directories
		new File(ROOT_DIR + "attempts/").mkdirs();
		new File(ROOT_DIR + "temp/").deleteOnExit();
		new File(ROOT_DIR + "temp/silenced/").mkdirs();
		new File(ROOT_DIR + "temp/equalised/").mkdirs();
		new File(ROOT_DIR + "temp/merged/").mkdirs();

		if (!new File(ROOT_DIR + "names/").exists()) {
			try {
				FileUtils.unzip("/resources/nameFiles.zip", ROOT_DIR);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		launch(args);
	}

	/**
	 * Creates the window and set the scene to the main menu
	 */
	public void start(Stage primaryStage) throws IOException {
		NamesDB namesDB = new NamesDB();
		AchievementStats stats = new AchievementStats();

		primaryStage.setTitle("NameSayer");
		primaryStage.setResizable(false);

		// Load the scene of the Player fxml file
		FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/resources/fxmls/MainMenu.fxml"));
		Parent menuPane = menuLoader.load();
		BaseController controller = menuLoader.getController();

		// Passes required data to controllers
		controller.setup(primaryStage, namesDB, stats);
		controller.init();

		Scene menuScene = new Scene(menuPane);
		primaryStage.setScene(menuScene);
		primaryStage.show();

	}

}
