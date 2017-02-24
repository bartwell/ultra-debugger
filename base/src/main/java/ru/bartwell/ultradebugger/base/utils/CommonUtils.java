package ru.bartwell.ultradebugger.base.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.Map;

import ru.bartwell.ultradebugger.base.html.ErrorPage;
import ru.bartwell.ultradebugger.base.model.HttpResponse;

/**
 * Created by BArtWell on 07.01.2017.
 */

public class CommonUtils {
    public static boolean isNumber(@Nullable String str) {
        return str == null || str.matches("-?\\d+");
    }

    @NonNull
    public static String trimFileExtension(@NonNull String fileName) {
        int dot = fileName.lastIndexOf(".");
        if (dot > 0) {
            return fileName.substring(0, dot);
        } else {
            return fileName;
        }
    }

    @Nullable
    public static Activity getCurrentActivity() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);

            Map<Object, Object> activities = (Map<Object, Object>) activitiesField.get(activityThread);
            if (activities == null)
                return null;

            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    Activity activity = (Activity) activityField.get(activityRecord);
                    return activity;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static HttpResponse requestPermissions(@NonNull String ... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Activity activity = getCurrentActivity();
            if (activity != null && !checkSelfPermissions(activity, permissions)) {
                activity.requestPermissions(permissions, 0);
                return new HttpResponse(new ErrorPage("This module require additional permissions. Please allow it on your smartphone and reload this page.").toHtml());
            }
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static boolean checkSelfPermissions(@NonNull Activity activity, @NonNull String[] permissions) {
        for (String permission : permissions) {
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
