package assignment4.model;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static assignment4.NameSayerApp.ROOT_DIR;

/**
 * -- NamesDB Class --
 *
 * NamesDB is a class to handle loading and fetching name objects that are populated within the lists of the
 * NameSayer application.
 *
 */
public class NamesDB {
	public static final URI BQ_FILE = new File(ROOT_DIR + "/bad_quality.txt").toURI();
	public static final URI TEMP_BQ_FILE = new File(ROOT_DIR + "/bad_quality.tmp.txt").toURI();

	private Random random;

	private ArrayList<Name> names;

	/**
	 * Create a new database, will automatically load existing names
	 */
	public NamesDB() {
		random = new Random();
		try {
			populateDB();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * @return all the loaded names
	 */
	public ArrayList<Name> getAllNames() {
		return getNames("");
	}

	/**
	 * Searches for names that begin with the given prefix.
	 * 
	 * @param prefix
	 *            search criteria
	 * @return found names
	 */
	public ArrayList<Name> getNames(String prefix) {
		ArrayList<Name> filtered = new ArrayList<Name>();
		for (Name name : names) {
			if (name.getName().toLowerCase().startsWith(prefix.toLowerCase())) {
				filtered.add(name);
			}
		}
		return filtered;
	}

	/**
	 * Grabs the next selected name in the list after the given name. Will loop back to the top if none were found below.
	 * 
	 * @param after
	 *            the current name
	 * @return chosen name
	 */
	public Name getNextSelected(Name after) {
		boolean foundAfter = false;

		if (after == null) {
			foundAfter = true;
		}

		for (int i = 0; i < names.size() * 2; i++) {
			Name name = names.get(i % names.size());

			if (foundAfter && name.isSelected()) {
				return name;
			}

			foundAfter |= name == after;
		}
		return null;
	}

	/**
	 * Randomly grabs a selected name from the set
	 * 
	 * @return chosen name
	 */
	public Name getRandSelected() {
		ArrayList<Name> selected = new ArrayList<Name>();
		for (Name name : names) {
			if (name.isSelected()) {
				selected.add(name);
			}
		}
		return selected.get(random.nextInt(selected.size()));
	}

	/**
	 * Counts how many names have been selected.
	 * 
	 * @return count
	 */
	public int getNumSelected() {
		int count = 0;
		for (Name name : names) {
			if (name.isSelected()) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Loads all the pre-made names, marks any bad files and load any existing user attempts of a name.
	 */
	private void populateDB() throws IOException {
		List<String> badQualityFiles = null;
		try {
			badQualityFiles = Files.readAllLines(Paths.get(BQ_FILE), Charset.forName("UTF8"));
		} catch (IOException e) {
			System.out.println("BQ file does not exist");
		}

		Map<String, Name> names = new HashMap<String, Name>();
		InputStream list = getClass().getResourceAsStream("/resources/names/list.txt");
		BufferedReader files = new BufferedReader(new InputStreamReader(list));

		for (String filename = files.readLine(); filename != null; filename = files.readLine()) {
			// Check if any versions are bad
			boolean badQuality = false;

			if (badQualityFiles != null) {
				for (String badFilename : badQualityFiles) {
					if (badFilename.contains(filename)) {
						badQuality = true;
						break;
					}
				}
			}

			// Grab name from filename
			String[] sections = filename.split("_"); // TODO Switch to regex matcher
			String name = sections[3].split("\\.")[0];
			name = name.substring(0, 1).toUpperCase() + name.substring(1);

			// Add versions to name object
			Name existing = names.get(name);
			if (existing != null) {
				existing.addVersion(filename, badQuality);
			} else {
				existing = new Name(name).addVersion(filename, badQuality);
				names.put(name, existing);
			}

			// Add any user attempts to name object
			File nameDir = new File(ROOT_DIR + "/attempts/" + name);
			if (nameDir.exists() && nameDir.isDirectory()) {
				for (File attempt : nameDir.listFiles(new WavFileFilter())) {
					String attemptName = attempt.getName();
					// Get datetime from parsing the file
					attemptName = attemptName.substring(0, attemptName.length() - name.length() - 5);
					String[] halves = attemptName.split("_");
					halves[0] = halves[0].replace("-", "/");
					halves[1] = halves[1].replace("-", ":");
					existing.addAttempt(attempt, halves[0] + " " + halves[1]);
				}
			}
		}
		this.names = new ArrayList<Name>(names.values());
		Collections.sort(this.names);
	}
}

/**
 * -- WavFileFilter Class --
 *
 * WavFileFilter filters the wav file directory listings for iteration of file names.
 *
 */
class WavFileFilter implements FilenameFilter {
	@Override
	public boolean accept(File dir, String name) {
		return name.endsWith(".wav") && !name.equals("temp.wav");
	}
}
