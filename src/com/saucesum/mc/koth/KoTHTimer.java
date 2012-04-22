package com.saucesum.mc.koth;

import java.util.Calendar;

import org.bukkit.Bukkit;

public class KoTHTimer {

	private KoTH koth;

	private int taskID;

	private long nextTime;

	public KoTHTimer(KoTH koth) {
		this.koth = koth;

		updateNext();

		startTimer();
	}

	private void startTimer() {
		taskID = Bukkit.getScheduler().scheduleAsyncRepeatingTask(koth, new Runnable() {
			@Override
			public void run() {
				if (isReady()) {
					koth.resetHills();
					updateNext();
				}
			}

		}, 5 * 20L, 30 * 20L);
	}

	private void updateNext() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 3);

		nextTime = cal.getTimeInMillis();
	}

	private boolean isReady() {
		return System.currentTimeMillis() > nextTime;
	}

	public void cancelTasks() {
		Bukkit.getScheduler().cancelTask(taskID);
	}

}
