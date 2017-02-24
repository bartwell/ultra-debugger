package ru.bartwell.ultradebugger.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import ru.bartwell.ultradebugger.base.model.HttpRequest;
import ru.bartwell.ultradebugger.base.model.HttpResponse;

/**
 * Created by BArtWell on 04.01.2017.
 */

public abstract class BaseModule {
    @NonNull
    private Context mContext;
    private String mModuleId;

    public BaseModule(@NonNull Context context, @NonNull String moduleId) {
        mContext = context;
        mModuleId = moduleId;
    }

    @NonNull
    public Context getContext() {
        return mContext;
    }

    @NonNull
    protected String getModuleId() {
        return mModuleId;
    }

    @NonNull
    protected String getString(@StringRes int stringResId) {
        return mContext.getString(stringResId);
    }

    @NonNull
    public abstract String getName();

    @NonNull
    public abstract String getDescription();

    @NonNull
    public abstract HttpResponse handle(@NonNull HttpRequest request);
}
