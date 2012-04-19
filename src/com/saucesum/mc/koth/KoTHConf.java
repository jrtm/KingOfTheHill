package com.saucesum.mc.koth;

import com.massivecraft.factions.P;

public class KoTHConf {

	public static String factionName = "KoTH";

	public static int numberOfHills = 5;

	public static int prizeMoney = 10_000;

	private static transient KoTHConf i = new KoTHConf();
	public static void load() {
		P.p.persist.loadOrSaveDefault(i, KoTHConf.class, "kothconf");
	}
	public static void save() {
		P.p.persist.save(i);
	}

}
