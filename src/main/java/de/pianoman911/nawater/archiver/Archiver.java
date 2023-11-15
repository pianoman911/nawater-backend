package de.pianoman911.nawater.archiver;

import com.google.gson.JsonArray;
import de.pianoman911.nawater.data.Dashboard;

public interface Archiver {

    void archive(Dashboard dashboard);

    JsonArray query(String dashboardId, long from, long to);
}
