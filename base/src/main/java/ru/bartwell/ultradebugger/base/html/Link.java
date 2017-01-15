package ru.bartwell.ultradebugger.base.html;

/**
 * Created by BArtWell on 07.01.2017.
 */
public class Link extends ContentPart {
    private final String mUrl;
    private final String mLabel;

    public Link(String url, String label) {
        super();
        mUrl = url;
        mLabel = label;
    }

    @Override
    public String toHtml() {
        return "<a href=\"" + mUrl + "\">" + mLabel + "</a>";
    }
}
