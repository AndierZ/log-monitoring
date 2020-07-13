package monitoring;

import common.Context;
import msgs.MessageParser;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class KeyedRollingStatsMonitor<T extends MessageParser> extends StatsMonitor<T> {

    protected final Map<String, RollingStatsMonitor> monitors = new HashMap<>();

    public KeyedRollingStatsMonitor(Context context, JSONObject config) {
        super(context, config);
    }

    @Override
    public void onMsg(T parser) {
        String key = getKey(parser);
        monitors.computeIfAbsent(key, k -> newRollingStatsMonitor(key)).onMsg(parser);
    }

    abstract protected RollingStatsMonitor newRollingStatsMonitor(String key);

    abstract protected String getKey(T parser);
}
