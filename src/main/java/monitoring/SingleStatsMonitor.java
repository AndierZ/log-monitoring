package monitoring;

import msgs.MessageParser;
import org.json.simple.JSONObject;

public abstract class SingleStatsMonitor<T extends MessageParser> extends StatsMonitor<T> {

    public SingleStatsMonitor(JSONObject config) {
        super(config);
    }

    abstract protected boolean increment(T parser);

    abstract protected String alertName();

    abstract protected void output(String alert);
}
