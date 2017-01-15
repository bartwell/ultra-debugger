package ru.bartwell.ultradebugger.base.model;

import java.util.List;
import java.util.Map;

/**
 * Created by BArtWell on 05.01.2017.
 */

public class HttpRequest {
    private Map<String, List<String>> mParameters;
    private Method mMethod;
    private String mUri;

    public HttpRequest(String uri, Method method, Map<String, List<String>> parameters) {
        mUri = uri;
        mMethod = method;
        mParameters = parameters;
    }

    public Map<String, List<String>> getParameters() {
        return mParameters;
    }

    public Method getMethod() {
        return mMethod;
    }

    public String getUri() {
        return mUri;
    }

    public enum Method {
        GET,
        POST
    }
}

