package ru.bartwell.ultradebugger.sampleapp;

import android.app.Application;

import ru.bartwell.ultradebugger.UltraDebugger;

/**
 * Created by BArtWell on 01.01.2017.
 */

public class DebuggerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        UltraDebugger.start(this, 8081);
    }
}
