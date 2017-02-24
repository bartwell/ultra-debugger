package ru.bartwell.ultradebugger.base.html;

/**
 * Created by BArtWell on 17.02.2017.
 */

public class Image extends ContentPart {
    public static final int SIZE_NO_SPECIFIED = -1;
    private final String mUrl;
    private final int mWidth;
    private final int mHeight;

    public Image(String url, int width, int height) {
        mUrl = url;
        mWidth = width;
        mHeight = height;
    }

    @Override
    public String toHtml() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<img src=\"");
        stringBuilder.append(mUrl);
        stringBuilder.append("\"");
        if (mWidth != SIZE_NO_SPECIFIED) {
            stringBuilder.append(" width=\"");
            stringBuilder.append(mWidth);
            stringBuilder.append("\"");
        }
        if (mHeight != SIZE_NO_SPECIFIED) {
            stringBuilder.append(" height=\"");
            stringBuilder.append(mHeight);
            stringBuilder.append("\"");
        }
        stringBuilder.append(" alt=\"Image\"/>");
        return stringBuilder.toString();
    }
}
