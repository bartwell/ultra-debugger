package ru.bartwell.ultradebugger.base.html;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by BArtWell on 07.01.2017.
 */

public class LinksList extends ContentPart {

    private ArrayList<ListItem> mContent = new ArrayList<>();

    public void add(@NonNull String url, @NonNull String name) {
        add(url, name, null);
    }

    public void add(@NonNull String url, @NonNull String name, @Nullable String description) {
        ListItem listItem = new ListItem(url, name, description);
        mContent.add(listItem);
    }

    @Override
    public String toHtml() {
        StringBuilder stringBuilder = new StringBuilder();
        for (ListItem listItem : mContent) {
            stringBuilder.append("<a href=\"")
                    .append(listItem.getUrl())
                    .append("\">")
                    .append(listItem.getName())
                    .append("</a><br/>");
            if (listItem.getDescription() != null) {
                stringBuilder.append(listItem.getDescription())
                        .append("<br/>");
            }
            stringBuilder.append("<br/>");
        }
        return stringBuilder.toString();
    }

    private class ListItem {
        private final String mUrl;
        private final String mName;
        private final String mDescription;

        ListItem(String url, String name, String description) {
            mUrl = url;
            mName = name;
            mDescription = description;
        }

        String getUrl() {
            return mUrl;
        }

        String getName() {
            return mName;
        }

        String getDescription() {
            return mDescription;
        }
    }
}
