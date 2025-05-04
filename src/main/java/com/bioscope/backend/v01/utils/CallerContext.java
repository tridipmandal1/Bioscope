package com.bioscope.backend.v01.utils;


import org.springframework.stereotype.Component;

@Component
public class CallerContext {

    private static final ThreadLocal<String> caller = new ThreadLocal<>();

    public static void setCaller(String name) {
        caller.set(name);
    }

    public static String getCaller() {
        return caller.get();
    }

    public static void clear() {
        caller.remove();
    }

}
