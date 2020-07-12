package demo;

import apps.consumer.ConsoleAlertingApp;
import common.Main;

public class ConsoleAlertingAppLauncher {

    public static void main(String[] args) {
        Main main = new Main();
        String[] appArgs = new String[2];
        appArgs[0] = ConsoleAlertingApp.class.getName();
        appArgs[1] = ConsoleAlertingAppLauncher.class.getClassLoader().getResource("config/console_alerting_app.json").getFile();
        main.main(appArgs);
    }
}
