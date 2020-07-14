package monitoring;

import common.Context;
import msgs.MessageParser;
import org.json.simple.JSONObject;

/**
 * Processes a certain type of message and collect stats
 *
 * @param <T>
 */
public abstract class StatsMonitor<T extends MessageParser> {

    protected Context context;
    protected JSONObject config;

    public StatsMonitor(Context context, JSONObject config) {
        this.context = context;
        this.config = config;
    }

    abstract public void onMsg(T parser);
}
