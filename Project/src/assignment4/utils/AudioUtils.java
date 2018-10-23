package assignment4.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static assignment4.NameSayerApp.ROOT_DIR;

/**
 * Helper functions that handle audio processing. 
 * 
 * @author Dhruv Phadnis, Vanessa Ciputra
 */
public class AudioUtils {
	private static final boolean DEBUG_OUTPUT = false;

	/**
	 * Removes any silences from the start and end of the specified file
	 * 
	 * @param fileName
	 * @return was successful
	 */
	public static boolean removeSilence(String fileName) {
		if (new File(ROOT_DIR + "temp/silenced/" + fileName).exists()) {
			return true;
		}

		String silence = ("ffmpeg -y -hide_banner -i " + fileName + " -af silenceremove=1:0:-35dB:-1:1:-35dB:1 ../temp/silenced/" + fileName);
		File directory = new File(ROOT_DIR + "names/");

		// Use a process to perform the silence removing
		ProcessBuilder remove = new ProcessBuilder("bash", "-lc", silence);
		remove.redirectErrorStream(true);
		remove.directory(directory);
		try {
			Process pro = remove.start();
			output(pro);
			return pro.waitFor() == 0;
		} catch (IOException | InterruptedException e) {
			return false;
		}
	}

	/**
	 * Normalises the volume of the specified file so that they all have the same levels when concatenated.
	 * 
	 * @param src input file path
	 * @param dest output file path
	 * @return was successful
	 */
	public static boolean equaliseVolume(String src, String dest) {
		if (new File(ROOT_DIR + "temp/" + dest).exists()) {
			return true;
		}

		String eq = ("ffmpeg -y -hide_banner -i " + src + " -af dynaudnorm " + dest);
		File directory = new File(ROOT_DIR + "temp/");

		// Use a process to perform the volume equalising
		ProcessBuilder volume = new ProcessBuilder("bash", "-lc", eq);
		volume.redirectErrorStream(true);
		volume.directory(directory);
		try {
			Process pro = volume.start();
			output(pro);
			return pro.waitFor() == 0;
		} catch (IOException | InterruptedException e) {
			return false;
		}
	}
	
	/**
	 * Concatenate the files defined in inputData.
	 * 
	 * @param inputData  path to a text file containing a list of files
	 * @param mergedName output name
	 * @return was successful
	 */
	public static boolean concatFiles(String inputData, String mergedName) {
		String concat = ("ffmpeg -y -hide_banner -f concat -safe 0 -i " + inputData + " -c copy -acodec pcm_s16le -ar 16000 -ac 1 ./merged/" + mergedName + ".wav");
		File directory = new File(ROOT_DIR + "temp/");

		ProcessBuilder merge = new ProcessBuilder("bash", "-lc", concat);
		merge.redirectErrorStream(true);
		merge.directory(directory);
		try {
			Process pro = merge.start();
			output(pro);
			return pro.waitFor() == 0;
		} catch (IOException | InterruptedException e) {
			return false;
		}
	}

	private static void output(Process p) {
		if (!DEBUG_OUTPUT) {
			return;
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		int i = 0;
		try {
			while ((line = reader.readLine()) != null) {
				System.out.println(++i + ": " + line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
