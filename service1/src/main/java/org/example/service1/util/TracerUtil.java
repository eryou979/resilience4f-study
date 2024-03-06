package org.example.service1.util;

/**
 * @author dlz
 * @since 2023/09/14
 */
public class TracerUtil {

    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static String traceId() {
        return threadLocal.get();
    }

    public static void setTraceId(String traceId) {
        threadLocal.set(traceId);
    }
}
