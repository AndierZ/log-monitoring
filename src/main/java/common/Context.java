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

    public final OutputCollection out;

    protected final JSONObject config;

    public Context(JSONObject config) {
        this.config = config;
        this.out = createOutputs();
    }

    abstract protected OutputCollection createOutputs();

    public JSONObject getConfig() {
        return config;
    }

    public class OutputCollection {
        public final Consumer<String> alertSink;

        OutputCollection(Consumer<String> alertSink) {
            this.alertSink = alertSink;
        }
    }
}
