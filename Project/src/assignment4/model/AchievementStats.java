package assignment4.model;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Scanner;

import assignment4.NameSayerApp;

public class AchievementStats {
	private static final File ACHIEVEMET_FILE = new File(NameSayerApp.ROOT_DIR + "achievements.txt");

	private int records, compares, special;

	public AchievementStats() {
		load();
	}

	public void incrementRecords() {
		records++;
		save();
	}

	public void incrementCompares() {
		compares++;
		save();
	}

	public void incrementSpecial() {
		special++;
		save();
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

	/**
	 * Writes current progress to file
	 */
	public void save() {
		String output = String.format("%d\n%d\n%d\n", records, compares, special);
		try {
			Files.write(Paths.get(ACHIEVEMET_FILE.toURI()), output.getBytes("UTF8"), CREATE, TRUNCATE_EXISTING);
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
		if (ACHIEVEMET_FILE.exists()) {
			try {
				Scanner scanner = new Scanner(ACHIEVEMET_FILE);

				records = scanner.nextInt();
				compares = scanner.nextInt();
				special = scanner.nextInt();

				scanner.close();
			} catch (NoSuchElementException e) {
				records = 0;
				compares = 0;
				special = 0;
			} catch (FileNotFoundException e) {
				// Checked, should never occur
			}
		}
	}
}
