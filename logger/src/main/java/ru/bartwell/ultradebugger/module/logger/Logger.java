package ru.bartwell.ultradebugger.module.logger;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by BArtWell on 24.02.2017.
 */

public class Logger {
    public static void addLog(@NonNull Context context, @NonNull String text) {
        StorageHelper.addLog(context, text);
    }

    public static void saveValue(@NonNull Context context, @NonNull String key, @Nullable Object value) {
        StorageHelper.saveValue(context, key, value);
    }
}
