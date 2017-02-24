package ru.bartwell.ultradebugger.module.reflection;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ru.bartwell.ultradebugger.base.BaseModule;
import ru.bartwell.ultradebugger.base.utils.HttpUtils;
import ru.bartwell.ultradebugger.base.utils.CommonUtils;
import ru.bartwell.ultradebugger.base.html.Content;
import ru.bartwell.ultradebugger.base.html.ContentPart;
import ru.bartwell.ultradebugger.base.html.ErrorPage;
import ru.bartwell.ultradebugger.base.html.Form;
import ru.bartwell.ultradebugger.base.html.HeadingContentPart;
import ru.bartwell.ultradebugger.base.html.LinksList;
import ru.bartwell.ultradebugger.base.html.Page;
import ru.bartwell.ultradebugger.base.html.RawContentPart;
import ru.bartwell.ultradebugger.base.html.Table;
import ru.bartwell.ultradebugger.base.model.HttpRequest;
import ru.bartwell.ultradebugger.base.model.HttpResponse;

/**
 * Created by BArtWell on 18.01.2017.
 */

public class Module extends BaseModule {
    private static final String PARAMETER_CALL = "call";
    private static final String PARAMETER_PREPARE = "prepare";
    private static final String PARAMETER_LABEL = "label";
    private static final String PARAMETER_TYPE = "type";
    private static final String PARAMETER_VALUE = "value";
    private static final long METHOD_CALL_TIMEOUT = 15;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public Module(@NonNull Context context, @NonNull String moduleId) {
        super(context, moduleId);
    }


    @NonNull
    @Override
    public String getName() {
        return getString(R.string.reflection_name);
    }

    @NonNull
    @Override
    public String getDescription() {
        return getString(R.string.reflection_description);
    }

    @NonNull
    @Override
    public HttpResponse handle(@NonNull HttpRequest request) {
        Page page = new Page();
        page.setTitle(getName());
        final String prepareMethodName = HttpUtils.getParameterValue(request.getParameters(), PARAMETER_PREPARE);
        if (TextUtils.isEmpty(prepareMethodName)) {
            Content content = new Content();
            final String callMethodName = HttpUtils.getParameterValue(request.getParameters(), PARAMETER_CALL);
            if (callMethodName != null) {
                String callResult = callMethod(callMethodName,
                        HttpUtils.getListFromParameters(request.getParameters(), PARAMETER_TYPE),
                        HttpUtils.getListFromParameters(request.getParameters(), PARAMETER_VALUE));
                Table table = new Table();
                table.add(0, 0, new RawContentPart(callMethodName + "() result"));
                table.add(1, 0, new RawContentPart(callResult));
                content.add(table);
            }
            content.addAll(buildMembersList());
            page.setContent(content);
        } else {
            Form form = new Form();
            form.setAction(request.getUri());
            form.addHidden(PARAMETER_CALL, prepareMethodName);
            List<String> labels = HttpUtils.getListFromParameters(request.getParameters(), PARAMETER_LABEL);
            List<String> parameters = HttpUtils.getListFromParameters(request.getParameters(), PARAMETER_TYPE);
            for (int i = 0; i < parameters.size(); i++) {
                form.addHidden(PARAMETER_TYPE + "[]", parameters.get(i));
                String defValue = "";
                if ("java.lang.String".equals(parameters.get(i))) {
                    defValue = "\"\"";
                } else if ("java.lang.Integer".equals(parameters.get(i)) || "int".equals(parameters.get(i)) ||
                        "java.lang.Double".equals(parameters.get(i)) || "double".equals(parameters.get(i)) ||
                        "java.lang.Float".equals(parameters.get(i)) || "float".equals(parameters.get(i)) ||
                        "java.lang.Long".equals(parameters.get(i)) || "long".equals(parameters.get(i))) {
                    defValue = "0";
                } else if ("java.lang.Boolean".equals(parameters.get(i)) || "boolean".equals(parameters.get(i))) {
                    defValue = "false";
                }
                form.addInputText("Parameter #" + i + " " + labels.get(i), PARAMETER_VALUE + "[]", defValue);
            }
            form.addSubmit("Call method");
            Content content = new Content();
            content.add(form);
            content.add(new RawContentPart("Use JSON format for parameters"));
            page.addNavigationLink(request.getUri(), "Back to members list");
            page.setContent(content);
        }
        return new HttpResponse(page.toHtml());
    }

