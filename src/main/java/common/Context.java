package common;


import org.json.simple.JSONObject;

import java.util.function.Consumer;

/**
 * An object to provide the context for an app, for example
 * Input config(s)
 * Output sink(s)
 * Ideally all I/Os are routed through this class so different context can be used for different environment: Dev, Prod, UnitTest, RegressionTest, Historical and etc.
 */
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
