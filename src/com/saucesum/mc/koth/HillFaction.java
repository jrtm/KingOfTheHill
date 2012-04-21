package com.saucesum.mc.koth;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

public class HillFaction {

	private String name;

	private Faction faction;

	public HillFaction(String name) {
		this.name = name;
	}

	public boolean create() {
		if (isCreated()) {
			if (!isConfigured()) {
				KoTH.warn("A faction named \"" + name
						+ "\" already exists!");
				return false;
			}
			return true;
		}
		Faction faction = Factions.i.create(name);
		if (faction == null) {
			KoTH.warn("Could not create faction \"" + name + "\"");
			return false;
		}
		faction.setPermanent(true);
		faction.setDescription("King of The Hill");
		faction.setTag(name);
		faction.setOpen(false);
		faction.setPermanentPower(Integer.MAX_VALUE);

		KoTH.info("Faction created: " + faction.getTag() + "(ID: " + faction.getId() + ")");

		this.faction = faction;
		return true;
	}


	public boolean isConfigured() {
		if (!isCreated())
			return false;

		// KoTH factions should be permanent, closed and with a perm power of
		// Integer.MAX_VALUE
		return faction.isPermanent() && !faction.getOpen()
				&& faction.getPermanentPower() == Integer.MAX_VALUE;
	}

	public boolean isCreated() {
		return faction != null || (faction = find()) != null;
	}

	private Faction find() {
		return Factions.i.getByTag(name);
	}

	public Faction get() {
		return faction;
	}
}