    private Content buildMembersList() {
        try {
            Activity activity = CommonUtils.getCurrentActivity();
            if (activity == null) {
                return new ErrorPage("Activity not available for listing").getContent();
            } else {
                Class clazz = activity.getClass();
                Content content = new Content();
                content.add(new HeadingContentPart(4, "Fields"));
                content.add(getFieldsList(activity, clazz));
                content.add(new HeadingContentPart(4, "Methods"));
                content.add(getMethodsList(clazz));
                return content;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorPage(e.getMessage()).getContent();
        }
    }

    @NonNull
    private ContentPart getMethodsList(@NonNull Class clazz) {
        LinksList linksList = new LinksList();
        for (Method method : clazz.getDeclaredMethods()) {
            Class[] parameters = method.getParameterTypes();
            String[] names = new String[parameters.length];
            String[] simpleNames = new String[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                names[i] = parameters[i].getName();
                simpleNames[i] = parameters[i].getSimpleName();
            }
            String url;
            if (parameters.length > 0) {
                url = "?" + PARAMETER_PREPARE + "=" + method.getName()
                        + HttpUtils.getQueryStringFromArray(PARAMETER_TYPE, names, true)
                        + HttpUtils.getQueryStringFromArray(PARAMETER_LABEL, simpleNames, true);
            } else {
                url = "?" + PARAMETER_CALL + "=" + method.getName();
            }
            linksList.add(url, method.getName() + "(" + TextUtils.join(", ", simpleNames) + ")");
        }
        return linksList;
    }

    @NonNull
    private ContentPart getFieldsList(@NonNull Activity activity, @NonNull Class clazz) {
        Table table = new Table();
        table.add(0, 0, new RawContentPart("Name"));
        table.add(1, 0, new RawContentPart("Value"));
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (!fields[i].isAccessible()) {
                fields[i].setAccessible(true);
            }
            table.add(0, i, new RawContentPart(fields[i].getName()));
            try {
                table.add(1, i, new RawContentPart(new Gson().toJson(fields[i].get(activity))));
            } catch (Exception e) {
                e.printStackTrace();
                table.add(1, i, new RawContentPart(e.getMessage()));
            }
        }
        return table;
    }

    @NonNull
    private String callMethod(@NonNull final String methodName, @NonNull final List<String> types, @NonNull final List<String> values) {
        if (types.size() != values.size()) {
            return "Form error";
        }
        final String[] result = {""};
        final CountDownLatch latch = new CountDownLatch(1);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Activity activity = CommonUtils.getCurrentActivity();
                    if (activity == null) {
                        result[0] = "Error: Activity not available for call";
                    } else {
                        Class<?>[] classes = new Class[types.size()];
                        Object[] objects = new Object[types.size()];
                        for (int i = 0; i < types.size(); i++) {
                            classes[i] = getClassForType(types.get(i));
                            objects[i] = new Gson().fromJson(values.get(i), classes[i]);
                        }
                        Method method;
                        if (classes.length > 0) {
                            method = activity.getClass().getDeclaredMethod(methodName, classes);
                        } else {
                            method = activity.getClass().getDeclaredMethod(methodName);
                        }
                        if (!method.isAccessible()) {
                            method.setAccessible(true);
                        }
                        Object callResult;
                        if (objects.length > 0) {
                            callResult = method.invoke(activity, objects);
                        } else {
                            callResult = String.valueOf(method.invoke(activity));
                        }
                        if (void.class.equals(method.getReturnType())) {
                            result[0] = "Success: no return value";
                        } else {
                            result[0] = "Success: " + callResult;
                        }
                    }
                } catch (Exception e) {
                    result[0] = "Error: " + e.toString();
                    e.printStackTrace();
                }
                latch.countDown();
            }
        });
        try {
            latch.await(METHOD_CALL_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result[0];
    }

    @NonNull
    private Class getClassForType(@NonNull String type) throws ClassNotFoundException {
        switch (type) {
            case "void":
                return void.class;
            case "boolean":
                return boolean.class;
            case "byte":
                return byte.class;
            case "char":
                return char.class;
            case "short":
                return short.class;
            case "int":
                return int.class;
            case "float":
                return float.class;
            case "double":
                return double.class;
            case "long":
                return long.class;
        }
        return Class.forName(type);
    }
}
