package monitoring;

import common.Context;
import msgs.MessageParser;
import org.json.simple.JSONObject;

public abstract class StatsMonitor<T extends MessageParser> {

    protected Context context;
    protected JSONObject config;

    public StatsMonitor(Context context, JSONObject config) {
        this.context = context;
        this.config = config;
    }

    abstract public void onMsg(T parser);
}
