package msgs;

import msgs.MessageParser;

public interface MessageHandler<T extends MessageParser> {

    void handle(T parser);

    int getMessageType();

}
