package monitoring;

import msgs.LogEntryParser;
import org.json.simple.JSONObject;

public abstract class StatsMonitor {

    public StatsMonitor(JSONObject config) {
    }

    abstract protected boolean increment(LogEntryParser parser);

    abstract protected boolean activateAlert();

    abstract protected String alertName();

    abstract public void onMsg(LogEntryParser parser);
}
