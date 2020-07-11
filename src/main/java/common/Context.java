package common;


import org.json.simple.JSONObject;

public abstract class Context {

    protected final JSONObject config;

    public Context(JSONObject config) {
        this.config = config;
    }

    public JSONObject getConfig() {
        return config;
    }
}
