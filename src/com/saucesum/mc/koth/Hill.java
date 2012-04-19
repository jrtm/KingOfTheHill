package com.saucesum.mc.koth;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class Hill {
	private final     Block hillBlock;

	private final     String name;
	private transient String king;

	public Hill(String name, Block b) {
		this.name = name;
		this.king = null;

		this.hillBlock = b;
	}

	public Location getLocation() {
		return hillBlock.getLocation();
	}

	public Chunk getChunk() {
		return hillBlock.getChunk();
	}

	public Block getBlock() {
		return hillBlock;
	}

	public String getKing() {
		return king;
	}

	public void setKing(String newKing) {
		this.king = newKing;
	}

	public boolean hasKing() {
		return this.king != null;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		if (hasKing()) {
			return "Hill[" + name + " @ " + hillBlock.getX() + "X " + hillBlock.getY() + "Y " + hillBlock.getZ() + "Z, " + king + "]";
		} else {
			return "Hill[" + name + " @ " + hillBlock.getX() + "X " + hillBlock.getY() + "Y " + hillBlock.getZ() + "Z]";
		}
	}

}
