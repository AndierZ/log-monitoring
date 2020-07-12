package apps.producer;

import common.Constants;
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
            long timestamp = Long.valueOf(tokens[headers.get(Constants.DATE)]) * 1000;
            String section = tokens[headers.get(Constants.REQUEST)].split(" ")[1].split("\\/")[1];
            String remoteHost = tokens[headers.get(Constants.REMOTE_HOST)];
            int bytes = Integer.valueOf(tokens[headers.get(Constants.BYTES)]);
            this.logEntryBuilder.newMsg().setTimestamp(timestamp).setSection(section).setRemoteHost(remoteHost).setBytes(bytes).send(dataOutputStream);
        }
    }
}
