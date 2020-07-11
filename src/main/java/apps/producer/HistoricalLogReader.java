package apps.producer;

import common.App;
import common.Constants;
import common.Context;
import common.LoggerFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoricalLogReader extends App {

    private final ExecutorService executorService;
    private List<MessageDistributionThread> fileParsers = new ArrayList<>();

    public HistoricalLogReader(Context context) throws IOException {
        super(context);
        JSONObject config = context.getConfig();
        JSONArray logFiles = (JSONArray) config.get(Constants.LOG_FILES);
        JSONArray consumerAddresses = (JSONArray) config.get(Constants.CONSUMER_ADDRESSES);

        for(int i=0; i< logFiles.size(); i++) {
            String filePath = logFiles.get(i).toString();
            fileParsers.add(new MessageDistributionThread(filePath, consumerAddresses, new LogEntrySender()));
        }

        this.executorService = Executors.newScheduledThreadPool(10);
    }

    @Override
    protected void start() {
        this.fileParsers.forEach(this.executorService::submit);
    }

    @Override
    protected void shutdown() {
        this.executorService.shutdown();
    }
}
