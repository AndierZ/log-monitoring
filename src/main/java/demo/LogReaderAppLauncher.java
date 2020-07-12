package demo;

import apps.producer.LogReaderApp;
import common.Main;

public class LogReaderAppLauncher {

    public static void main(String[] args) {
        Main main = new Main();
        String[] appArgs = new String[2];
        appArgs[0] = LogReaderApp.class.getName();
        appArgs[1] = LogReaderAppLauncher.class.getClassLoader().getResource("config/log_reader_app.json").getFile();

        main.main(appArgs);
    }
}
