package ru.bartwell.ultradebugger.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by BArtWell on 07.01.2017.
 */

public class Utils {
    public static boolean isNumber(@Nullable String str) {
        return str == null || str.matches("-?\\d+");
    }

    @NonNull
    public static String trimFileExtension(@NonNull String fileName) {
        int dot = fileName.lastIndexOf(".");
        if (dot > 0) {
            return fileName.substring(0, dot);
        } else {
            return fileName;
        }
    }
}
