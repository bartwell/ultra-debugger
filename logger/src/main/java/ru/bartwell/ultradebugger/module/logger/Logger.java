package ru.bartwell.ultradebugger.module.logger;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by BArtWell on 24.02.2017.
 */

public class Logger {
    public static void addLog(@NonNull Context context, @NonNull String text) {
        addLog(context, text, null);
    }

    public static void addLog(@NonNull Context context, @NonNull String text, @Nullable Throwable throwable) {
        if (throwable == null) {
            StorageHelper.addLog(context, text);
        } else {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(printWriter);
            addLog(context, text + "\n" + stringWriter.toString() + "\n");
        }
    }

    public static String getLogDownloadPath() {
        return Utils.getDownloadPath();
    }

    public static void clearLogs(@NonNull Context context) {
        StorageHelper.clearLogs(context);
    }

    public static void saveValue(@NonNull Context context, @NonNull String key, @Nullable Object value) {
        StorageHelper.saveValue(context, key, value);
    }
}
