package assignment4;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import assignment4.model.NamesDB;
import assignment4.ui.BaseController;
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
 */
public class NameSayerApp extends Application {
	public static final String ROOT_DIR = System.getProperty("user.home") + File.separator + "NameSayer/";

	/**
	 * The main entry point to the NameSayer application
	 */
	public static void main(String[] args) {

		// Create the required directorys
		new File(ROOT_DIR + "attempts/").mkdirs();
		new File(ROOT_DIR + "temp/silenced/").mkdirs();
		new File(ROOT_DIR + "temp/equalised/").mkdirs();
		new File(ROOT_DIR + "temp/merged/").mkdirs();
		// new File(ROOT_DIR + "temp/").deleteOnExit();
		
		if (!new File(ROOT_DIR + "names/").exists()) {
			try {
				unzip("/resources/nameFiles.zip", ROOT_DIR);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Starts the application
		launch(args);
	}

	/**
	 * Creates the window and set the scene to the main menu
	 */
	public void start(Stage primaryStage) throws IOException {
		NamesDB namesDB = new NamesDB();

		primaryStage.setTitle("NameSayer");
		// Prevent the stage from being resized by the user
		primaryStage.setResizable(false);

		// Load the scene of the Player fxml file
		// Microphone icon was made by https://www.flaticon.com/authors/prosymbols from
		// www.flaticon.com
		FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/resources/MainMenu.fxml"));
		Parent menuPane = menuLoader.load();
		BaseController controller = menuLoader.getController();

		// Passes in the current stage to be used as the main stage for all following
		// windows
		controller.setup(primaryStage, namesDB);
		controller.init();

		// Add the scene of the main menu
		Scene menuScene = new Scene(menuPane);
		primaryStage.setScene(menuScene);
		primaryStage.show();

	}

	private static void unzip(String zipFilePath, String destDirectory) throws IOException {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		ZipInputStream zipIn = new ZipInputStream(NameSayerApp.class.getResourceAsStream(zipFilePath));

		ZipEntry entry = zipIn.getNextEntry();
		// iterates over entries in the zip file
		while (entry != null) {
			String filePath = destDirectory + entry.getName();
			if (!entry.isDirectory()) {
				new File(filePath).getParentFile().mkdirs();
				extractFile(zipIn, filePath);
			} else {
				File dir = new File(filePath);
				dir.mkdirs();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}

	private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		byte[] bytesIn = new byte[4 * 1024];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}
}
