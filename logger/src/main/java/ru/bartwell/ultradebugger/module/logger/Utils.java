package ru.bartwell.ultradebugger.module.logger;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by BArtWell on 18.11.2017.
 */

class Utils {

    private static final String MODULE_ID = "logger";
    static final String DOWNLOAD_URI_PART = "/download/";
    static final String LOG_FILE_EXTENSION = ".log";

    @NonNull
    static String getDownloadPath() {
        return "/" + MODULE_ID + DOWNLOAD_URI_PART + Utils.getFileName();
    }

    @NonNull
    private static String getFileName() {
        String date = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        return date + LOG_FILE_EXTENSION;
    }
}
