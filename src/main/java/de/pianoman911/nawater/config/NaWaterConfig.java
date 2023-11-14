package de.pianoman911.nawater.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.UUID;

@SuppressWarnings("FieldMayBeFinal")
@ConfigSerializable
public class NaWaterConfig {

    public Grabber grabber = new Grabber();
    public Web web = new Web();
    public MySql mysql = new MySql();

    @ConfigSerializable
    public static class Grabber {
        public UUID[] dashboards = new UUID[0];
        public int intervalMinutes = 5;
    }

    @ConfigSerializable
    public static class Web {
        public int port = 8080;

    }

    @ConfigSerializable
    public static class MySql {
        public String host = "127.0.0.1";
        public int port = 3306;
        public String database = "nawater";
        public String username = "";
        public String password = "";
    }
}
