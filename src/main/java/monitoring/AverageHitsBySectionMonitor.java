package monitoring;

import common.Context;
import msgs.LogEntryParser;
import org.json.simple.JSONObject;

/**
 * Keeps track of average hits by section over a rolling window and generates alerts if above certain threshold
 */
public class AverageHitsBySectionMonitor extends KeyedRollingStatsMonitor<LogEntryParser> {

    public AverageHitsBySectionMonitor(Context context, JSONObject config) {
        super(context, config);
    }

    @Override
    protected RollingStatsMonitor newRollingStatsMonitor(String key) {
        AverageHitsMonitor m = new AverageHitsMonitor(context, config);
        m.setPrefix("Section: " + key);
        return m;
    }

    @Override
    protected String getKey(LogEntryParser parser) {
        return parser.getSection();
    }
}
