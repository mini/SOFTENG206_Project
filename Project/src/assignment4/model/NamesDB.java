package assignment4.model;

import static assignment4.NameSayerApp.ROOT_DIR;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import assignment4.NameSayerApp;

/**
 * -- NamesDB Class --
 *
 * NamesDB is a class to handle loading and fetching name objects that are populated within the lists of the NameSayer
 * application.
 *
 */
public class NamesDB {
	public static final URI BQ_FILE = new File(ROOT_DIR + "bad_quality.txt").toURI();
	public static final URI TEMP_BQ_FILE = new File(ROOT_DIR + "bad_quality.tmp.txt").toURI();

	private ArrayList<Name> names;

	/**
	 * Create a new database, will automatically load existing names
	 */
	public NamesDB() {
		try {
			populateDB();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Adds the name to the database
	 * 
	 * @param name
	 */
	public void addName(Name name) {
		names.add(name);
		Collections.sort(names);
	}

	/**
	 * Removes the name form the database and deletes related files
	 * 
	 * @param name
	 */
	public void deleteName(Name name) {
		name.deleteAll();
		names.remove(name);
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
	 * @param prefix search criteria
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
	 * Return the name object with the specified name.
	 * 
	 * @param name
	 * @return the name if found, null otherwise
	 */
	public Name getName(String name) {
		for (Name n : names) {
			if (n.getName().equalsIgnoreCase(name)) {
				return n;
			}
		}
		return null;
	}

	/**
	 * Loads all the pre-made names, marks any bad files and load any existing user attempts of a name.
	 */
	public void populateDB() throws IOException {
		// Read entries from bad_quality file
		List<String> badQualityFiles = new ArrayList<String>();
		try {
			badQualityFiles = Files.readAllLines(Paths.get(BQ_FILE), Charset.forName("UTF8"));
		} catch (IOException e) {
			// BQ file does not exist
		}

		File[] files = new File(NameSayerApp.ROOT_DIR + "names/").listFiles((dir, name) -> {
			return name.endsWith(".wav");
		});
		Map<String, Name> names = new HashMap<String, Name>();

		for (File file : files) {
			// Check if any name versions are bad
			String filename = file.getName();
			boolean badQuality = false;

			for (String badFilename : badQualityFiles) {
				if (badFilename.contains(filename)) {
					badQuality = true;
					break;
				}
			}

			// Grab name from filename
			String name;
			int front = filename.lastIndexOf("_") + 1;
			int back = filename.lastIndexOf(".wav");
			name = filename.substring(front, back);
			name = name.substring(0, 1).toUpperCase() + name.substring(1);

			// Add versions to name object
			Name existing = names.get(name);
			if (existing != null) {
				existing.addVersion(filename, badQuality);
			} else {
				existing = new Name(name).addVersion(filename, badQuality);
				names.put(name, existing);
			}
		}
		this.names = new ArrayList<Name>(names.values());
		Collections.sort(this.names);
	}
}
