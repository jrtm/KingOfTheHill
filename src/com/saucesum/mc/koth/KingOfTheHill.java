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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

public class KingOfTheHill extends JavaPlugin implements Listener {

	private static File maindirectory = new File("plugins", "KoTH");
	private static File locationsFile = new File(maindirectory, "hills.dat");

	public static final String TAG = "[KoTH]";

	private List<HillLocation> locations;

	private Hill[] hills;
	private Faction hillFaction;

	@Override
	public void onEnable() {
		maindirectory.mkdir();
		createHillFaction();

		loadHillLocations();
		claimHillsForFaction();

		getServer().getPluginManager().registerEvents(this, this);

		getCommand("koth").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender sender, Command command,
					String label, String[] args) {
				if (!(sender instanceof Player)) {
					return true;
				}

				if (args == null || args.length == 0) {
					sendInvalidUsageMessage(sender);
				}
				switch (args[0].toLowerCase()) {
				case "add":
				case "set":
				case "create":
					addHillLocation((Player) sender);
					sender.sendMessage("Hill created!");
					break;

				case "remove":
				case "unset":
				case "delete":
					removeHillLocation((Player) sender);
					sender.sendMessage("Hill removed!");
					break;

				case "clear":
					clearHillLocations();
					sender.sendMessage("Hills cleared!");
					break;

				default:
					sendInvalidUsageMessage(sender);
				}

				return true;
			}

		});
	}

	private void sendInvalidUsageMessage(CommandSender sender) {
		sender.sendMessage("Invalid usage. Use " + ChatColor.RED + "/koth add"
				+ ChatColor.RESET + " or " + ChatColor.RED + "/koth remove");
	}

	@Override
	public void onDisable() {
		saveHillLocations();
	}

	@SuppressWarnings("unchecked")
	private void loadHillLocations() {
		try {
			ObjectInputStream reader = new ObjectInputStream(
					new FileInputStream(locationsFile));
			locations = (List<HillLocation>) reader.readObject();
			reader.close();

			Bukkit.getLogger().info(
					TAG + " Loaded "
							+ (locations == null ? 0 : locations.size())
							+ " hills");
		} catch (IOException ioe) {
			Bukkit.getLogger().warning(
					TAG + " Could not load hill data: " + ioe);
		} catch (ClassNotFoundException cnfe) {
			Bukkit.getLogger().warning(
					TAG + " Location data is not of class List: " + cnfe);
		} finally {
			if (locations == null) {
				locations = new ArrayList<HillLocation>();
			}
		}
	}

	private void saveHillLocations() {
		try {
			ObjectOutputStream writer = new ObjectOutputStream(
					new FileOutputStream(locationsFile, false));
			writer.writeObject(locations);
			writer.close();
		} catch (IOException ioe) {
			Bukkit.getLogger().warning(
					TAG + " Could not save hill data: " + ioe);
		}
	}

	private void claimHillsForFaction() {
		if (!isFactionConfigured()) {
			return;
		}

		for (HillLocation hill : locations) {
			claimHillForFaction(hill);
		}
	}

	private void claimHillForFaction(HillLocation hill) {
		FLocation hillLocation = new FLocation(hill.getLocation());
		Board.setFactionAt(hillFaction, hillLocation);
	}

	private void unclaimHillForFaction(HillLocation hill) {
		FLocation hillLocation = new FLocation(hill.getLocation());
		Board.removeAt(hillLocation);
	}

	private void unclaimHillsForFaction() {
		Board.unclaimAll(hillFaction.getId());
	}

	private void createHillFaction() {
		if (isFactionCreated()) {
			if (!isFactionConfigured()) {
				Bukkit.getLogger().warning(
						TAG + " A faction named \"" + KoTHConf.factionName
						+ "\" already exists!");
			}
			return;
		}
		Faction faction = Factions.i.create(KoTHConf.factionName);
		faction.setOpen(false);
		faction.setPermanentPower(Integer.MAX_VALUE);
		faction.setPermanent(true);

		this.hillFaction = faction;
	}

	private boolean isFactionConfigured() {
		if (!isFactionCreated())
			return false;
		// KoTH factions should be permanent, closed and with a perm power of
		// Integer.MAX_VALUE
		return hillFaction.isPermanent() && !hillFaction.getOpen()
				&& hillFaction.getPermanentPower() == Integer.MAX_VALUE;
	}

	private boolean isFactionCreated() {
		return hillFaction != null || (hillFaction = findFaction()) != null;
	}

	private Faction findFaction() {
		return Factions.i.getByTag(KoTHConf.factionName);
	}

	private void addHillLocation(Player player) {
		HillLocation location = new HillLocation(player.getLocation());
		claimHillForFaction(location);
		locations.add(location);
	}

	private void removeHillLocation(Player player) {
		HillLocation location = new HillLocation(player.getLocation());
		unclaimHillForFaction(location);
		locations.remove(location);
	}

	private void clearHillLocations() {
		unclaimHillsForFaction();
		locations.clear();
	}

	private void generateNewHills(int numberOfHills) {
		if (locations.isEmpty()) {
			Bukkit.getLogger().info(TAG + " No hills created. Taking a nap for today.");
			return;
		}

		if (numberOfHills > locations.size()) {
			numberOfHills = locations.size();
		} else {
			Collections.shuffle(locations);
		}

		Hill[] hills = new Hill[numberOfHills];

		for (int i = 0; i < numberOfHills; i++) {
			hills[i] = generateHill(getHillName(i), locations.get(i));
		}

		this.hills = hills;
	}

	private Hill generateHill(String hillName, HillLocation hillLocation) {
		Block block = hillLocation.getLocation().getBlock();
		block.setType(Material.OBSIDIAN);
		block.setMetadata("koth.name", new FixedMetadataValue(this, hillName));

		return new Hill(hillName, block);
	}

	private String getHillName(int i) {
		return Character.toString((char)('A' + i));
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		Player player = e.getPlayer();

		Hill hill = getHill(e.getClickedBlock());
		if (hill != null) {
			if (hill.hasKing()) {
				alertPreviousKing(hill, player);
			}
			hill.setKing(player.getName());
			alertNewKing(hill, player);
		}

	}

	private void alertNewKing(Hill hill, Player newKing) {
		newKing.sendMessage(ChatColor.LIGHT_PURPLE + "[Hill " + hill.getName()
				+ "] " + ChatColor.RESET + "You are now the king!");
	}

	private void alertPreviousKing(Hill hill, Player newKing) {
		Player oldKing = getKingPlayer(hill);
		if (oldKing != null) {
			oldKing.sendMessage(ChatColor.LIGHT_PURPLE + "[Hill "
					+ hill.getName() + "] " + ChatColor.RESET
					+ newKing.getDisplayName() + " stole your throne!");
		}
	}

	private Player getKingPlayer(Hill hill) {
		return getServer().getPlayer(hill.getKing());
	}

	public Hill getHill(Block block) {
		if (!hillsCreated()) {
			return null;
		}

		if (block.getType() != Material.OBSIDIAN) {
			return null;
		}
		for (Hill hill : hills) {
			if (hill.getLocation().equals(block.getLocation())) {
				return hill;
			}
		}
		return null;
	}

	private boolean hillsCreated() {
		return hills != null && hills.length != 0;
	}

}
