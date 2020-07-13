package common;


import org.json.simple.JSONObject;

import java.util.function.Consumer;

public abstract class Context {

    protected final JSONObject config;

    public Context(JSONObject config) {
        this.config = config;
    }

    public JSONObject getConfig() {
        return config;
    }

    abstract public Consumer<String> getOutputSink();
}
