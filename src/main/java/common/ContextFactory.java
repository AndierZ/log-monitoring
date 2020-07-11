package common;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ContextFactory {

    public static Context newContext(String configPath) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject configObject = (JSONObject) parser.parse(new BufferedReader(new FileReader(new File(configPath))));
        return new Context(configObject);
    }
}
