package assignment4.model;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import assignment4.NameSayerApp;

/**
 * -- Version Class --
 *
 * Version holds data about single media files to check for bad quality files and returns corresponding
 * good quality sound recordings when necessary.
 *
 */
public class Version {
	private String audioFileName;
	private String path;
	private boolean badQuality;
	private String label;

	/**
	 * For existing names
	 * 
	 * @param audioFileName
	 *            media source
	 * @param badQuality
	 *            whether it was marked previously
	 */
	public Version(String audioFileName, boolean badQuality) {
		this.audioFileName = audioFileName;
		this.path = new File(NameSayerApp.ROOT_DIR + "names/" + audioFileName).toURI().toString();
		this.badQuality = badQuality;
	}

	/**
	 * For new user attempts
	 * 
	 * @param audioFile
	 *            media source
	 * @param label
	 *            shown on the list
	 */
	public Version(File audioFile, String label) {
		this.audioFileName = audioFile.getName();
		this.path = audioFile.toURI().toString();
		this.label = label;
	}

	/**
	 * Marks this version as bad quality, writes this to the text file in the program's folder
	 */
	public void toggleBadQuality() {
		badQuality = !badQuality;

		try {
			if(badQuality) {
				// Append to file
				Files.write(Paths.get(NamesDB.BQ_FILE), (audioFileName + System.lineSeparator()).getBytes("UTF8"), CREATE, APPEND);
			} else {
				// Makes a new file with all entries except this one
				File bqFile = new File(NamesDB.BQ_FILE);
				File tmpFile = new File(NamesDB.TEMP_BQ_FILE);
				
				BufferedReader reader = new BufferedReader(new FileReader(bqFile));
				BufferedWriter writer = new BufferedWriter(new FileWriter(tmpFile));
				String line;
				while((line = reader.readLine()) != null) {
					if(line.trim().contains(audioFileName)) {
						continue;
					}
				    writer.write(line + System.lineSeparator());
				}
				tmpFile.renameTo(bqFile);
				
				reader.close();
				writer.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isBadQuality() {
		return badQuality;
	}
	
	public String getPath() {
		return path;
	}

	public String getAudioFileName() {
		return audioFileName;
	}
	
	public String getLabel() {
		return label;
	}

	/**
	 * Deletes the versions media file from disk
	 */
	public void deleteFile() {
		try {
			new File(new URI(path)).delete();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "Version(" + audioFileName + ") isBadQuality: " + badQuality;
	}

}
