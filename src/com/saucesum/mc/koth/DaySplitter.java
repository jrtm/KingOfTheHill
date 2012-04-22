package com.saucesum.mc.koth;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class DaySplitter implements Runnable {

	private int taskId;

	private double perDay;
	private Runnable callback;

	private long nextTime;

	public DaySplitter(Plugin plugin, double perDay, Runnable callback, long freq) {
		this.perDay   = perDay;
		this.callback = callback;

		updateNext();

		this.taskId = Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, this, freq, freq);
	}

	public void stop() {
		Bukkit.getScheduler().cancelTask(taskId);
	}

	@Override
	public void run() {
		if (isReady()) {
			callback.run();
			updateNext();
		}
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
			time += (24 / perDay) * 60 * 60 * 1000;
		}

		return time;
	}

	private boolean isReady() {
		return System.currentTimeMillis() > nextTime;
	}

}
