package ru.bartwell.ultradebugger.wrapper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Method;

/**
 * Created by BArtWell on 17.07.2017.
 */

public class UltraDebuggerWrapper {

    private static boolean sIsEnabled = true;

    private UltraDebuggerWrapper() {
    }

    public static void setEnabled(boolean isEnabled) {
        sIsEnabled = isEnabled;
    }

    public static void start(@NonNull Context context) {
        if (sIsEnabled) {
            try {
                Class<?> clazz = Class.forName("ru.bartwell.ultradebugger.UltraDebugger");
                Method method = clazz.getMethod("start", Context.class);
                method.invoke(null, context);
            } catch (Exception ignored) {
            }
        }
    }

    public static void start(@NonNull Context context, int port) {
        if (sIsEnabled) {
            try {
                Class<?> clazz = Class.forName("ru.bartwell.ultradebugger.UltraDebugger");
                Method method = clazz.getMethod("start", Context.class, int.class);
                method.invoke(null, context, port);
            } catch (Exception ignored) {
            }
        }
    }

    public static void stop(@NonNull Context context) {
        if (sIsEnabled) {
            try {
                Class<?> clazz = Class.forName("ru.bartwell.ultradebugger.UltraDebugger");
                Method method = clazz.getMethod("stop", Context.class);
                method.invoke(null, context);
            } catch (Exception ignored) {
            }
        }
    }

    public static int getPort() {
        if (sIsEnabled) {
            try {
                Class<?> clazz = Class.forName("ru.bartwell.ultradebugger.UltraDebugger");
                Method method = clazz.getMethod("getPort");
                return (int) method.invoke(null);
            } catch (Exception ignored) {
            }
        }
        return -1;
    }

    @Nullable
    public static String getIp() {
        if (sIsEnabled) {
            try {
                Class<?> clazz = Class.forName("ru.bartwell.ultradebugger.UltraDebugger");
                Method method = clazz.getMethod("getIp");
                return (String) method.invoke(null);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public static void addLog(@NonNull Context context, @NonNull String text) {
        addLog(context, text, null);
    }

    public static void addLog(@NonNull Context context, @NonNull String text, @Nullable Throwable throwable) {
        if (sIsEnabled) {
            try {
                Class<?> clazz = Class.forName("ru.bartwell.ultradebugger.module.logger.Logger");
                Method method = clazz.getMethod("addLog", Context.class, String.class, Throwable.class);
                method.invoke(null, context, text, throwable);
            } catch (Exception ignored) {
            }
        }
    }

    public static void saveValue(@NonNull Context context, @NonNull String key, @Nullable Object value) {
        if (sIsEnabled) {
            try {
                Class<?> clazz = Class.forName("ru.bartwell.ultradebugger.module.logger.Logger");
                Method method = clazz.getMethod("saveValue", Context.class, String.class, Object.class);
                method.invoke(null, context, key, value);
            } catch (Exception ignored) {
            }
        }
    }
}
