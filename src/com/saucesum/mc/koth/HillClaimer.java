package com.saucesum.mc.koth;

import org.bukkit.Location;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.util.SpiralTask;

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
		claimWarZones(location);
	}

	private void claimWarZones(FLocation location) {
		final String hillFactionId = faction.get().getId();
		final Faction warzone = Factions.i.getWarZone();

		new SpiralTask(location, 2) {
			@Override
			public boolean work() {
				FLocation loc  = this.currentFLocation();
				Faction   fact = Board.getFactionAt(loc);

				if (fact != null && fact.getId().equals(hillFactionId)) {
					return true;
				}

				Board.setFactionAt(warzone, loc);
				return true;
			}
		};
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
		unclaimWarZones(location);
		Board.removeAt(location);
	}

	private void unclaimWarZones(FLocation location) {
		final Faction none = Factions.i.getNone();
		new SpiralTask(location, 2) {
			@Override
			public boolean work() {
				FLocation loc  = currentFLocation();
				Faction   fact = Board.getFactionAt(loc);

				if (fact != null && fact.isWarZone()) {
					Board.setFactionAt(none, loc);
				}

				return true;
			}
		};
	}

	public void unclaimAll(HillLocations locs) {
		if (!isFactionSet()) {
			return;
		}
		Faction f = faction.get();
		for (HillLocation loc : locs.getAll()) {
			unclaim(loc);
		}
		Board.unclaimAll(f.getId());
	}

	private boolean isFactionSet() {
		return faction != null && faction.isConfigured();
	}

}
