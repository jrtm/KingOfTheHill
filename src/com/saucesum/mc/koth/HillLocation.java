package com.saucesum.mc.koth;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class HillLocation implements Serializable {
	private static final long serialVersionUID = 1965790575438655355L;

	private final String world;
	private final int x;
	private final int y;
	private final int z;

	private transient Location location;

	public HillLocation(Location loc) {
		this.world = loc.getWorld().getName();
		this.x = loc.getBlockX();
		this.y = loc.getBlockY();
		this.z = loc.getBlockZ();

		this.location = loc;
	}

	public HillLocation(String world, int x, int y, int z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		HillLocation other = (HillLocation) obj;

		return other.x == x && other.y == y && other.z == z
				&& other.world.equals(world);
	}

	@Override
	public int hashCode() {
		int hash = 7;

		hash = 31 * hash + x;
		hash = 31 * hash + y;
		hash = 31 * hash + z;
		hash = 31 * hash + world.hashCode();

		return hash;
	}

	public String getWorld() {
		return world;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public Location getLocation() {
		return location == null ? (location = new Location(
				Bukkit.getWorld(world), x, y, z)) : location;
	}

	@Override
	public String toString() {
		return "HillLocation[" + world + ", " + x + ", " + y + ", " + z + "]";
	}

}
