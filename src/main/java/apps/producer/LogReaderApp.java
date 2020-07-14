package apps.producer;

import common.App;
import common.Constants;
import common.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogReaderApp extends App {

    private final ExecutorService executorService;
    private List<MessageDistributionThread> fileParsers = new ArrayList<>();

    public LogReaderApp(Context context) throws IOException {
        super(context);
        JSONObject config = context.getConfig();
        JSONArray logFiles = (JSONArray) config.get(Constants.LOG_FILES);
        JSONArray consumerAddresses = (JSONArray) config.get(Constants.CONSUMER_ADDRESSES);
        // When running locally, option to get test log file from the resource directory
        boolean useResourceFile = (boolean) config.getOrDefault(Constants.LOG_FILES_IN_RESOURCE, false);

        // Each log file is processed by a separate thread
        for (int i = 0; i < logFiles.size(); i++) {
            String filePath;
            if (useResourceFile) {
                filePath = LogReaderApp.class.getClassLoader().getResource(logFiles.get(i).toString()).getFile();
            } else {
                filePath = logFiles.get(i).toString();
            }

            fileParsers.add(new MessageDistributionThread(filePath, consumerAddresses, new LogEntrySender()));
        }

        executorService = Executors.newScheduledThreadPool(10);
    }

    @Override
    protected void start() {
        fileParsers.forEach(executorService::submit);
    }

    @Override
    protected void shutdown() {
        executorService.shutdown();
    }
}
