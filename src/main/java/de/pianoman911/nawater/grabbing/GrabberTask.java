package de.pianoman911.nawater.grabbing;

import de.pianoman911.nawater.NaWater;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public record GrabberTask(UUID dashboardId, NaWater naWater) implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger("GrabberTask");

    @Override
    public void run() {
        DataGrabber.request(dashboardId).thenAccept(dashboard -> {
            LOGGER.info("Grabbed dashboard " + dashboardId);
            naWater.getArchiver().archive(dashboard);
        }).exceptionally(throwable -> {
            LOGGER.error("Failed to grab dashboard " + dashboardId, throwable);
            return null;
        });
    }
}
