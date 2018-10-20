package assignment4.model;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static assignment4.utils.AudioUtils.*;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import assignment4.NameSayerApp;

/**
 * Holds multiple Name objects and concatenates them 
 *
 */
public class Combination {

	private List<Name> names;
	private String displayName;
	private String mergedName;
	private String path;
	private boolean badQuality;

	/**
	 * @param displayName what to show on the listview
	 */
	public Combination(String displayName) {
		this.names = new ArrayList<Name>();
		this.displayName = displayName;
	}

	/**
	 * Adds a name to the combination
	 * @param name the name to add
	 */
	public Combination addName(Name name) {
		names.add(name);
		return this;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getMergedName() {
		return mergedName;
	}

	public String getPath() {
		return path;
	}

	public boolean isBadQuality() {
		return badQuality;
	}

	/**
	 * Toggles the quality indicator for this combo, compares to a text file.
	 */
	public void toggleBadQuality() {
		badQuality = !badQuality;
		try {
			if (badQuality) {
				// Append to file
				Files.write(Paths.get(NamesDB.BQ_FILE), ("Combo-" + mergedName + System.lineSeparator()).getBytes("UTF8"), CREATE, APPEND);
			} else {
				// Makes a new file with all entries except this one
				File bqFile = new File(NamesDB.BQ_FILE);
				File tmpFile = new File(NamesDB.TEMP_BQ_FILE);

				BufferedReader reader = new BufferedReader(new FileReader(bqFile));
				BufferedWriter writer = new BufferedWriter(new FileWriter(tmpFile));
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.contains("Combo-" + mergedName)) {
						continue;
					}
					writer.write(line + System.getProperty("line.separator"));
				}
				tmpFile.renameTo(bqFile);

				reader.close();
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * To be called when all desired names have been added.
	 * Generates the final audio file.
	 */
	public void process(NamesDB db) {
		final String mergedName = displayName.replaceAll("\\s|-", "").toLowerCase();
		this.mergedName = mergedName;

		badQuality = db.checkBadCombo(this);

		Thread thread = new Thread(() -> {
			try {
				boolean success = true;
				
				// Proccess individual files and add to txt file
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(NameSayerApp.ROOT_DIR + "temp/" + mergedName + ".txt"), "utf-8"));
				for (Name name : names) {
					String fileName = name.getBestVersion().getAudioFileName();
					
					if(!removeSilence(fileName) || !equaliseVolume(fileName)) { // Use short circuits to prevent propagating errors
						success = false;
						break;
					}
					
					writer.write("file './equalised/" + fileName + "'");
					writer.newLine();
				}
				writer.close();
				
				if(success && concatFiles(mergedName + ".txt", mergedName)) {
					path = new File(NameSayerApp.ROOT_DIR + "temp/merged/" + mergedName + ".wav").toURI().toString();
				} else {
					//TODO
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}

		});
		thread.setDaemon(true);
		thread.start();
	}
	
	@Override
	public String toString() {
		return displayName;
	}
}
