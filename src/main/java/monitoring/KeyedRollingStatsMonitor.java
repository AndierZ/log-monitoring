package monitoring;

import msgs.MessageParser;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class KeyedRollingStatsMonitor<T extends MessageParser> extends StatsMonitor<T> {

    private final JSONObject config;
    protected final Map<String, RollingStatsMonitor> monitors = new HashMap<>();

    public KeyedRollingStatsMonitor(JSONObject config) {
        super(config);
        this.config = config;
    }

    @Override
    public void onMsg(T parser) {
        String key = getKey(parser);
        monitors.computeIfAbsent(key, k -> newRollingStatsMonitor(key, config)).onMsg(parser);
    }

    abstract protected RollingStatsMonitor newRollingStatsMonitor(String key, JSONObject config);

    abstract protected String getKey(T parser);
}
