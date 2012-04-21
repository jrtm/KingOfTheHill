package com.saucesum.mc.koth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;

public class HillLocations {

	private final File locationsFile;

	private List<HillLocation> locations;

	public HillLocations(File locationsFile) {
		this.locationsFile = locationsFile;
		locations = Collections.synchronizedList(new ArrayList<HillLocation>());
	}


	@SuppressWarnings("unchecked")
	public void load() {
		try {
			ObjectInputStream reader = new ObjectInputStream(
					new FileInputStream(locationsFile));
			locations = (List<HillLocation>) reader.readObject();
			reader.close();

			KoTH.info("Loaded "
					+ (locations == null ? 0 : locations.size())
					+ " hill locations");
		} catch (Exception ioe) {
			KoTH.info("Could not load hill location data: " + ioe);
		} finally {
			if (locations == null) {
				locations = new ArrayList<HillLocation>();
			}
		}
	}

	public void save() {
		try {
			ObjectOutputStream writer = new ObjectOutputStream(
					new FileOutputStream(locationsFile, false));
			writer.writeObject(locations);
			writer.close();
		} catch (IOException ioe) {
			KoTH.warn("Could not save hill location data: " + ioe);
		}
	}

	public List<HillLocation> getAll() {
		return locations;
	}

	public int size() {
		return locations.size();
	}

	public boolean isEmpty() {
		return locations.isEmpty();
	}

	public void shuffle() {
		Collections.shuffle(locations);
	}

	public HillLocation get(int idx) {
		return locations.get(idx);
	}

	public void add(Location loc) {
		HillLocation hill = new HillLocation(loc);
		locations.add(hill);
	}

	public boolean remove(Location loc) {
		int size = size();
		removeHillsInChunk(loc.getChunk());

		// returns whether it actually removed anything
		return size != size();
	}

	public void clear() {
		locations.clear();
	}

	public void removeHillsInChunk(Chunk chunk) {
		for (int i = 0; i < locations.size(); i++) {
			HillLocation hill = locations.get(i);
			if (isInChunk(hill.getLocation(), chunk)) {
				locations.remove(i--);
			}
		}
	}

	private boolean isInChunk(Location location, Chunk chunk) {
		return location.getChunk().equals(chunk);
	}

}
