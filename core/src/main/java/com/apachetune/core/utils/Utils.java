package com.apachetune.core.utils;

import static org.apache.commons.lang.StringUtils.left;
import static org.apache.commons.lang.StringUtils.right;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public final class Utils {
    private Utils() {
        // No-op.
    }

    public static String deleteSubstring(String text, int startIdx, int length) {
        if (text == null) {
            throw new NullPointerException("Argument text cannot be a null [text = \"" + text + "\"]");
        }

        if (text.length() == 0) {
            throw new IllegalArgumentException("Argument text cannot be zero length.");
        }

        if ((startIdx < 0) || (startIdx >= text.length())) {
            throw new IllegalArgumentException("Invalid argument startIdx value [startIdx = " + startIdx + "]");
        }

        if ((startIdx + length >= text.length())) {
            throw new IllegalArgumentException("Invalid argument length value [length = " + startIdx + "]");
        }

        String leftPiece;

        if (startIdx > 0) {
            leftPiece = text.substring(0, startIdx);
        } else {
            leftPiece = "";
        }

        String rightPiece;

        if (startIdx + length < text.length()) {
            rightPiece = text.substring(startIdx + length, text.length());
        } else {
            rightPiece = "";
        }

        return leftPiece + rightPiece;
    }

    public static String abbreviateFilePath(String filePath, int maxSize) {
        if (filePath == null) {
            throw new NullPointerException("Argument filePath cannot be a null [filePath = \"" + filePath + "\"]");
        }

        if (maxSize < 1) {
            throw new IllegalArgumentException("Argument maxSize must be greate than zero [maxSize = " + maxSize + ']');
        }

        String abbreviatedLocation;

        if (filePath.length() <= maxSize) {
            abbreviatedLocation = filePath;
        } else {
            abbreviatedLocation = left(filePath, maxSize / 2) + "......" + right(filePath, maxSize / 2);
        }
        
        return abbreviatedLocation;
    }
}
