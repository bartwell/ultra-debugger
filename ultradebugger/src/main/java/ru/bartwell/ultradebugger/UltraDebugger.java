package ru.bartwell.ultradebugger;

import android.content.Context;

/**
 * Created by BArtWell on 01.01.2017.
 */

public class UltraDebugger {

    public static void start(Context context) {
        start(context, HttpServer.DEFAULT_PORT);
    }

    public static void start(Context context, int port) {
        ModulesManager.newInstance(context);
        ServerService.start(context, port);
    }

    public static void stop(Context context) {
        ServerService.stop(context);
    }

    public static int getPort() {
        return ServerService.getPort();
    }
}
