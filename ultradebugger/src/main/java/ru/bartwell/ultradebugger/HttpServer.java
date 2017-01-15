package ru.bartwell.ultradebugger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import ru.bartwell.ultradebugger.base.BaseModule;
import ru.bartwell.ultradebugger.base.html.ErrorPage;
import ru.bartwell.ultradebugger.base.html.LinksList;
import ru.bartwell.ultradebugger.base.html.Page;
import ru.bartwell.ultradebugger.base.model.HttpRequest;
import ru.bartwell.ultradebugger.base.model.HttpResponse;

/**
 * Created by BArtWell on 01.01.2017.
 */

class HttpServer extends NanoHTTPD {
    static final int DEFAULT_PORT = 8080;

    HttpServer(int port) {
        super(port);
        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        if (session.getMethod() == Method.POST) {
            try {
                session.parseBody(new HashMap<String, String>());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        HttpRequest request = new HttpRequest(session.getUri(),
                session.getMethod() == Method.POST ? HttpRequest.Method.POST : HttpRequest.Method.GET,
                session.getParameters());

        String module = getModuleName(request.getUri());
        if (TextUtils.isEmpty(module)) {
            Map<String, BaseModule> modules = ModulesManager.getInstance().getAll();
            if (modules.isEmpty()) {
                return newFixedLengthResponse(new ErrorPage("No modules available", false).toHtml());
            } else {
                LinksList linksList = new LinksList();
                for (Map.Entry<String, BaseModule> entry : modules.entrySet()) {
                    BaseModule instance = entry.getValue();
                    linksList.add("/" + entry.getKey(), instance.getName(), instance.getDescription());
                }
                Page page = new Page();
                page.setTitle("Modules list");
                page.setSingleContentPart(linksList);
                page.showHomeButton(false);
                return newFixedLengthResponse(page.toHtml());
            }
        } else {
            BaseModule instance = ModulesManager.getInstance().get(module);
            if (instance == null) {
                return newFixedLengthResponse(new ErrorPage("Module " + module + " unavailable", false).toHtml());
            } else {
                HttpResponse response = instance.handle(request);
                return newFixedLengthResponse(convertResponseStatus(response.getStatus()), response.getContentType(), response.getContent());
            }
        }
    }

    @Nullable
    private String getModuleName(String uri) {
        String[] segments = uri.split("/");
        if (segments.length > 1) {
            return segments[1];
        }
        return null;
    }

    @NonNull
    private Response.IStatus convertResponseStatus(HttpResponse.Status status) {
        switch (status) {
            case OK:
                return Response.Status.OK;
            case BAD_REQUEST:
                return Response.Status.BAD_REQUEST;
            case NOT_FOUND:
                return Response.Status.NOT_FOUND;
            default:
                return Response.Status.INTERNAL_ERROR;
        }
    }
}