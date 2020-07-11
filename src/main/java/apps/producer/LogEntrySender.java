package apps.producer;

import msgs.LogEntryBuilder;
import msgs.MessageSender;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LogEntrySender implements MessageSender {

    private final Map<String, Integer> headers = new HashMap<>();
    private final LogEntryBuilder logEntryBuilder = new LogEntryBuilder();

    @Override
    public void send(String[] tokens, DataOutputStream dataOutputStream) throws IOException {
        if(headers.isEmpty()) {
            for(int i=0; i<tokens.length; i++) {
                headers.put(tokens[i], i);
            }
        } else {
            long timestamp = Long.valueOf(tokens[headers.get("date")]) * 1000;
            String section = tokens[headers.get("request")].split(" ")[1].split("\\/")[1];
            this.logEntryBuilder.newMsg().setTimestamp(timestamp).setSection(section).send(dataOutputStream);
        }
    }
}
