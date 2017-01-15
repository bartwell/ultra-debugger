package ru.bartwell.ultradebugger.base.model;

/**
 * Created by BArtWell on 05.01.2017.
 */

public class HttpResponse {

    private static final String TEXT_HTML = "text/html";
    private Status mStatus;
    private String mContentType;
    private String mContent;

    public HttpResponse(String content) {
        mStatus = Status.OK;
        mContentType = TEXT_HTML;
        mContent = content;
    }

    public String getContent() {
        return mContent;
    }

    public String getContentType() {
        return mContentType;
    }

    public Status getStatus() {
        return mStatus;
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
