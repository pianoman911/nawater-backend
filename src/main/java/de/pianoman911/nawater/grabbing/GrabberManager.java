package de.pianoman911.nawater.grabbing;

import de.pianoman911.nawater.NaWater;
import de.pianoman911.nawater.config.NaWaterConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class GrabberManager extends TimerTask {

    private final List<GrabberTask> tasks = new ArrayList<>();

    public GrabberManager(NaWater naWater) {
        NaWaterConfig.Grabber grabber = naWater.getConfig().grabber;

        for (UUID dashboard : grabber.dashboards) {
            this.tasks.add(new GrabberTask(dashboard, naWater));
        }

        Timer timer = new Timer("GrabberTimer", true);
        timer.scheduleAtFixedRate(this, 0, grabber.intervalMinutes * 60 * 1000L);
    }

    @Override
    public void run() {
        for (GrabberTask task : this.tasks) {
            task.run();
        }
    }
}
