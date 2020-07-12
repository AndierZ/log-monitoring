package monitoring;

import common.Context;
import msgs.LogEntryParser;

public class SectionHitMonitor extends FixedStatsMonitor {

    public SectionHitMonitor(Context context) {
        super(context);
    }

    @Override
    protected boolean increment(LogEntryParser parser) {
        hits.put(parser.getSection(), hits.getOrDefault(parser.getSection(), 0) + 1);
        return true;
    }

    @Override
    protected boolean activateAlert() {
        return true;
    }

    @Override
    protected String alertName() {
        return "Section hit";
    }
}
