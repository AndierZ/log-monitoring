package monitoring;

import common.Context;
import msgs.LogEntryParser;
import org.json.simple.JSONObject;

public class TotalHitsByRemoteHostMonitor extends KeyedRollingStatsMonitor<LogEntryParser> {

    public TotalHitsByRemoteHostMonitor(Context context, JSONObject config) {
        super(context, config);
    }

    @Override
    protected RollingStatsMonitor newRollingStatsMonitor(String key) {
        TotalHitsMonitor m = new TotalHitsMonitor(context, config);
        m.setPrefix("RemoteHost: " + key);
        return m;
    }

    @Override
    protected String getKey(LogEntryParser parser) {
        return parser.getRemoteHost();
    }
}
