package common;

import org.json.simple.JSONObject;

public class DevContext extends Context {

    public DevContext(JSONObject config) {
        super(config);
    }

    @Override
    protected OutputCollection createOutputs() {
        return new OutputCollection(System.out::println);
    }
}
