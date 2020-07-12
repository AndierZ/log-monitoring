package apps.consumer;

import common.App;
import common.Constants;
import common.Context;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsoleAlertingApp extends App {

    private final ServerSocket serverSocket;
    private final ExecutorService executorService;

    public ConsoleAlertingApp(Context context) throws IOException {
        super(context);
        JSONObject config = context.getConfig();
        this.serverSocket = new ServerSocket(Integer.valueOf(config.get(Constants.PORT).toString()));
        this.executorService = Executors.newScheduledThreadPool(10);
    }

    @Override
    protected void start() throws Exception {
        while(true) {
            Socket socket = this.serverSocket.accept();
            MessageProcessingThread t = new MessageProcessingThread(socket);
            t.addHandler(new LogEntryHandler(context));
            executorService.submit(t);
        }
    }

    @Override
    protected void shutdown() throws IOException {
        this.serverSocket.close();
    }
}
