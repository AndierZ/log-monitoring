package monitoring;

import common.Context;
import msgs.LogEntryParser;
import org.json.simple.JSONObject;

/**
 * Keeps track of hits by section over fixed period of time and prints sections with top hits
 */
public class SectionHitsMonitor extends RankedFixedStatsMonitor<LogEntryParser> {

    public SectionHitsMonitor(Context context, JSONObject config) {
        super(context, config);
    }

    /**
     * Increment the count using Section as the key
     *
     * @param parser
     * @return
     */
    @Override
    protected boolean increment(long timestamp, LogEntryParser parser) {
        this.hits.put(parser.getSection(), hits.getOrDefault(parser.getSection(), 0) + 1);
        return true;
    }

    @Override
    protected String alertName() {
        return "Section hit";
    }
}
