package de.pianoman911.nawater.util;

import com.google.gson.JsonObject;
import de.pianoman911.nawater.NaWater;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class StreamUtils {

    private static final Logger LOGGER = LogManager.getLogger("StreamUtils");

    public static String readFully(InputStream is) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Throwable throwable) {
            LOGGER.error("Failed to read fully", throwable);
            return null;
        }
    }

    public static void writeFully(String string, OutputStream os) {
        try (OutputStreamWriter writer = new OutputStreamWriter(os)) {
            writer.write(string);
        } catch (Throwable throwable) {
            LOGGER.error("Failed to write fully", throwable);
        }
    }

    public static JsonObject readJsonFully(InputStream is) {
        return NaWater.GSON.fromJson(readFully(is), JsonObject.class);
    }

    public static void writeJsonFully(JsonObject json, OutputStream os) {
        writeFully(NaWater.GSON.toJson(json), os);
    }
}
