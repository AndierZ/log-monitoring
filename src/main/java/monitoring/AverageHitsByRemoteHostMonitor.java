package monitoring;

import common.Context;
import msgs.LogEntryParser;
import org.json.simple.JSONObject;

/**
 * Keeps track of average hits by remote host over a rolling window and generates alerts if above certain threshold
 */
public class AverageHitsByRemoteHostMonitor extends KeyedRollingStatsMonitor<LogEntryParser> {

    public AverageHitsByRemoteHostMonitor(Context context, JSONObject config) {
        super(context, config);
    }

    @Override
    protected RollingStatsMonitor newRollingStatsMonitor(String key) {
        AverageHitsMonitor m = new AverageHitsMonitor(context, config);
        m.setPrefix("RemoteHost: " + key);
        return m;
    }

    @Override
    protected String getKey(LogEntryParser parser) {
        return parser.getRemoteHost();
    }
}
