package common;

import org.json.simple.JSONObject;

import java.util.function.Consumer;

public class DevContext extends Context {

    public DevContext(JSONObject config) {
        super(config);
    }

    @Override
    public Consumer<String> getOutputSink() {
        return System.out::println;
    }
}
