package ru.bartwell.ultradebugger.base.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by BArtWell on 17.02.2017.
 */

public class HttpUtils {
    @NonNull
    public static Map<String, String> getMapFromParameters(@Nullable Map<String, List<String>> parameters, @Nullable String name) {
        Map<String, String> result = new HashMap<>();
        if (parameters != null && name != null) {
            for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
                if (entry.getKey().startsWith(name + "[")) {
                    result.put(entry.getKey().substring(name.length() + 1, entry.getKey().length() - 1), entry.getValue().get(0));
                }
            }
        }
        return result;
    }

    @NonNull
    public static List<String> getListFromParameters(@Nullable Map<String, List<String>> parameters, @Nullable String name) {
        if (parameters != null && name != null) {
            List<String> values = parameters.get(name + "[]");
            if (values != null) {
                return values;
            }
        }
        return new ArrayList<>();
    }

    @Nullable
    public static String getParameterValue(@Nullable Map<String, List<String>> parameters, @Nullable String key) {
        if (parameters != null && key != null && parameters.get(key) != null) {
            return parameters.get(key).get(0);
        }
        return null;
    }

    @NonNull
    public static String getQueryStringFromArray(@NonNull String paramName, @Nullable String[] array, boolean startWithAmp) {
        if (array != null && array.length > 0) {
            return (startWithAmp ? "&" : "?") +
                    paramName + "[]=" +
                    TextUtils.join("&" + paramName + "[]=", array);
        }
        return "";
    }
}
