package assignment4;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import assignment4.model.NamesDB;
import assignment4.ui.BaseController;
import assignment4.ui.RewardsController;
import assignment4.utils.FileUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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

		primaryStage.setTitle("NameSayer");
		primaryStage.setResizable(false);

		// When the user closes the application, ensure that the achievement scores are saved
		saveAchievements(primaryStage);

		// Load any previous scores to the current session
		loadSave();

		// Load the scene of the Player fxml file
		FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/resources/fxmls/MainMenu.fxml"));
		Parent menuPane = menuLoader.load();
		BaseController controller = menuLoader.getController();

		// Passes required data to controllers
		controller.setup(primaryStage, namesDB);
		controller.init();

		Scene menuScene = new Scene(menuPane);
		primaryStage.setScene(menuScene);
		primaryStage.show();

	}

	/**
	 * Store the values of the achievements progress of the particular user on closing the window
	 * @param primaryStage the stage being used
	 *
	 * Used https://stackoverflow.com/questions/22576261/how-do-i-get-the-close-event-of-a-stage-in-javafx as a reference
	 * on how to track the closing of a window
	 */
	private void saveAchievements(Stage primaryStage) {


		// On closing of the window
		primaryStage.setOnHiding(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						try {

							// Create a new file called achievements.txt that stores the integer values
							// of the records, compares and specials scores of the current user
							try (Writer writer = new BufferedWriter(new OutputStreamWriter(
									new FileOutputStream(ROOT_DIR + "achievements.txt"), "utf-8"))) {
								writer.write("" + RewardsController.records + "\n");
								writer.write("" + RewardsController.compares + "\n");
								writer.write("" + RewardsController.special);
							}

						} catch (IOException e) {
							e.printStackTrace();
						}


						System.exit(0);

					}
				});
			}
		});
	}


	/**
	 * Loads the previous attempt of progress in terms of achievements and recording, comparing and special scores.
	 * This relates to the rewards page.
	 */
	private void loadSave() {


		// Read the achievements save file from previous attempts
		File file = new File(ROOT_DIR + "achievements.txt");

		// If there has been a previous score, apply the scores to continue progress. Otherwise, start fresh.
		if (file.exists()) {

			try {
				Scanner scanner = new Scanner(file);

				// Read the first line that corresponds to the previous recording score, and set it
				// to be the current recording score to continue progress
				int recordScore = scanner.nextInt();
				RewardsController.records = recordScore;

				// Read the second line that corresponds to the previous comparison score, and set it
				// to be the current compare score to continue progress
				int compareScore = scanner.nextInt();
				RewardsController.compares = compareScore;

				// Read the third line that corresponds to the previous special score, and set it
				// to be the current special score to continue progress
				int specialScore = scanner.nextInt();
				RewardsController.special = specialScore;


				scanner.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

	}


}



