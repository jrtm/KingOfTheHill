package com.saucesum.mc.koth;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.earth2me.essentials.api.Economy;

/**
 * 
 * King of The Hill bukkit plugin
 * 
 * @author Ravn T-M
 * 
 */
public class KoTH extends JavaPlugin implements Listener {

	public static  File mainDirectory = new File("plugins", "KoTH");
	private static File locationsFile = new File(mainDirectory, "locations.dat");
	private static File hillsFile     = new File(mainDirectory, "hills.dat");
	private static File confFile      = new File(mainDirectory, "config.json");

	public static final String TAG = "[KoTH]";

	private Hills hills;
	private HillLocations locations;
	private HillFaction faction;
	private HillClaimer claimer;

	//private KoTHTimer timer;
	private DaySplitter timer;

	@Override
	public void onEnable() {
		mainDirectory.mkdir();
		KoTHConf.load(confFile);

		locations = new HillLocations(locationsFile);
		hills     = new Hills(this, hillsFile);
		faction   = new HillFaction(KoTHConf.factionName);
		claimer   = new HillClaimer(faction);

		// Load persistent data
		locations.load();
		hills.    load();

		// Register bukkit handlers
		getServer().getPluginManager().registerEvents(this, this);
		getCommand("koth").setExecutor(new KoTHCommandExecutor(this));

		// Start timer to reset the hills
		//timer = new KoTHTimer(this);
		timer = new DaySplitter(this, KoTHConf.resetsPerDay, new Runnable() {
			@Override public void run() {
				resetHills();
			}
		}, 30 * 20L); 
	}

	@Override
	public void onDisable() {
		// Save persistent data
		locations.save();
		hills.    save();
		KoTHConf. save(confFile);
		timer.stop();
	}

	public void resetHills() {
		if (hills.isGenerated()) {
			awardOwnershipPrizes();
		}

		generateNewHills();
	}

	private void awardOwnershipPrizes() {
		StringBuilder winnersStr = new StringBuilder();

		for (Hill hill : hills.getAll()) {
			if (hill.hasKing()) {
				awardPrizeToKing(hill);
				winnersStr.append(" " + hill.getKing());
			}
		}
		if (winnersStr.length() > 0) {
			getServer().broadcastMessage(ChatColor.AQUA + "KoTH winners:" + ChatColor.RESET + winnersStr);
		}
	}

	private void awardPrizeToKing(Hill hill) {
		String king = hill.getKing();
		int prize = KoTHConf.prizeMoney;

		try {
			Economy.add(king, prize);
			sendToPlayer(king, "You were the king of " + ChatColor.BLUE + "Hill " + hill.getName() + ChatColor.RESET + "! Prize: " + ChatColor.GREEN + "$" + prize);

		} catch (Exception nlpe) {
			KoTH.warn(nlpe.toString());
		}
	}

	private void sendToPlayer(String playerName, String string) {
		Player player = Bukkit.getPlayer(playerName);
		if (player != null) {
			player.sendMessage(string);
		}
	}

	private void generateNewHills() {
		hills.generate(locations, KoTHConf.numberOfHills);
		if (hills.isGenerated()) {
			getServer().broadcastMessage(ChatColor.AQUA + "King of The Hill reset with " + hills.size() + " hills!");
		}
	}

	/* Bukkit event handlers */

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		Player player = e.getPlayer();

		Block block = e.getClickedBlock();
		if (block == null) {
			return;
		}

		Hill hill = hills.getHillByBlock(block);
		if (hill != null) {
			transferHillOwnership(player, hill);
		}
	}

	private void transferHillOwnership(Player player, Hill hill) {
		if (hill.hasKing()) {
			if (hill.getKing().equals((player.getName()))) {
				player.sendMessage(getHillPrefix(hill) + "You already own this hill!");
				return;
			}
			alertPreviousKing(hill, player);
		}
		hill.setKing(player.getName());
		alertNewKing(hill, player);

		// Apply some potion effects just for the luls
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 2));
		player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0));
	}

	private void alertNewKing(Hill hill, Player newKing) {
		newKing.sendMessage(getHillPrefix(hill) + "You are now the king!");
	}

	private void alertPreviousKing(Hill hill, Player newKing) {
		Player oldKing = getKingPlayer(hill);
		if (oldKing != null) {
			oldKing.sendMessage(getHillPrefix(hill) + newKing.getDisplayName()
					+ " captured your hill!");
		}
	}

	private String getHillPrefix(Hill hill) {
		return ChatColor.LIGHT_PURPLE + "[Hill " + hill.getName() + "] "
				+ ChatColor.RESET;
	}

	private Player getKingPlayer(Hill hill) {
		return getServer().getPlayer(hill.getKing());
	}

	/* Command handlers */

	public void commandAdd(Player sender) {
		Location location = sender.getLocation();
		locations.add(location);
		claimer.claim(location);

		sender.sendMessage("Hill location added");
	}

	public void commandRemove(Player sender) {
		Location location = sender.getLocation();

		if (locations.remove(location)) {
			claimer.unclaim(location);
			sender.sendMessage("Hill location removed");

		} else {
			sender.sendMessage("No hills to remove");
		}

	}

	public void commandClear(Player sender) {
		claimer.unclaimAll(locations);
		locations.clear();

		sender.sendMessage("Hill locations cleared");
	}

	public void commandInitialize(Player sender) {
		if (faction.create()) {
			sender.sendMessage("King of The Hill faction initialized");
		} else {
			sender.sendMessage("Could not initialize the King of The Hill faction");
		}
	}

	public void commandGenerate(Player sender) {
		generateNewHills();
	}

	/* Utility output methods */

	public static void warn(String message) {
		Bukkit.getLogger().warning(TAG + " " + message);
	}

	public static void info(String message) {
		Bukkit.getLogger().info(TAG + " " + message);
	}
}
