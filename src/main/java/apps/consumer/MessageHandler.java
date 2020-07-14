package apps.consumer;

import common.Constants;
import common.Context;
import monitoring.StatsMonitor;
import msgs.MessageParser;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class MessageHandler<T extends MessageParser> {

    protected final List<StatsMonitor<T>> monitorList;

    protected final Context context;

    /**
     * Instantiate an MessageHandler with a given context
     * Create list of monitors specified in the config file
     * All monitors specified must enforce the same typed parameter T
     *
     * @param context
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    @SuppressWarnings("unchecked")
    public MessageHandler(Context context) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        this.context = context;
        this.monitorList = new ArrayList<>();
        JSONObject monitors = (JSONObject) context.getConfig().get(Constants.MONITOR_LIST);
        for (Object key : monitors.keySet()) {
            Class<?> clz = Class.forName(key.toString());
            JSONObject config = (JSONObject) monitors.get(key);
            StatsMonitor<T> monitor = (StatsMonitor<T>) clz.getConstructor(Context.class, JSONObject.class).newInstance(context, config);
            monitorList.add(monitor);
        }
    }

    public void handle(T parser) {
        for (int i = 0; i < monitorList.size(); i++) {
            monitorList.get(i).onMsg(parser);
        }
    }
}
