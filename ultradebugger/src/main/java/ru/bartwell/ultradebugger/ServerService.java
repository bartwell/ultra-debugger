package ru.bartwell.ultradebugger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class ServerService extends Service {
    private static final String EXTRA_PORT = "port";
    private HttpServer mHttpServer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int port = intent == null ? HttpServer.DEFAULT_PORT : intent.getIntExtra(EXTRA_PORT, HttpServer.DEFAULT_PORT);
        mHttpServer = new HttpServer(port);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mHttpServer.stop();
        super.onDestroy();
    }

    public static void start(Context context, int port) {
        Intent intent = new Intent(context, ServerService.class);
        intent.putExtra(EXTRA_PORT, port);
        context.startService(intent);
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, ServerService.class));
    }
}
