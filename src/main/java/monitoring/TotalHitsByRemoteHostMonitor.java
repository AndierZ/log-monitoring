package monitoring;

import msgs.LogEntryParser;
import org.json.simple.JSONObject;

public class TotalHitsByRemoteHostMonitor extends KeyedRollingStatsMonitor<LogEntryParser> {

    public TotalHitsByRemoteHostMonitor(JSONObject config) {
        super(config);
    }

    @Override
    protected RollingStatsMonitor newRollingStatsMonitor(String key, JSONObject config) {
        JSONObject clone = new JSONObject(config);
        clone.put("key", "RemoteHost: " + key);
        return new TotalHitsMonitor(clone);
    }

    @Override
    protected String getKey(LogEntryParser parser) {
        return parser.getRemoteHost();
    }
}
