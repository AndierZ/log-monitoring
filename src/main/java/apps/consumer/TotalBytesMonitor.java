package apps.consumer;

import common.TimeseriesCircularCounter;
import msgs.LogEntryParser;
import org.json.simple.JSONObject;

public class TotalBytesMonitor extends RollingStatsMonitor {

    public TotalBytesMonitor(JSONObject config) {
        super(config);
    }

    @Override
    protected void increment(TimeseriesCircularCounter counter, LogEntryParser parser) {

    }

    @Override
    protected boolean activateAlert(TimeseriesCircularCounter counter, double threshold) {
        return false;
    }

    @Override
    protected String alertName() {
        return null;
    }
}
