package monitoring;

import msgs.LogEntryParser;

public abstract class StatsMonitor {

    abstract protected boolean increment(LogEntryParser parser);

    abstract protected boolean activateAlert();

    abstract protected String alertName();

    abstract public void onMsg(LogEntryParser parser);
}
