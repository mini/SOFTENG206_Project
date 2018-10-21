package assignment4.model;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Scanner;

import assignment4.NameSayerApp;
import assignment4.ui.BaseController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Data class to hold achivement progress 
 */
public class AchievementStats {
	private static final File ACHIEVEMENT_FILE = new File(NameSayerApp.ROOT_DIR + "achievements.txt");

	public enum SpecialFeature {
		MICROPHONE, ADDNAMES, CATHERINEWATSON;
	}

	private int records, compares, special;
	private int microphoneCheck, namesCheck, catherineCheck;
	
	public AchievementStats() {
		load();
	}

	public void incrementRecords() {
		records++;
		save();

		if (records == 10 || records == 25 || records == 50) {
			notification();
		}
	}

	public void incrementCompares() {
		compares++;
		save();

		if (compares == 5 || compares == 15 || compares == 30) {
			notification();
		}
	}

	public void incrementSpecial(SpecialFeature feature) {
		disableSpecial(feature);
	}

	private void disableSpecial(SpecialFeature feature) {
		if (feature == SpecialFeature.MICROPHONE && microphoneCheck == 0) {
			notification();
			special++;
			microphoneCheck = 1;
			save();
		}

		if (feature == SpecialFeature.ADDNAMES && namesCheck == 0) {
			notification();
			special++;
			namesCheck = 1;
			save();
		}

		if (feature == SpecialFeature.CATHERINEWATSON && catherineCheck == 0) {
			notification();
			special++;
			catherineCheck = 1;
			save();
		}
	}

	public int getRecords() {
		return records;
	}

	public int getCompares() {
		return compares;
	}

	public int getSpecial() {
		return special;
	}

	public void notification() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxmls/PopUpAchievement.fxml"));
			Scene scene = new Scene(loader.load());

			BaseController controller = loader.getController();

			Stage stage = new Stage();
			stage.setAlwaysOnTop(true);
			stage.initModality(Modality.APPLICATION_MODAL);

			// Pass data to child controllers
			stage.setScene(scene);
			stage.setResizable(false);
			controller.init();

			stage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes current progress to file, each progress value is on its own line.
	 */
	public void save() {
		String output = String.format("%d\n%d\n%d\n%d\n%d\n%d", records, compares, special, microphoneCheck, namesCheck, catherineCheck);
		try {
			Files.write(Paths.get(ACHIEVEMENT_FILE.toURI()), output.getBytes("UTF8"), CREATE, TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads the previous attempt of progress in terms of achievements and recording, comparing and special scores. This
	 * relates to the rewards page.
	 */
	private void load() {
		// If there has been a previous score, apply the scores to continue progress. Otherwise, start fresh.
		if (ACHIEVEMENT_FILE.exists()) {
			try {
				Scanner scanner = new Scanner(ACHIEVEMENT_FILE);

				records = scanner.nextInt();
				compares = scanner.nextInt();
				special = scanner.nextInt();
				microphoneCheck = scanner.nextInt();
				namesCheck = scanner.nextInt();
				catherineCheck = scanner.nextInt();

				scanner.close();
			} catch (NoSuchElementException | FileNotFoundException e) {
				// Checked, should never occur
			}
		}
	}
}
