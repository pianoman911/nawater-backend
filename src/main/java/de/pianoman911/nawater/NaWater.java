package de.pianoman911.nawater;

import com.google.gson.Gson;
import de.pianoman911.nawater.archiver.Archiver;
import de.pianoman911.nawater.archiver.SQLArchiver;
import de.pianoman911.nawater.config.ConfigLoader;
import de.pianoman911.nawater.config.NaWaterConfig;
import de.pianoman911.nawater.grabbing.GrabberManager;
import de.pianoman911.nawater.web.WebServer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.io.IoBuilder;

import java.nio.file.Path;


public class NaWater {

    public static final Gson GSON = new Gson();
    private static final Logger LOGGER = LogManager.getLogger("NaWater");

    static {
        System.setProperty("java.util.logging.manager", org.apache.logging.log4j.jul.LogManager.class.getName());
        System.setProperty("java.awt.headless", "true");

        System.setOut(IoBuilder.forLogger(LOGGER).setLevel(Level.INFO).buildPrintStream());
        System.setErr(IoBuilder.forLogger(LOGGER).setLevel(Level.ERROR).buildPrintStream());
    }

    private NaWaterConfig config;
    private Archiver archiver;
    private GrabberManager grabberManager;
    private boolean running = true;

    public void start(long bootTime) {
        try {
            LOGGER.info("Starting NaWater...");

            LOGGER.info("Reading configuration...");
            this.config = ConfigLoader.loadObject(Path.of("config.yml"), NaWaterConfig.class);

            LOGGER.info("Starting archiver...");
            LOGGER.info("This version only supports SQL archiving.");
            this.archiver = new SQLArchiver(this);

            LOGGER.info("Starting data grabbers...");
            this.grabberManager = new GrabberManager(this);

            LOGGER.info("Starting Webserver...");
            new WebServer(this).start();

            LOGGER.info("Starting console...");
            new NaWaterConsole(this).startThread();

            double startingTime = (System.currentTimeMillis() - bootTime) / 1000d;
            double startTime = Math.round(startingTime * 100d) / 100d;
            LOGGER.info("\\(^O^)/ Hello there! Service started in {}s, type \"help\" for help", startTime);
        } catch (Exception exception) {
            LOGGER.error("Failed to start NaWater", exception);
            this.shutdown(false);
        }
    }

    public void shutdown(boolean clean) {
        this.running = false;

        LOGGER.info("Exiting, goodbye! (-_-) . z Z");
        LogManager.shutdown();
        System.exit(clean ? 0 : 1);
    }

    public NaWaterConfig getConfig() {
        return config;
    }

    public Archiver getArchiver() {
        return archiver;
    }

    public GrabberManager getGrabberManager() {
        return grabberManager;
    }

    public boolean isRunning() {
        return this.running;
    }
}
