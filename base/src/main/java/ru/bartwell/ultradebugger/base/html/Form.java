package ru.bartwell.ultradebugger.base.html;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by BArtWell on 07.01.2017.
 */

public class Form extends ContentPart {

    private final Table mTable;
    private int mRow;
    private StringBuilder mHiddens = new StringBuilder();

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
        mTable.add(1, mRow, new RawContentPart("<input type=\"text\" name=\"" + TextUtils.htmlEncode(name) + "\" value=\"" + TextUtils.htmlEncode(value) + "\">"));
        mRow++;
    }

    public void addHidden(String name, String value) {
        mHiddens.append("<input type=\"hidden\" name=\"")
                .append(TextUtils.htmlEncode(name))
                .append("\" value=\"")
                .append(TextUtils.htmlEncode(value))
                .append("\">");
    }

    public void addSelect(String label, String name, ArrayList<String> values) {
        mTable.add(0, mRow, new RawContentPart(label));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<select name=\"")
                .append(TextUtils.htmlEncode(name))
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
        mTable.add(1, mRow, new RawContentPart("<input type=\"submit\" value=\"" + TextUtils.htmlEncode(label) + "\">"));
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
        stringBuilder.append(mHiddens.toString());
        stringBuilder.append(mTable.toHtml())
                .append("</form>");
        return stringBuilder.toString();
    }
}
