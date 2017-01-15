package ru.bartwell.ultradebugger.base.html;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by BArtWell on 07.01.2017.
 */

public class Form extends ContentPart {

    private final Table mTable;
    private int mRow;
    private Map<String, String> mHiddens = new HashMap<>();

    public void setPostMethod(boolean isPostMethod) {
        mIsPostMethod = isPostMethod;
    }

    public void setAction(String action) {
        mAction = action;
    }

    private boolean mIsPostMethod = true;
    private String mAction = "";

    public Form() {
        mTable = new Table();
    }

    public void addInputText(@NonNull String label, @NonNull String name, @Nullable String value) {
        if (value == null) {
            value = "";
        }
        mTable.add(0, mRow, new RawContentPart(label));
        mTable.add(1, mRow, new RawContentPart("<input type=\"text\" name=\"" + name + "\" value=\"" + value + "\">"));
        mRow++;
    }

    public void addHidden(String name, String value) {
        mHiddens.put(name, value);
    }

    public void addSelect(String label, String name, ArrayList<String> values) {
        mTable.add(0, mRow, new RawContentPart(label));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<select name=\"")
                .append(name)
                .append("\">");
        for (String value : values) {
            stringBuilder.append("<option>")
                    .append(value)
                    .append("</option>");
        }
        stringBuilder.append("</select>");
        mTable.add(1, mRow, new RawContentPart(stringBuilder.toString()));
        mRow++;
    }

    public void addSubmit(String label) {
        mTable.add(1, mRow, new RawContentPart("<input type=\"submit\" value=\"" + label + "\">"));
        mRow++;
    }

    @Override
    public String toHtml() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<form action=\"")
                .append(mAction)
                .append("\" method=\"")
                .append(mIsPostMethod ? "post" : "get")
                .append("\">");
        for (Map.Entry<String, String> entry : mHiddens.entrySet()) {
            stringBuilder.append("<input type=\"hidden\" name=\"")
                    .append(entry.getKey())
                    .append("\" value=\"")
                    .append(entry.getValue())
                    .append("\">");
        }
        stringBuilder.append(mTable.toHtml())
                .append("</form>");
        return stringBuilder.toString();
    }
}
