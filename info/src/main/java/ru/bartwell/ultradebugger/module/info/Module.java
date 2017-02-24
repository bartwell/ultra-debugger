package ru.bartwell.ultradebugger.module.info;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import ru.bartwell.ultradebugger.base.BaseModule;
import ru.bartwell.ultradebugger.base.html.Content;
import ru.bartwell.ultradebugger.base.html.Image;
import ru.bartwell.ultradebugger.base.html.Link;
import ru.bartwell.ultradebugger.base.html.Page;
import ru.bartwell.ultradebugger.base.html.Table;
import ru.bartwell.ultradebugger.base.model.HttpRequest;
import ru.bartwell.ultradebugger.base.model.HttpResponse;
import ru.bartwell.ultradebugger.base.utils.CommonUtils;

/**
 * Created by BArtWell on 15.02.2017.
 */

public class Module extends BaseModule {
    private static final String SCREENSHOT_URI = "screenshot";

    public Module(@NonNull Context context, @NonNull String moduleId) {
        super(context, moduleId);
    }

    @NonNull
    @Override
    public String getName() {
        return getString(R.string.info_name);
    }

    @NonNull
    @Override
    public String getDescription() {
        return getString(R.string.info_description);
    }

    @NonNull
    @Override
    public HttpResponse handle(@NonNull HttpRequest request) {
        String screenshotFullUri = "/" + getModuleId() + "/" + SCREENSHOT_URI;
        if (request.getUri().contains(screenshotFullUri)) {
            InputStream inputStream = getScreenshotStream();
            if (inputStream == null) {
                return new HttpResponse(HttpResponse.Status.INTERNAL_SERVER_ERROR);
            } else {
                return new HttpResponse("image/jpeg", inputStream);
            }
        } else {
            Page page = new Page();
            page.setTitle(getName());

            Image image = new Image(screenshotFullUri, 200, Image.SIZE_NO_SPECIFIED);
            Link link = new Link(screenshotFullUri, image);

            Table table = new DeviceInfoHelper(getContext()).buildTable();

            Content content = new Content();
            content.add(link);
            content.add(table);

            page.setContent(content);

            return new HttpResponse(page.toHtml());
        }
    }

    @Nullable
    private InputStream getScreenshotStream() {
        try {
            Activity activity = CommonUtils.getCurrentActivity();
            if (activity != null) {
                View rootView = activity.getWindow().getDecorView().getRootView();
                rootView.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
                rootView.setDrawingCacheEnabled(false);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                byte[] bytes = outputStream.toByteArray();
                return new ByteArrayInputStream(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
