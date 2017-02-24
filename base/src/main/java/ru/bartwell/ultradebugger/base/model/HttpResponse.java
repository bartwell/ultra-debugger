package ru.bartwell.ultradebugger.base.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.InputStream;

/**
 * Created by BArtWell on 05.01.2017.
 */

public class HttpResponse {

    private static final String CONTENT_TYPE_TEXT_HTML = "text/html";

    @NonNull
    private Status mStatus;
    @NonNull
    private String mContentType;
    @Nullable
    private String mContent;
    @Nullable
    private InputStream mStream;
    private long mContentLength;

    public HttpResponse(@NonNull String content) {
        mStatus = Status.OK;
        mContentType = CONTENT_TYPE_TEXT_HTML;
        mContent = content;
    }

    @SuppressWarnings("WeakerAccess")
    public HttpResponse(@NonNull String contentType, @NonNull InputStream stream, long contentLength) {
        mStatus = Status.OK;
        mContentType = contentType;
        mStream = stream;
        mContentLength = contentLength;
    }

    public HttpResponse(@NonNull String contentType, @NonNull InputStream stream) {
        this(contentType, stream, -1);
    }

    public HttpResponse(@NonNull Status status) {
        mStatus = status;
        mContentType = CONTENT_TYPE_TEXT_HTML;
    }

    @Nullable
    public String getContent() {
        return mContent;
    }

    @NonNull
    public String getContentType() {
        return mContentType;
    }

    @NonNull
    public Status getStatus() {
        return mStatus;
    }

    @Nullable
    public InputStream getStream() {
        return mStream;
    }

    public long getContentLength() {
        return mContentLength;
    }

    public enum Status {
        OK(200, "OK"),
        BAD_REQUEST(400, "Bad request"),
        NOT_FOUND(404, "Not found"),
        INTERNAL_SERVER_ERROR(500, "Internal server error");

        private int mCode;
        private String mDescription;

        Status(int code, String description) {
            mCode = code;
            mDescription = description;
        }

        public int getCode() {
            return mCode;
        }

        public String getDescription() {
            return mDescription;
        }
    }
}
