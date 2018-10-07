package assignment4.model;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

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
	public void addName(Name name) {
		names.add(name);
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
	 * Toggles the quality indicator for this combo, saves to a text file.
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
				processIndividual();

				String concat = ("ffmpeg -y -f concat -safe 0 -i " + mergedName + ".txt -c copy -acodec pcm_s16le -ar 16000 -ac 1 ./merged/" + mergedName + ".wav");
				File directory = new File(NameSayerApp.ROOT_DIR + "temp/");
				ProcessBuilder merge = new ProcessBuilder("bash", "-lc", concat);
				merge.directory(directory);
				Process pro = merge.start();
				if (pro.waitFor() == 0) {
					path = new File(NameSayerApp.ROOT_DIR + "temp/merged/" + mergedName + ".wav").toURI().toString();
				} 
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}

		});
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Processes individual name files.
	 * Adds the path to a text file which is used later
	 */
	public void processIndividual() throws IOException, InterruptedException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(NameSayerApp.ROOT_DIR + "temp/" + mergedName + ".txt"), "utf-8"));
		for (Name name : names) {
			String fileName = name.getBestVersion().getAudioFileName();
			removeSilence(fileName);
			equaliseVolume(fileName);
			writer.write("file './equalised/" + fileName + "'");
			writer.newLine();
		}
		writer.close();
	}

	/**
	 * Removes any silences from the start and end of the specified file
	 * @param fileName
	 */
	public void removeSilence(String fileName) throws IOException, InterruptedException {
		String silence = ("ffmpeg -y -hide_banner -i " + fileName + " -af silenceremove=0:0:0:-1:1:-50dB:1 ../temp/silenced/" + fileName);
		File directory = new File(NameSayerApp.ROOT_DIR + "names/");
		ProcessBuilder remove = new ProcessBuilder("bash", "-lc", silence);
		remove.directory(directory);
		Process pro = remove.start();
		pro.waitFor();
	}

	/**
	 * Normalises the volume of the specified file so that they all have the same levels when concatenated. 
	 * @param fileName
	 */
	public void equaliseVolume(String fileName) throws IOException, InterruptedException {
		String eq = ("ffmpeg -y -i " + fileName + " -af dynaudnorm ../equalised/" + fileName);
		File directory = new File(NameSayerApp.ROOT_DIR + "temp/silenced/");
		ProcessBuilder volume = new ProcessBuilder("bash", "-lc", eq);
		volume.directory(directory);
		Process pro = volume.start();
		pro.waitFor();
	}
}
