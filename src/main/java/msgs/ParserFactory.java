package msgs;

import common.Constants;

public class ParserFactory {

    private static MessageParser[] CACHE = new MessageParser[Constants.TOTAL_MSG_TYPES];

    static {
        CACHE[LogEntryMeta.MSG_TYPE] =  new LogEntryParser();
    }

    public static MessageParser getParser(int msgType) {
        return CACHE[msgType];
    }
}
