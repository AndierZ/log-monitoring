package common;

public abstract class App {
    protected final Context context;

    public App(Context context) {
        this.context = context;
    }

    protected abstract void start() throws Exception;

    protected abstract void shutdown() throws Exception;
}
