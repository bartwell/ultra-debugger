package ru.bartwell.ultradebugger.base;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.bartwell.ultradebugger.base.model.HttpRequest;
import ru.bartwell.ultradebugger.base.model.HttpResponse;

/**
 * Created by BArtWell on 04.01.2017.
 */

public abstract class BaseModule {
    private Context mContext;

    public BaseModule(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    @Nullable
    protected String getString(@StringRes int stringResId) {
        return mContext.getString(stringResId);
    }

    public Map<String, String> getMapFromParameters(Map<String, List<String>> parameters, String name) {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
            if (entry.getKey().startsWith(name + "[")) {
                result.put(entry.getKey().substring(name.length() + 1, entry.getKey().length() - 1), entry.getValue().get(0));
            }
        }
        return result;
    }

    @Nullable
    public String getParameterValue(@Nullable Map<String, List<String>> parameters, @Nullable String key) {
        if (parameters != null && key != null && parameters.get(key) != null) {
            return parameters.get(key).get(0);
        }
        return null;
    }

    @Nullable
    public abstract String getName();

    @Nullable
    public abstract String getDescription();

    public abstract HttpResponse handle(HttpRequest request);
}
