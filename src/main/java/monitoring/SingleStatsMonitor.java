package monitoring;

import msgs.LogEntryParser;
import org.json.simple.JSONObject;

public abstract class SingleStatsMonitor extends StatsMonitor {

    public SingleStatsMonitor(JSONObject config) {
        super(config);
    }

    abstract protected boolean increment(LogEntryParser parser);

    abstract protected String alertName();

    abstract protected void output(String alert);
}
