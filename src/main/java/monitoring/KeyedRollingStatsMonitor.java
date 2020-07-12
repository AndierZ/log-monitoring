package monitoring;

import msgs.LogEntryParser;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class KeyedRollingStatsMonitor extends StatsMonitor {

    private final JSONObject config;
    protected final Map<String, RollingStatsMonitor> monitors = new HashMap<>();

    public KeyedRollingStatsMonitor(JSONObject config) {
        super(config);
        this.config = config;
    }

    @Override
    public void onMsg(LogEntryParser parser) {
        String key = getKey(parser);
        monitors.computeIfAbsent(key, k -> newRollingStatsMonitor(key, config)).onMsg(parser);
    }

    abstract protected RollingStatsMonitor newRollingStatsMonitor(String key, JSONObject config);

    abstract protected String getKey(LogEntryParser parser);
}
