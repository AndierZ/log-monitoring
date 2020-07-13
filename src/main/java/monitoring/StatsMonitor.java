package monitoring;

import msgs.MessageParser;
import org.json.simple.JSONObject;

public abstract class StatsMonitor<T extends MessageParser> {

    public StatsMonitor(JSONObject config) {

    }

    abstract public void onMsg(T parser);
}
