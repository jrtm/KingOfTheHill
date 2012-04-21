package com.saucesum.mc.koth;

import org.bukkit.Location;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

public class HillClaimer {

	private HillFaction faction;

	public HillClaimer(HillFaction faction) {
		this.faction = faction;
	}

	public void claim(HillLocations locations) {
		for (HillLocation location : locations.getAll()) {
			claim(location);
		}
	}

	public void claim(HillLocation location) {
		claim(location.getLocation());
	}

	public void claim(Location location) {
		claim(new FLocation(location));
	}

	public void claim(FLocation location) {
		if (!isFactionSet()) {
			return;
		}
		Faction f = faction.get();
		Board.setFactionAt(f, location);
	}

	public void unclaim(HillLocations locations) {
		for (HillLocation location : locations.getAll()) {
			unclaim(location);
		}
	}

	public void unclaim(HillLocation location) {
		unclaim(location.getLocation());
	}

	public void unclaim(Location location) {
		unclaim(new FLocation(location));
	}

	public void unclaim(FLocation location) {
		if (!isFactionSet()) {
			return;
		}
		Board.removeAt(location);
	}

	public void unclaimAll() {
		if (!isFactionSet()) {
			return;
		}
		Faction f = faction.get();
		Board.unclaimAll(f.getId());
	}

	private boolean isFactionSet() {
		return faction != null && faction.isConfigured();
	}

}
