package ru.bartwell.ultradebugger.base.html;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

/**
 * Created by BArtWell on 20.01.2017.
 */

public class HeadingContentPart extends ContentPart {
    @IntRange(from = 1, to = 6)
    private int mImportance;
    @NonNull
    private final String mTitle;

    public HeadingContentPart(@IntRange(from = 1, to = 6) int importance, @NonNull String title) {
        super();
        mImportance = importance;
        mTitle = title;
    }

    @NonNull
    @Override
    public String toHtml() {
        return "<h" + mImportance + ">" + mTitle + "</h" + mImportance + ">";

    }
}
