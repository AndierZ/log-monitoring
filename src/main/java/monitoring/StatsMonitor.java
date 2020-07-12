package monitoring;

import msgs.LogEntryParser;
import org.json.simple.JSONObject;

public abstract class StatsMonitor {

    public StatsMonitor(JSONObject config) {

    }

    abstract public void onMsg(LogEntryParser parser);
}
