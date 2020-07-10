package common;

import java.lang.reflect.InvocationTargetException;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Must specify 2 arguments: (AppClass, ConfigFilePath)");
        }
        String configFile = args[1];

        Class<?> clz = Class.forName(args[0]);
        if (clz.isAssignableFrom(App.class)) {
            Context context = ContextBuilder.buildContext(configFile);
            App app = (App) clz.getConstructor(Context.class).newInstance(context);
            app.start();
        } else {
            throw new IllegalArgumentException("Specified class must be a subclass of common.App");
        }
    }
}
