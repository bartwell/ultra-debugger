package ru.bartwell.ultradebugger;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import ru.bartwell.ultradebugger.base.utils.IpUtils;

/**
 * Created by BArtWell on 01.01.2017.
 */

public class UltraDebugger {

    private static final String TAG = "UltraDebugger";

    private static String sIp;
    private static int sPort;

    public static void start(Context context) {
        start(context, HttpServer.DEFAULT_PORT);
    }

    public static void start(Context context, int port) {
        sIp = IpUtils.getIpV4();
        sPort = port;
        addLogInfo(port);
        ModulesManager.newInstance(context);
        ServerService.start(context, port);
    }

    public static void stop(Context context) {
        ServerService.stop(context);
    }

    public static int getPort() {
        return sPort;
    }

    public static String getIp() {
        return sIp;
    }

    private static void addLogInfo(int port) {
        Log.i(TAG, "-----------------------------");
        Log.i(TAG, "Open browser on computer and type address:");
        if (TextUtils.isEmpty(sIp)) {
            Log.i(TAG, "http://xxx.xxx.xxx.xxx:" + port);
            Log.i(TAG, "where xxx.xxx.xxx.xxx is IP of your smartphone");
            Log.i(TAG, "Make sure that your computer and smartphone connected to same network");
        } else {
            Log.i(TAG, "http://" + sIp + ":" + port);
        }
        Log.i(TAG, "-----------------------------");
    }
}
