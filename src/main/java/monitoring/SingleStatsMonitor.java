package monitoring;

import common.Context;
import msgs.MessageParser;
import org.json.simple.JSONObject;


/**
 * Outputs a single metric
 *
 * @param <T>
 */
public abstract class SingleStatsMonitor<T extends MessageParser> extends StatsMonitor<T> {

    public SingleStatsMonitor(Context context, JSONObject config) {
        super(context, config);
    }

    abstract protected boolean increment(long timestamp, T parser);

    abstract protected String alertName();
}
