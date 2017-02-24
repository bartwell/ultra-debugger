package ru.bartwell.ultradebugger;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ru.bartwell.ultradebugger.base.BaseModule;

/**
 * Created by BArtWell on 04.01.2017.
 */

class ModulesManager {
    private static final String MODULE_CLASS_NAME_FORMAT = "%1$s.module.%2$s.Module";

    @Nullable
    private static ModulesManager sInstance;
    @NonNull
    private Map<String, BaseModule> mModules = new HashMap<>();

    private ModulesManager(@NonNull Context context) {
        for (int i = 0; i < BuildConfig.KNOWN_MODULES.length; i++) {
            try {
                String moduleClassName = String.format(Locale.getDefault(), MODULE_CLASS_NAME_FORMAT,
                        BuildConfig.APPLICATION_ID, BuildConfig.KNOWN_MODULES[i]);
                Class<?> clazz = Class.forName(moduleClassName);
                Constructor<?> constructor = clazz.getConstructor(Context.class, String.class);
                mModules.put(BuildConfig.KNOWN_MODULES[i], (BaseModule) constructor.newInstance(context, BuildConfig.KNOWN_MODULES[i]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void newInstance(Context context) {
        sInstance = new ModulesManager(context);
    }

    @NonNull
    static ModulesManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("You should call newInstance() first");
        }
        return sInstance;
    }

    @NonNull
    Map<String, BaseModule> getAll() {
        return mModules;
    }

    @Nullable
    BaseModule get(@Nullable String module) {
        if (module != null && mModules.containsKey(module)) {
            return mModules.get(module);
        }
        return null;
    }
}