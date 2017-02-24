package ru.bartwell.ultradebugger.module.files;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

import ru.bartwell.ultradebugger.base.BaseModule;
import ru.bartwell.ultradebugger.base.html.Content;
import ru.bartwell.ultradebugger.base.html.HeadingContentPart;
import ru.bartwell.ultradebugger.base.html.Link;
import ru.bartwell.ultradebugger.base.html.Page;
import ru.bartwell.ultradebugger.base.html.RawContentPart;
import ru.bartwell.ultradebugger.base.html.Table;
import ru.bartwell.ultradebugger.base.model.HttpRequest;
import ru.bartwell.ultradebugger.base.model.HttpResponse;
import ru.bartwell.ultradebugger.base.utils.CommonUtils;
import ru.bartwell.ultradebugger.base.utils.HttpUtils;

/**
 * Created by BArtWell on 24.02.2017.
 */

public class Module extends BaseModule {
    private static final String SIZE_UNITS = "kMGTPE";
    private static final int SIZE_DIVISOR = 1024;
    private static final String PERMISSION_READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    private static final String PERMISSION_WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static final String PARAMETER_PATH = "path";
    private static final String PARAMETER_REMOVE = "remove";
    private static final String PATH_PART_DOWNLOAD_FILE = "download";

    public Module(@NonNull Context context, @NonNull String moduleId) {
        super(context, moduleId);
    }

    @NonNull
    @Override
    public String getName() {
        return getString(R.string.files_name);
    }

    @NonNull
    @Override
    public String getDescription() {
        return getString(R.string.files_description);
    }

    @NonNull
    @Override
    public HttpResponse handle(@NonNull HttpRequest request) {
        HttpResponse permissionsPage = CommonUtils.requestPermissions(PERMISSION_READ_EXTERNAL_STORAGE, PERMISSION_WRITE_EXTERNAL_STORAGE);
        if (permissionsPage != null) {
            return permissionsPage;
        }

        String downloadPath = "/" + getModuleId() + "/" + PATH_PART_DOWNLOAD_FILE;

        if (request.getUri().startsWith(downloadPath + "/")) {
            InputStream inputStream = getFileStream(request.getUri().substring(downloadPath.length()));
            if (inputStream == null) {
                return new HttpResponse(HttpResponse.Status.INTERNAL_SERVER_ERROR);
            } else {
                return new HttpResponse(getMimeType(downloadPath), inputStream);
            }
        } else {
            Page page = new Page();
            page.setTitle(getName());

            File externalStorage = getExternalStorage();
            if (externalStorage != null) {
                page.addNavigationLink(getPathLink(externalStorage.getAbsolutePath()), "SD Card");
            }

            File filesDir = getContext().getApplicationContext().getFilesDir().getParentFile();
            page.addNavigationLink(getPathLink(filesDir.getAbsolutePath()), "Application directory");

            Content content = new Content();

            String remove = HttpUtils.getParameterValue(request.getParameters(), PARAMETER_REMOVE);
            if (!TextUtils.isEmpty(remove)) {
                if (new File(remove).delete()) {
                    content.add(new HeadingContentPart(3, "File removed"));
                } else {
                    content.add(new HeadingContentPart(3, "Error while file removing"));
                }
            }

            File directory = new File(getDirectoryPath(request));

            if (directory.getParentFile() != null) {
                content.add(new Link(getPathLink(directory.getParent()), "&#8624; Parent directory"));
            }

            content.add(new HeadingContentPart(3, directory.getAbsolutePath()));

            File[] files = directory.listFiles();
            if (files != null) {
                if (files.length > 0) {
                    Table table = new Table();
                    int i = 0;
                    Arrays.sort(files, new FilesComparator());
                    for (File file : files) {
                        if (file.isDirectory()) {
                            table.add(0, i, new Link(getPathLink(file.getAbsolutePath()), file.getName()));
                        } else {
                            table.add(0, i, new Link(downloadPath + file.getAbsolutePath(), file.getName()));
                        }
                        table.add(1, i, new RawContentPart(getHumanReadableFileSize(file.length())));
                        table.add(2, i, new RawContentPart(getFormattedDate(file.lastModified())));
                        table.add(3, i, new RawContentPart(getPermissions(file)));
                        table.add(4, i, new Link(getPathLink(directory.getAbsolutePath()) + "&" + PARAMETER_REMOVE + "=" + file.getAbsolutePath(), "Remove"));
                        i++;
                    }
                    content.add(table);
                } else {
                    content.add(new HeadingContentPart(3, "Directory is empty"));
                }
            }
            page.setContent(content);
            return new HttpResponse(page.toHtml());
        }
    }

    @Nullable
    private InputStream getFileStream(@NonNull String file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NonNull
    private String getMimeType(@NonNull String file) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(file);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        if (TextUtils.isEmpty(type)) {
            type = "application/octet-stream";
        }
        return type;
    }

    @NonNull
    private String getPathLink(@NonNull String path) {
        return "?" + PARAMETER_PATH + "=" + path;
    }

    @NonNull
    private String getDirectoryPath(HttpRequest request) {
        String path = HttpUtils.getParameterValue(request.getParameters(), PARAMETER_PATH);
        if (TextUtils.isEmpty(path)) {
            File storage = getExternalStorage();
            if (storage == null) {
                path = "/";
            } else {
                path = storage.getAbsolutePath();
            }
        }
        return path;
    }

    @Nullable
    private File getExternalStorage() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory();
        }
        return null;
    }

    @NonNull
    private String getPermissions(@NonNull File file) {
        return (file.canRead() ? "r" : "-") +
                (file.canWrite() ? "w" : "-") +
                (file.canExecute() ? "x" : "-");
    }

    @NonNull
    private String getFormattedDate(long time) {
        return String.valueOf(DateFormat.format("yyyy-MM-dd HH:mm:ss", time));
    }

    @NonNull
    private String getHumanReadableFileSize(long bytes) {
        if (bytes < SIZE_DIVISOR) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(SIZE_DIVISOR));
        String pre = String.valueOf(SIZE_UNITS.charAt(exp - 1));
        return String.format(Locale.getDefault(), "%.1f %sB", bytes / Math.pow(SIZE_DIVISOR, exp), pre);
    }

    private class FilesComparator implements Comparator<File> {
        @Override
        public int compare(@NonNull File file1, @NonNull File file2) {
            if (file1.isDirectory() && !file2.isDirectory()) {
                return -1;
            } else if (!file1.isDirectory() && file2.isDirectory()) {
                return 1;
            } else {
                return file1.getName().compareToIgnoreCase(file2.getName());
            }
        }
    }
}
