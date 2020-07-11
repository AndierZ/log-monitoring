package apps.consumer;

import msgs.LogEntryMeta;
import msgs.LogEntryParser;
import msgs.MessageHandler;

public class LogEntryHandler implements MessageHandler<LogEntryParser> {
    @Override
    public void handle(LogEntryParser parser) {
        System.out.println(parser.getMsgLen() + "," + parser.getMsgType() + "," + parser.getTimestamp() + "," + parser.getSection());
    }

    @Override
    public int getMessageType() {
        return LogEntryMeta.MSG_TYPE;
    }

}