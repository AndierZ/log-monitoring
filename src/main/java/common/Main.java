package common;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Main {

    private static Logger LOGGER = LoggerFactory.newLogger();

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Must specify 2 arguments: (AppClass, ConfigFilePath)");
        }

        Class<?> clz;
        try {
            clz = Class.forName(args[0]);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Cannot find app class {}.", args[0]);
            throw new IllegalArgumentException(e);
        }

        if (!App.class.isAssignableFrom(clz)) {
            throw new IllegalArgumentException("Specified class must be a subclass of common.App");
        }

        Context context;
        try {
            context = ContextFactory.newContext(args[1]);
        } catch (IOException | ParseException e) {
            LOGGER.error("Error parsing config file {}.", args[1]);
            throw new IllegalArgumentException(e);
        }

        App app;
        try {
            app = (App) clz.getConstructor(Context.class).newInstance(context);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOGGER.error("Error instantiating app class.");
            throw new IllegalArgumentException(e);
        }

        try {
            app.start();
        } catch (Exception e) {
            LOGGER.error("Exception running app.", e);
        } finally {
            LOGGER.info("App shutting down.");
            try {
                app.shutdown();
                LOGGER.info("App shutdown complete.");
            } catch (Exception e) {
                LOGGER.error("App shutdown exception.", e);
            }
        }
    }
}
