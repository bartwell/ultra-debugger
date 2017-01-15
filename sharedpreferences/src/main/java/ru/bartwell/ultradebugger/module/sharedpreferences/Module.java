package ru.bartwell.ultradebugger.module.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ru.bartwell.ultradebugger.base.BaseModule;
import ru.bartwell.ultradebugger.base.Utils;
import ru.bartwell.ultradebugger.base.html.ErrorPage;
import ru.bartwell.ultradebugger.base.html.Form;
import ru.bartwell.ultradebugger.base.html.LinksList;
import ru.bartwell.ultradebugger.base.html.Page;
import ru.bartwell.ultradebugger.base.html.RawContentPart;
import ru.bartwell.ultradebugger.base.model.HttpRequest;
import ru.bartwell.ultradebugger.base.model.HttpResponse;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by BArtWell on 04.01.2017.
 */

public class Module extends BaseModule {
    private static final String PARAMETER_FILE = "file";
    private static final String PARAMETER_EDIT = "edit";
    private static final String PARAMETER_SAVE = "save";
    private static final String PARAMETER_DELETE = "delete";
    private static final String PARAMETER_VALUE = "value";
    private static final String PARAMETER_TYPE = "type";
    private static final String SET_DELIMITER = ";";

    private Map<String, Class<?>> mTypes = new HashMap<>();

    public Module(Context context) {
        super(context);
        mTypes.put("String", String.class);
        mTypes.put("Boolean", Boolean.class);
        mTypes.put("Integer", Integer.class);
        mTypes.put("Float", Float.class);
        mTypes.put("Long", Long.class);
        mTypes.put("Set", Set.class);
    }

    @Nullable
    @Override
    public String getName() {
        return getString(R.string.sharedpreferences_name);
    }

    @Nullable
    @Override
    public String getDescription() {
        return getString(R.string.sharedpreferences_description);
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        Page page;
        String file = getParameterValue(request.getParameters(), PARAMETER_FILE);
        if (file == null) {
            page = showFilesList();
        } else {
            String edit = getParameterValue(request.getParameters(), PARAMETER_EDIT);
            if (edit == null) {
                String delete = getParameterValue(request.getParameters(), PARAMETER_DELETE);
                if (delete != null) {
                    deleteItem(file, delete);
                }
                String save = getParameterValue(request.getParameters(), PARAMETER_SAVE);
                if (save != null) {
                    saveItem(file, save, request);
                }
                page = showItemsList(file);
            } else {
                page = showForm(file, edit);
            }
        }
        page.setTitle(getName());
        return new HttpResponse(page.toHtml());
    }

    @NonNull
    private Page showForm(@NonNull String file, @Nullable String key) {
        try {
            boolean isEdit = !TextUtils.isEmpty(key);
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(file, MODE_PRIVATE);

            Page page = new Page();
            page.addNavigationLink("?" + PARAMETER_FILE + "=" + file, "Back to items");
            Form form = new Form();
            form.setAction("?" + PARAMETER_FILE + "=" + file);
            if (isEdit) {
                form.addHidden(PARAMETER_SAVE, key);
                form.addHidden(PARAMETER_TYPE, getType(sharedPreferences, key));
                form.addInputText(key, PARAMETER_VALUE, getStringFromObject(getValueAsObject(sharedPreferences, key)));
            } else {
                form.addSelect("Type", PARAMETER_TYPE, new ArrayList<>(mTypes.keySet()));
                form.addInputText("Key", PARAMETER_SAVE, "");
                form.addInputText("Value", PARAMETER_VALUE, "");
            }
            form.addSubmit("Save");
            page.setSingleContentPart(form);
            return page;
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorPage("Can't show form: " + e.getMessage(), true);
        }
    }

    @NonNull
    private Page showItemsList(String file) {
        try {
            Page page = new Page();
            page.setTitle(getName());
            page.addNavigationLink("?" + PARAMETER_FILE + "=" + file + "&" + PARAMETER_EDIT + "=", "New item");
            page.addNavigationLink("?", "Files list");
            StringBuilder stringBuilder = new StringBuilder();
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(file, MODE_PRIVATE);
            for (Map.Entry<String, ?> entry : sharedPreferences.getAll().entrySet()) {
                stringBuilder.append("<b>")
                        .append(entry.getKey())
                        .append("</b><br/>")
                        .append(getStringFromObject(entry.getValue()))
                        .append("<br/>")
                        .append("<a href=\"?" + PARAMETER_FILE + "=")
                        .append(file)
                        .append("&")
                        .append(PARAMETER_EDIT)
                        .append("=")
                        .append(entry.getKey())
                        .append("\">Edit</a> ")
                        .append("<a href=\"?" + PARAMETER_FILE + "=")
                        .append(file)
                        .append("&")
                        .append(PARAMETER_DELETE)
                        .append("=")
                        .append(entry.getKey())
                        .append("\">Remove</a><br/><br/>");
            }
            page.setSingleContentPart(new RawContentPart(stringBuilder.toString()));
            return page;
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorPage("Can't read items: " + e.getMessage(), true);
        }
    }

    @NonNull
    private Page showFilesList() {
        try {
            File directory = new File(getContext().getApplicationInfo().dataDir, "shared_prefs");
            if (directory.exists() && directory.isDirectory()) {
                String[] list = directory.list();
                if (list.length > 0) {
                    LinksList linksList = new LinksList();
                    for (String file : list) {
                        file = Utils.trimFileExtension(file);
                        linksList.add("?" + PARAMETER_FILE + "=" + file, file);
                    }
                    Page page = new Page();
                    page.setSingleContentPart(linksList);
                    return page;
                }
            }
            return new ErrorPage("No shared preferences files available", true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorPage("Can't get files list: " + e.getMessage(), true);
        }
    }

    private void deleteItem(String file, String key) {
        try {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(file, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(key);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveItem(@NonNull String file, @NonNull String key, HttpRequest request) {
        try {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(file, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String type = getParameterValue(request.getParameters(), PARAMETER_TYPE);
            String value = getParameterValue(request.getParameters(), PARAMETER_VALUE);
            if (value != null) {
                if ("String".equals(type)) {
                    editor.putString(key, value);
                } else if ("Boolean".equals(type)) {
                    editor.putBoolean(key, Boolean.parseBoolean(value));
                } else if ("Integer".equals(type)) {
                    editor.putInt(key, Integer.parseInt(value));
                } else if ("Long".equals(type)) {
                    editor.putLong(key, Long.parseLong(value));
                } else if ("Float".equals(type)) {
                    editor.putFloat(key, Float.parseFloat(value));
                } else if ("Set".equals(type)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        editor.putStringSet(key, new HashSet<>(Arrays.asList(value.split(SET_DELIMITER))));
                    }
                }
            }
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private String getType(@NonNull SharedPreferences sharedPreferences, @NonNull String key) {
        Object value = getValueAsObject(sharedPreferences, key);
        for (Map.Entry<String, Class<?>> entry : mTypes.entrySet()) {
            if (entry.getValue().isInstance(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private Object getValueAsObject(SharedPreferences sharedPreferences, String key) {
        Map<String, ?> all = sharedPreferences.getAll();
        return all.get(key);
    }

    private String getStringFromObject(Object value) {
        if (value instanceof Set) {
            StringBuilder stringBuilder = new StringBuilder();
            Set<?> strings = (Set<?>) value;
            boolean isFirst = true;
            for (Object string : strings) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    stringBuilder.append(SET_DELIMITER);
                }
                stringBuilder.append(String.valueOf(string));
            }
            return stringBuilder.toString();
        }
        return String.valueOf(value);
    }
}
