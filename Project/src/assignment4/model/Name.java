package assignment4.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import assignment4.NameSayerApp;

/**
 * -- Name Class --
 *
 * Name handles all corresponding methods to deal with the names in the 
 * database and their corresponding versions.
 *
 */
public class Name implements Comparable<Name> {
	private static final Random random = new Random();

	private final String name;
	private final List<Version> versions;

	/**
	 * @param name the name
	 */
	public Name(String name) {
		versions = new ArrayList<Version>();
		this.name = name;
	}

	/**
	 * Returns the first version of good quality, if there are none then the latest version.
	 * 
	 * @return the best version
	 */
	public Version getBestVersion() {
		for (Version version : versions) {
			if (!version.isBadQuality()) {
				return version;
			}
		}

		return versions.get(random.nextInt(versions.size()));
	}

	/**
	 * Adds a version to this name.
	 * 
	 * @param audioFileName source media file
	 * @param badQuality if version was already marked bad
	 * @return this for chaining
	 */
	public Name addVersion(String audioFileName, boolean badQuality) {
		versions.add(new Version(audioFileName, badQuality));
		return this;
	}

	/**
	 * Deletes all files related to the name.
	 */
	void deleteAll() {
		new File(NameSayerApp.ROOT_DIR + "attempts/" + name).delete();
		for (Version version : versions) {
			version.deleteFile();
		}
	}

	public String getName() {
		return name;
	}

	/*
	 * For use with comparators
	 */
	@Override
	public int compareTo(Name other) {
		return name.compareTo(other.getName());
	}

	@Override
	public String toString() {
		return String.format("Name(\"%s\") Versions: %d", name, versions.size());
	}
}
