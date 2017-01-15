package ru.bartwell.ultradebugger.base.html;

import java.util.ArrayList;

/**
 * Created by BArtWell on 07.01.2017.
 */

public class Content extends ArrayList<ContentPart> {
    public String toHtml() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            stringBuilder.append(get(i).toHtml());
        }
        return stringBuilder.toString();
    }
}
