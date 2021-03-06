package common;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TestContext extends Context {

    private final List<String> outputs = new ArrayList<>();

    public TestContext(JSONObject config) {
        super(config);
    }

    @Override
    protected OutputCollection createOutputs() {
        return new OutputCollection(this::output);
    }

    private void output(String s) {
        outputs.add(s);
    }

    public int outputCount() {
        return outputs.size();
    }

    public String getLastAlert() {
        return outputs.get(outputs.size() - 1);
    }
}
