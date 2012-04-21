package com.saucesum.mc.koth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * 
 * Hill manager
 * 
 * @author Ravn T-M
 * 
 */
public class Hills {

	private static final String KOTH_NAME_METADATA = "koth.name";

	private final File hillsFile;

	private KoTH koth;

	private Hill[] hills;

	public Hills(KoTH koth, File hillsFile) {
		this.koth = koth;
		this.hillsFile = hillsFile;

		this.hills = new Hill[] {};
	}

	/**
	 * Generates new hills randomly from the specified HillLocations list
	 * 
	 * @param locations
	 *            The list of where hills can be generated
	 * @param num
	 *            The maximum number of hills to generate
	 */
	public synchronized void generate(HillLocations locations, int num) {
		clearHills();

		if (locations.size() < num) {
			num = locations.size();
		} else {
			locations.shuffle();
		}

		Hill[] hills = new Hill[num];

		for (int i = 0; i < num; i++) {
			hills[i] = new Hill(getHillName(i), locations.get(i));
			initHill(hills[i]);
		}

		this.hills = hills;
	}

	private String getHillName(int i) {
		return Character.toString((char) (i + 'A'));
	}

	/**
	 * Uninitializes all hills
	 */
	public synchronized void clearHills() {
		if (!isGenerated()) {
			return;
		}
		for (int i = 0; i < hills.length; i++) {
			Hill hill = hills[i];
			uninitHill(hill);
		}
		this.hills = new Hill[] {};
	}

	/**
	 * Uninitializes a hill by removing its underlying Block
	 */
	private void uninitHill(Hill hill) {
		hill.updateBlock();

		Block block = hill.getBlock();
		block.setType(Material.AIR);
		block.removeMetadata(KOTH_NAME_METADATA, koth);
	}

	/**
	 * Initializes a hill by setting its underlying Block
	 */
	private void initHill(Hill hill) {
		hill.updateBlock();

		Block block = hill.getBlock();
		block.setType(KoTHConf.hillType);

		// If the hills are represented by signs, add some text
		if (KoTHConf.hillType == Material.SIGN
				|| KoTHConf.hillType == Material.SIGN_POST) {
			Sign sign = (Sign) block.getState();
			sign.setLine(0, "== KoTH ==");
			sign.setLine(1, hill.getName());
			sign.setLine(3, "(right click)");
			sign.update();
		}

		block.setMetadata(KOTH_NAME_METADATA,
				new FixedMetadataValue(koth, hill.getName()));
	}

	/**
	 * Returns whether there are generated hills available
	 */
	public boolean isGenerated() {
		return hills != null && hills.length > 0;
	}

	/**
	 * Loads the persistent hill data from file
	 */
	public synchronized void load() {
		try {
			ObjectInputStream reader = new ObjectInputStream(
					new FileInputStream(hillsFile));
			hills = (Hill[]) reader.readObject();
			initHillBlocks();
			reader.close();

			KoTH.info("Loaded " + (hills == null ? 0 : hills.length) + " hills");
		} catch (Exception ioe) {
			KoTH.info("Could not load hill data: " + ioe);
		} finally {
			if (hills == null) {
				hills = new Hill[] {};
			}
		}
	}

	/**
	 * Saves the persistent hill data to file
	 */
	public synchronized void save() {
		try {
			ObjectOutputStream writer = new ObjectOutputStream(
					new FileOutputStream(hillsFile, false));
			writer.writeObject(hills);
			writer.close();
		} catch (IOException ioe) {
			KoTH.warn("Could not save hill data: " + ioe);
		}
	}

	/**
	 * Initializes all hills
	 */
	private void initHillBlocks() {
		for (int i = 0; i < hills.length; i++) {
			initHill(hills[i]);
		}
	}

	/**
	 * Returns all generated hills available
	 */
	public synchronized Hill[] getAll() {
		return hills;
	}

	/**
	 * Returns the Hill associated with the specified Block, or null if no such
	 * hill exists.
	 */
	public synchronized Hill getHillByBlock(Block block) {
		if (!isGenerated() || block.getType() != KoTHConf.hillType) {
			return null;
		}

		for (int i = 0; i < hills.length; i++) {
			Hill hill = hills[i];
			if (isBlock(block, hill)) {
				return hill;
			}
		}
		return null;
	}

	private boolean isBlock(Block block, Hill hill) {
		Location a = block.getLocation();
		Location b = hill.getLocation();
		return a.getBlockX() == b.getBlockX() && a.getBlockY() == b.getBlockY()
				&& a.getBlockZ() == b.getBlockZ()
				&& a.getWorld().getName().equals(b.getWorld().getName());

	}

}
