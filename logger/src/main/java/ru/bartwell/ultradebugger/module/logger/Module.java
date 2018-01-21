package ru.bartwell.ultradebugger.module.logger;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import ru.bartwell.ultradebugger.base.BaseModule;
import ru.bartwell.ultradebugger.base.html.Content;
import ru.bartwell.ultradebugger.base.html.HeadingContentPart;
import ru.bartwell.ultradebugger.base.html.Link;
import ru.bartwell.ultradebugger.base.html.Page;
import ru.bartwell.ultradebugger.base.html.RawContentPart;
import ru.bartwell.ultradebugger.base.html.Table;
import ru.bartwell.ultradebugger.base.model.HttpRequest;
import ru.bartwell.ultradebugger.base.model.HttpResponse;
import ru.bartwell.ultradebugger.base.utils.HttpUtils;

/**
 * Created by BArtWell on 24.02.2017.
 */

public class Module extends BaseModule {
    private static final String PARAMETER_CLEAR_LOGS = "clear_logs";
    private static final String PARAMETER_DELETE_VALUE = "delete_value";
    private static final String PARAMETER_DELETE_VALUE_KEY = "delete_value_key";

    private int mLastPageRandom;

    public Module(@NonNull Context context, @NonNull String moduleId) {
        super(context, moduleId);
    }

    @NonNull
    @Override
    public String getName() {
        return getString(R.string.logger_name);
    }

    @NonNull
    @Override
    public String getDescription() {
        return getString(R.string.logger_description);
    }

    @NonNull
    @Override
    public HttpResponse handle(@NonNull HttpRequest request) {
        if (request.getUri().contains(Utils.DOWNLOAD_URI_PART)) {
            InputStream inputStream = getLogsFileStream();
            if (inputStream == null) {
                return new HttpResponse(HttpResponse.Status.INTERNAL_SERVER_ERROR);
            } else {
                HttpResponse response = new HttpResponse("text/plain", inputStream);
                String fileName = Uri.parse(request.getUri()).getLastPathSegment();
                if (fileName.contains(Utils.LOG_FILE_EXTENSION)) {
                    StorageHelper.addLog(getContext(), "--- Download log file " + fileName + " ---");
                    response.addHeader("content-disposition", "attachment; filename=\"" + fileName + "\"");
                }
                return response;
            }
        } else {
            Page page = new Page();
            page.setTitle(getName());
            Content content = new Content();

            StorageHelper.rotateLogs(getContext());

            if (isActionParameterValid(request, PARAMETER_CLEAR_LOGS)) {
                StorageHelper.clearLogs(getContext());
            }
            String deleteValue = HttpUtils.getParameterValue(request.getParameters(), PARAMETER_DELETE_VALUE_KEY);
            if (isActionParameterValid(request, PARAMETER_DELETE_VALUE) && !TextUtils.isEmpty(deleteValue)) {
                StorageHelper.removeValue(getContext(), deleteValue);
            }

            mLastPageRandom = getRandInt();
            page.addNavigationLink(Utils.getDownloadPath(), "Download logs");
            page.addNavigationLink("?" + PARAMETER_CLEAR_LOGS + "=" + mLastPageRandom, "Clear logs");

            Map<String, ?> allValues = StorageHelper.getAllValues(getContext());
            if (!allValues.isEmpty()) {
                content.add(new HeadingContentPart(3, "Saved values"));
                Table table = new Table();
                int i = 0;
                for (Map.Entry<String, ?> entry : allValues.entrySet()) {
                    table.add(0, i, new RawContentPart(entry.getKey()));
                    table.add(1, i, new RawContentPart(String.valueOf(entry.getValue())));
                    table.add(2, i, new Link("?" + PARAMETER_DELETE_VALUE + "=" + mLastPageRandom
                            + "&" + PARAMETER_DELETE_VALUE_KEY + "=" + entry.getKey()
                            , "Remove"));
                    i++;
                }
                content.add(table);
            }

            content.add(new HeadingContentPart(3, "Logs"));
            content.add(new RawContentPart(StorageHelper.readLogs(getContext(), "<br/>")));

            page.setContent(content);
            return new HttpResponse(page.toHtml());
        }
    }

    private int getRandInt() {
        return new Random().nextInt(10000);
    }

    private boolean isActionParameterValid(@NonNull HttpRequest request, @NonNull String parameter) {
        String value = HttpUtils.getParameterValue(request.getParameters(), parameter);
        return !TextUtils.isEmpty(value) && value.equals(String.valueOf(mLastPageRandom));
    }

    @Nullable
    private InputStream getLogsFileStream() {
        try {
            return new ByteArrayInputStream(StorageHelper.readLogs(getContext(), "\r\n").getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
