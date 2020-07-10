package common;

import org.json.simple.JSONObject;

public class ContextBuilder {

    public static Context buildContext(String configPath){
        return null;
    }

    private class HistoricalContext extends Context {
        private HistoricalContext(JSONObject config) {
            super(config);
        }
    }

    private class RealtimeContext extends Context {
        private RealtimeContext(JSONObject config) {
            super(config);
        }
    }

}
