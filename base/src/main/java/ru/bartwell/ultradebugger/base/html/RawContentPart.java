package ru.bartwell.ultradebugger.base.html;

/**
 * Created by BArtWell on 07.01.2017.
 */
public class RawContentPart extends ContentPart {
    private final String mContent;

    public RawContentPart(String content) {
        super();
        mContent = content;
    }

    @Override
    public String toHtml() {
        return mContent;
    }
}
