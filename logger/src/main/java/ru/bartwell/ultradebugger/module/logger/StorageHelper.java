package ru.bartwell.ultradebugger.module.logger;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Created by BArtWell on 24.02.2017.
 */
class StorageHelper {
    private static final String LOG_FILE_NAME = "ultra_debugger_logger.log";
    private static final String PREFERENCES_FILE_NAME = "ultra_debugger_logger";
    private static final long MAX_LOGS_FILE_SIZE = 1024 * 1024;

    static void saveValue(@NonNull Context context, @NonNull String key, @Nullable Object value) {
        getSharedPreferences(context)
                .edit()
                .putString(key, new Gson().toJson(value))
                .apply();
    }

    @NonNull
    static Map<String, ?> getAllValues(@NonNull Context context) {
        return getSharedPreferences(context).getAll();
    }

    static void removeValue(@NonNull Context context, @NonNull String key) {
        getSharedPreferences(context)
                .edit()
                .remove(key)
                .apply();
    }

    @NonNull
    private static SharedPreferences getSharedPreferences(@NonNull Context context) {
        return context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
    }

    static void addLog(@NonNull Context context, @NonNull String text) {
        rotateLogs(context);
        writeToLogsFile(context, getLogDate() + " " + text + "\n", true);
    }

    static void clearLogs(@NonNull Context context) {
        writeToLogsFile(context, "", false);
    }

    static void rotateLogs(@NonNull Context context) {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            if (getLogsFile(context).length() > MAX_LOGS_FILE_SIZE) {
                String tmpFileName = getLogsFile(context).getAbsolutePath().concat(".tmp");
                //noinspection ResultOfMethodCallIgnored
                new File(tmpFileName).delete();
                //noinspection ResultOfMethodCallIgnored
                getLogsFile(context).renameTo(new File(tmpFileName));
                inputStream = new FileInputStream(new File(tmpFileName));
                outputStream = new FileOutputStream(getLogsFile(context));
                inputChannel = inputStream.getChannel();
                outputChannel = outputStream.getChannel();
                long size = inputChannel.size();
                long start = size - MAX_LOGS_FILE_SIZE;
                inputChannel.transferTo(start, size - start, outputChannel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeSafely(outputChannel);
            closeSafely(inputChannel);
            closeSafely(outputStream);
            closeSafely(inputStream);
        }
    }

    @NonNull
    static String readLogs(@NonNull Context context, @NonNull String delimiter) {
        FileInputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            inputStream = context.openFileInput(LOG_FILE_NAME);
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(delimiter);
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeSafely(bufferedReader);
            closeSafely(inputStreamReader);
            closeSafely(inputStream);
        }
        return "";
    }

    private static void writeToLogsFile(Context context, String text, boolean append) {
        FileOutputStream outputStream = null;
        try {
            outputStream = context.openFileOutput(LOG_FILE_NAME, append ? Context.MODE_APPEND : Context.MODE_PRIVATE);
            outputStream.write(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeSafely(outputStream);
        }
    }

    @NonNull
    private static File getLogsFile(@NonNull Context context) {
        return context.getFileStreamPath(LOG_FILE_NAME);
    }

    @NonNull
    private static String getLogDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
                .format(new Date());
    }

    private static void closeSafely(@Nullable Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
