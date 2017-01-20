package ru.bartwell.ultradebugger.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.bartwell.ultradebugger.base.model.HttpRequest;
import ru.bartwell.ultradebugger.base.model.HttpResponse;

/**
 * Created by BArtWell on 04.01.2017.
 */

public abstract class BaseModule {
    @NonNull
    private Context mContext;

    public BaseModule(@NonNull Context context) {
        mContext = context;
    }

    @NonNull
    public Context getContext() {
        return mContext;
    }

    @Nullable
    protected String getString(@StringRes int stringResId) {
        return mContext.getString(stringResId);
    }

    @NonNull
    protected Map<String, String> getMapFromParameters(@Nullable Map<String, List<String>> parameters, @Nullable String name) {
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
    protected List<String> getListFromParameters(@Nullable Map<String, List<String>> parameters, @Nullable String name) {
        if (parameters != null && name != null) {
            List<String> values = parameters.get(name + "[]");
            if (values != null) {
                return values;
            }
        }
        return new ArrayList<>();
    }

    @Nullable
    protected String getParameterValue(@Nullable Map<String, List<String>> parameters, @Nullable String key) {
        if (parameters != null && key != null && parameters.get(key) != null) {
            return parameters.get(key).get(0);
        }
        return null;
    }

    @NonNull
    protected String getQueryStringFromArray(@NonNull String paramName, @Nullable String[] array, boolean startWithAmp) {
        if (array != null && array.length > 0) {
            return (startWithAmp ? "&" : "?") +
                    paramName + "[]=" +
                    TextUtils.join("&" + paramName + "[]=", array);
        }
        return "";
    }

    @Nullable
    public abstract String getName();

    @Nullable
    public abstract String getDescription();

    @NonNull
    public abstract HttpResponse handle(@NonNull HttpRequest request);
}
