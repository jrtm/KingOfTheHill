package com.saucesum.mc.koth;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;

import org.bukkit.Material;

import com.google.gson.Gson;

public class KoTHConf {

	public static String factionName = "KoTH";

	public static int numberOfHills = 3;

	public static int prizeMoney = 10000;

	public static Material hillType = Material.SIGN_POST;

	private static Gson gson = new Gson();

	@SuppressWarnings("unchecked")
	public static void load(File file) {
		try {
			FileReader confReader = new FileReader(file);

			HashMap<String, String> conf = gson.fromJson(confReader, HashMap.class);
			confReader.close();

			factionName   = conf.get("factionName");
			numberOfHills =  Integer.valueOf(conf.get("numberOfHills").toString());
			prizeMoney    =  Integer.valueOf(conf.get("prizeMoney").toString());
			String type   = conf.get("hillType");

			hillType      = Material.valueOf(type);

		} catch (Exception e) {
			KoTH.warn("Could not load configuration data: " + e);
		}
	}

	public static void save(File file) {
		HashMap<String, String> conf = new HashMap<String, String>();
		conf.put("factionName", factionName);
		conf.put("numberOfHills", Integer.toString(numberOfHills));
		conf.put("prizeMoney", Integer.toString(prizeMoney));
		conf.put("hillType", hillType.name());

		try {
			PrintWriter writer = new PrintWriter(file);
			String json = gson.toJson(conf);
			writer.println(json);
			writer.close();

		} catch (Exception e) {
			KoTH.warn("Could not save configuration data: " + e);
		}

	}

}
