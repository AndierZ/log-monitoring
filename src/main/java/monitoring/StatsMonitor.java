package monitoring;

import msgs.LogEntryParser;
import org.json.simple.JSONObject;

public abstract class StatsMonitor {

    public StatsMonitor(JSONObject config) {
    }

    abstract protected boolean increment(LogEntryParser parser);

    abstract protected boolean activateAlert();

    abstract protected String alertName();

    abstract protected void output(String alert);

    abstract public void onMsg(LogEntryParser parser);
}
