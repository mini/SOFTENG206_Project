package assignment4.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import assignment4.NameSayerApp;

public class Combination {

	private List<Name> names;
	private String combinedName;
	private String mergedName;
	
	private String path;

	public Combination() {
		this.names = new ArrayList<Name>();		
	}

	public void addName(Name name) {
		names.add(name);
	}
	
	public String getCombinedName() {
		return combinedName;
	}
	
	public String getMergedName() {
		return mergedName;
	}
	
	public String getPath() {
		return path;
	}

	public void process() {
		combinedName = "";
		for (Name name : names) {
			combinedName += name.getName() + " ";
		}
		combinedName.trim();
		final String mergedName = combinedName.replaceAll("\\s","");
		this.mergedName = mergedName;
		
		Thread thread = new Thread(() -> {
			try {
				// Create the text file to concatenate all the names in the string
				createCombinedNameFile();

				// Use a process to concatenate the separate wav files into one file
				String concat = ("ffmpeg -y -f concat -safe 0 -i " + mergedName + ".txt -c copy -acodec pcm_s16le -ar 16000 -ac 1 ./merged/" + mergedName + ".wav");
				File directory = new File(NameSayerApp.ROOT_DIR + "temp/");
				ProcessBuilder merge = new ProcessBuilder("bash", "-lc", concat);
				merge.directory(directory);
				Process pro = merge.start();
				if(pro.waitFor() == 0) {
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
	 * Separates the line of the text file to obtain the separate name files to concatenate by creating a new text file with
	 * the correct format to concatenate the wav files
	 * 
	 * @param disjointName
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void createCombinedNameFile() throws IOException, InterruptedException {
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(NameSayerApp.ROOT_DIR + "temp/" + mergedName + ".txt"), "utf-8"));
		for (Name name : names) {
			String fileName = name.getBestVersion().getAudioFileName();
			removeSilence(fileName);
			equaliseVolume(fileName);
			writer.write("file './equalised/" + fileName + "'");
			((BufferedWriter) writer).newLine();
		}

		writer.close();
	}

	public void removeSilence(String fileName) throws IOException, InterruptedException {
		String silence = ("ffmpeg -n -hide_banner -i " + fileName + " -af silenceremove=1:0:-35dB:1:5:-35dB:0:peak ../temp/silenced/" + fileName);

		File directory = new File(NameSayerApp.ROOT_DIR + "names/");
		ProcessBuilder remove = new ProcessBuilder("bash", "-lc", silence);
		remove.directory(directory);
		Process pro = remove.start();
		pro.waitFor();
	}

	public void equaliseVolume(String fileName) throws IOException, InterruptedException {
		String eq = ("ffmpeg -n -i " + fileName + " -filter:a loudnorm ../equalised/" + fileName);
		File directory = new File(NameSayerApp.ROOT_DIR + "temp/silenced/");
		ProcessBuilder volume = new ProcessBuilder("bash", "-lc", eq);
		volume.directory(directory);
		Process pro = volume.start();
		pro.waitFor();
	}

}
