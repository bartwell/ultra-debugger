package ru.bartwell.ultradebugger.base.html;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by BArtWell on 07.01.2017.
 */
public class RawContentPart extends ContentPart {
    @NonNull
    private final String mContent;

    public RawContentPart(@Nullable String content) {
        super();
        mContent = content == null ? "null" : content;
    }

    @NonNull
    @Override
    public String toHtml() {
        return mContent;
    }
}
