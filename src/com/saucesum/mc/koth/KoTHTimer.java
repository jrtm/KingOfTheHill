package com.saucesum.mc.koth;

import java.util.Calendar;
import java.util.GregorianCalendar;

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
		long now = System.currentTimeMillis();

		this.nextTime = getNext(now);
	}

	private Calendar getMidnight(Calendar cal) {
		return new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
	}

	private long getNext(long now) {
		Calendar nowCal = new GregorianCalendar();
		nowCal.setTimeInMillis(now);

		Calendar midnight = getMidnight(nowCal);
		long time = midnight.getTimeInMillis();

		while (time <= now) {
			time += (24 / KoTHConf.resetsPerDay) * 60 * 60 * 1000;
		}

		return time;
	}

	private boolean isReady() {
		return System.currentTimeMillis() > nextTime;
	}

	public void cancelTasks() {
		Bukkit.getScheduler().cancelTask(taskID);
	}

}
