package ru.bartwell.ultradebugger.base.html;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by BArtWell on 07.01.2017.
 */

public class Table extends ContentPart {

    Map<CellXY, ContentPart> mContent = new HashMap<>();
    private int mMaxX;
    private int mMaxY;

    public void add(int x, int y, ContentPart part) {
        mMaxX = Math.max(mMaxX, x);
        mMaxY = Math.max(mMaxY, y);
        mContent.put(new CellXY(x, y), part);
    }

    @Override
    public String toHtml() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<table border=\"#777777\" cellspacing=\"0\" cellpadding=\"5\">");
        for (int y = 0; y < mMaxY + 1; y++) {
            stringBuilder.append("<tr align=\"center\">");
            for (int x = 0; x < mMaxX + 1; x++) {
                stringBuilder.append("<td>");
                ContentPart part = mContent.get(new CellXY(x, y));
                if (part != null) {
                    stringBuilder.append(part.toHtml());
                }
                stringBuilder.append("</td>");
            }
            stringBuilder.append("</tr>");
        }
        stringBuilder.append("</table>");
        return stringBuilder.toString();
    }

    private class CellXY {
        private final int x;
        private final int y;

        CellXY(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CellXY)) return false;
            CellXY key = (CellXY) o;
            return x == key.x && y == key.y;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }
    }
}

