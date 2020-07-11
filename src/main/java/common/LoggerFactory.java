package common;

import org.slf4j.Logger;

import java.lang.management.ManagementFactory;

public class LoggerFactory {
    public static Logger newLogger() {
        StackTraceElement[] stackTrade = ManagementFactory.getThreadMXBean().getThreadInfo(Thread.currentThread().getId(), 5).getStackTrace();
        String clz = stackTrade[4].getClassName();
        return org.slf4j.LoggerFactory.getLogger(clz);
    }
}
