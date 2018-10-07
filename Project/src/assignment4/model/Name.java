package assignment4.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import assignment4.NameSayerApp;

/**
 * -- Name Class --
 *
 * Name handles all corresponding methods to deal with the names being practiced, and
 * versions that are being created due to several attempts of the same name. This allows
 * encapsulated access to these methods throughout the entire application.
 *
 */
public class Name implements Comparable<Name> {
	private static final Random random = new Random();

	private Version lastVersion;
	
	private final String name;
	private final List<Version> versions;
	private final ObservableList<Version> attempts;

	private boolean selected;
	private SimpleStringProperty playing;

	/**
	 * @param name
	 *            the name
	 */
	public Name(String name) {
		playing = new SimpleStringProperty(" ");
		versions = new ArrayList<Version>();
		attempts = FXCollections.observableArrayList();
		this.name = name;
	}

	/**
	 * Returns the first version of good quality, if there are none then the latest version.
	 * 
	 * @return the best version
	 */
	public Version getBestVersion() {
		lastVersion = null;
		for (Version version : versions) {
			if (!version.isBadQuality()) {
				lastVersion = version;
			}
		}
		
		if(lastVersion == null) {
			lastVersion = versions.get(random.nextInt(versions.size()));
		}
				
		return lastVersion;
	}

	/**
	 * Gets the best version that was previously returned
	 * @see #getBestVersion()
	 */
	public Version getLastVersion() { //TODO remove this, it was a hack to fix a bug
		if(lastVersion == null) {
			getBestVersion();
		}
		return lastVersion;
	}
	
	/**
	 * Adds a version to this name.
	 * 
	 * @param audioFileName
	 *            source media file
	 * @param badQuality
	 *            if version was already marked bad
	 * @return this for chaining
	 */
	public Name addVersion(String audioFileName, boolean badQuality) {
		versions.add(new Version(audioFileName, badQuality));
		return this;
	}

	/**
	 * Adds a attempt to this name
	 * 
	 * @param file
	 *            source media file
	 * @param label
	 *            showed on table
	 */
	public void addAttempt(File file, String label) {
		if ("Unsaved Attempt".equals(label)) { // If it's the temporary attempt add it to the top
			attempts.add(0, new Version(file, label));
		} else {
			attempts.add(new Version(file, label));
		}
	}

	/**
	 * Removes the temporary attempt if it exists.
	 */
	public void removeTemp() {
		if (!attempts.isEmpty() && "Unsaved Attempt".equals(attempts.get(0).getLabel())) {
			attempts.remove(0);
		}
	}

	/**
	 * Removes the given attempt from this name, will delete it's file from disk.
	 * 
	 * @param version
	 *            the version to delete
	 */
	public void removeAttempt(Version version) {
		if (attempts.remove(version)) {
			version.deleteFile();
		}
	}
	
	/**
	 * Deletes all files related to the name.
	 */
	public void deleteAll() {
		new File(NameSayerApp.ROOT_DIR + "attempts/" + name).delete();
		for(Version version: versions) {
			version.deleteFile();
		}
	}


	public String getName() {
		return name;
	}

	public SimpleStringProperty getPlayingProperty() {
		return playing;
	}

	public ObservableList<Version> getAttempts() {
		return attempts;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
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
