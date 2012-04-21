package com.saucesum.mc.koth;

import java.io.Serializable;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class Hill implements Serializable {
	private static final long serialVersionUID = 457129825963285778L;

	private final HillLocation location; 

	private final     String name;
	private volatile  String king;

	private transient Block block;

	public Hill(String name, HillLocation location) {
		this.name = name;
		this.king = null;

		this.location = location;
		updateBlock();
	}

	public void updateBlock() {
		this.block = location.getLocation().getBlock();
	}

	public HillLocation getHillLocation() {
		return location;
	}

	public Location getLocation() {
		return location.getLocation();
	}

	public Chunk getChunk() {
		return block.getChunk();
	}

	public Block getBlock() {
		return block;
	}

	public synchronized String getKing() {
		return king;
	}

	public synchronized void setKing(String newKing) {
		this.king = newKing;
	}

	public synchronized boolean hasKing() {
		return this.king != null;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		if (hasKing()) {
			return "Hill[" + name + " @ " + block.getX() + "X " + block.getY() + "Y " + block.getZ() + "Z, " + king + "]";
		} else {
			return "Hill[" + name + " @ " + block.getX() + "X " + block.getY() + "Y " + block.getZ() + "Z]";
		}
	}

}
