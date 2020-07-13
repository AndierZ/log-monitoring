package monitoring;

import common.Context;
import msgs.LogEntryParser;
import org.json.simple.JSONObject;

public class SectionHitsMonitor extends KeyedFixedStatsMonitor<LogEntryParser> {

    public SectionHitsMonitor(Context context, JSONObject config) {
        super(context, config);
    }

    @Override
    protected boolean increment(LogEntryParser parser) {
        hits.put(parser.getSection(), hits.getOrDefault(parser.getSection(), 0) + 1);
        return true;
    }

    @Override
    protected String alertName() {
        return "Section hit";
    }
}
