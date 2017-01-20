package ru.bartwell.ultradebugger.base.html;

/**
 * Created by BArtWell on 07.01.2017.
 */

public class ErrorPage extends Page {

    public ErrorPage(String errorText) {
        this(errorText, true);
    }

    public ErrorPage(String errorText, boolean showHomeButton) {
        Content content = new Content();
        content.add(new RawContentPart("<p align=\"center\">" + errorText + "</p>"));
        setContent(content);
        setTitle("Error");
        showHomeButton(showHomeButton);
    }
}
