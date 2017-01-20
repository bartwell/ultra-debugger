package ru.bartwell.ultradebugger.base.html;

/**
 * Created by BArtWell on 07.01.2017.
 */

public class Page {
    private String mTitle;
    private Content mContent;
    private boolean mShowHomeButton = true;
    private LinksList mLinksList = new LinksList();

    public void setTitle(String title) {
        mTitle = title;
    }

    public Content getContent() {
        return mContent;
    }

    public void setContent(Content content) {
        mContent = content;
    }

    public void setSingleContentPart(ContentPart contentPart) {
        Content content = new Content();
        content.add(contentPart);
        setContent(content);
    }

    public String toHtml() {
        String result = "<html><title>" + mTitle + "</title></head><body>";
        if (mShowHomeButton) {
            mLinksList.add("/", "Back to modules list");
        }
        result += mLinksList.toHtml() + mContent.toHtml() + "</body></html>";
        return result;
    }

    public void showHomeButton(boolean show) {
        mShowHomeButton = show;
    }

    public void addNavigationLink(String url, String label) {
        mLinksList.add(url, label);
    }
}