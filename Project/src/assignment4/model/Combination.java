package assignment4.model;

import static assignment4.NameSayerApp.ROOT_DIR;
import static assignment4.utils.AudioUtils.concatFiles;
import static assignment4.utils.AudioUtils.equaliseVolume;
import static assignment4.utils.AudioUtils.removeSilence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javafx.util.Callback;

/**
 * Holds multiple Name objects and concatenates them
 *
 */
public class Combination {
	private static ThreadPoolExecutor executor;

	static {
		int threads = Math.max(2, Runtime.getRuntime().availableProcessors() - 2);
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads, runnable -> {
			Thread thread = new Thread(runnable);
			thread.setDaemon(true);
			return thread;
		});
	}

	private List<Name> names;
	private String displayName;
	private String mergedName;
	private File finalFile;

	/**
	 * @param displayName what to show on the listview
	 */
	public Combination(String displayName) {
		this.names = new ArrayList<Name>();
		final String mergedName = displayName.replaceAll("\\s|-", "").toLowerCase();
		this.mergedName = mergedName;
		this.displayName = displayName;
	}

	/**
	 * Adds a name to the combination
	 * 
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

	/**
	 * @return path the the final audio file
	 */
	public String getPath() {
		return finalFile.toURI().toString();
	}

	/**
	 * To be called when all desired names have been added. Generates the final audio file.
	 */
	public void process(NamesDB db, Callback<Boolean, Void> callback) {
		executor.submit(() -> {
			boolean success = true;

			// Proccess individual files and add to txt file
			String fileName = null;
			for (Name name : names) {
				fileName = name.getBestVersion().getAudioFileName();
				if (!removeSilence(fileName) || !equaliseVolume("silenced/" + fileName, "equalised/" + fileName)) { // Use short circuits to prevent propagating errors
					success = false;
					break;
				}
			}

			if (success) {
				if (names.size() > 1) {
					try {
						// Generate input text file for batch ffmpeg call
						BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ROOT_DIR + "temp/" + mergedName + ".txt"), "utf-8"));
						for (Name name : names) {
							fileName = name.getBestVersion().getAudioFileName();
							writer.write("file './equalised/" + fileName + "'");
							writer.newLine();
						}
						writer.close();
						success &= concatFiles(mergedName + ".txt", mergedName);
					} catch (IOException e) {
						success = false;
					}
					finalFile = new File(ROOT_DIR + "temp/merged/" + mergedName + ".wav");
				} else {
					// Don't need to concatenate a single name, so just use the normalised file
					finalFile = new File(ROOT_DIR + "temp/equalised/" + fileName);
				}
			}
			callback.call(success);
		});
	}

	@Override
	public String toString() {
		return displayName;
	}

	/**
	 * @return a set of all the unique names in this combo
	 */
	public HashSet<Name> getNameSet() {
		return new HashSet<Name>(names);
	}
}
