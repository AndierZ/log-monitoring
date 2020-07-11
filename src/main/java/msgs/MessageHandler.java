package msgs;

import common.Context;

public abstract class MessageHandler<T extends MessageParser> {

    protected final Context context;

    public MessageHandler(Context context) {
        this.context = context;
    }

    public abstract void handle(T parser);

    public abstract int getMessageType();

}
