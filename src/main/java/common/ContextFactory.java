package common;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Objects;

public class ContextFactory {

    public static Context newContext(String configPath) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject configObject = (JSONObject) parser.parse(new BufferedReader(new FileReader(new File(configPath))));
        if(Objects.equals(configObject.get(Constants.CONTEXT_MODE), Constants.CONTEXT_HISTORICAL)) {
            return new HistoricalContext(configObject);
        } else if (Objects.equals(configObject.get(Constants.CONTEXT_MODE), Constants.CONTEXT_REALTIME)) {
            return new RealtimeContext(configObject);
        }
        throw new IllegalArgumentException("Config file must specify the context mode: historical/realtime");
    }

    private static class HistoricalContext extends Context {
        private HistoricalContext(JSONObject config) {
            super(config);
        }
    }

    private static class RealtimeContext extends Context {
        private RealtimeContext(JSONObject config) {
            super(config);
        }
    }

}
