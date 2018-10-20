package assignment4.model;

import static assignment4.utils.AudioUtils.concatFiles;
import static assignment4.utils.AudioUtils.equaliseVolume;
import static assignment4.utils.AudioUtils.removeSilence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import assignment4.NameSayerApp;
import javafx.util.Callback;

/**
 * Holds multiple Name objects and concatenates them 
 *
 */
public class Combination {

	private List<Name> names;
	private String displayName;
	private String mergedName;
	private String path;

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

	/**
	 * To be called when all desired names have been added.
	 * Generates the final audio file.
	 */
	public void process(NamesDB db, Callback<Boolean, Void> callback) {
		final String mergedName = displayName.replaceAll("\\s|-", "").toLowerCase();
		this.mergedName = mergedName;
		path = new File(NameSayerApp.ROOT_DIR + "temp/merged/" + mergedName + ".wav").toURI().toString();

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
				success &= concatFiles(mergedName + ".txt", mergedName);
				callback.call(success);
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
