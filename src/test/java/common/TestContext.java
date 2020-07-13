package common;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TestContext extends Context {

    private final List<String> outputs = new ArrayList<>();

    public TestContext(JSONObject config) {
        super(config);
    }

    @Override
    public Consumer<String> getOutputSink() {
        return this::output;
    }

    public void output(String s) {
        outputs.add(s);
    }

    public int outputCount() {
        return this.outputs.size();
    }

    public String getLastAlert() {
        return this.outputs.get(this.outputs.size()-1);
    }
}
