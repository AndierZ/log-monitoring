package monitoring;

import msgs.LogEntryParser;
import org.json.simple.JSONObject;

public class TotalHitsByUserMonitor extends KeyedRollingStatsMonitor {

    public TotalHitsByUserMonitor(JSONObject config) {
        super(config);
    }

    @Override
    protected RollingStatsMonitor newRollingStatsMonitor(String key, JSONObject config) {
        JSONObject clone = new JSONObject(config);
        clone.put("key", "AuthUser: " + key);
        return new TotalHitsMonitor(clone);
    }

    @Override
    protected String getKey(LogEntryParser parser) {
        return parser.getAuthUser();
    }
}
