package apps.consumer;

import common.Context;
import monitoring.StatsMonitor;
import msgs.LogEntryMeta;
import msgs.LogEntryParser;
import msgs.MessageHandler;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class LogEntryHandler extends MessageHandler<LogEntryParser> {

    private final List<StatsMonitor> monitorList;

    public LogEntryHandler(Context context) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        super(context);
        this.monitorList = new ArrayList<>();
        JSONObject monitors = (JSONObject) context.getConfig().get("monitor_list");
        for(Object key : monitors.keySet()) {
            Class<?> clz = Class.forName(key.toString());
            if (StatsMonitor.class.isAssignableFrom(clz)) {
                monitorList.add((StatsMonitor) clz.getConstructor(JSONObject.class).newInstance(monitors.get(key)));
            }
        }
    }

    @Override
    public void handle(LogEntryParser parser) {
        System.out.println(parser.getMsgLen() + "," + parser.getMsgType() + "," + parser.getTimestamp() + "," + parser.getSection());
        for(int i=0; i<monitorList.size(); i++) {
            monitorList.get(i).onMsg(parser);
        }
    }

    @Override
    public int getMessageType() {
        return LogEntryMeta.MSG_TYPE;
    }

}