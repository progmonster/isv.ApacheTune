package com.apachetune.core.utils;

import com.apachetune.core.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.apache.commons.lang.StringUtils.left;
import static org.apache.commons.lang.StringUtils.right;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public final class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

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

    public static void close(ResultSet rs) {
        if (rs == null) {
            return;
        }

        try {
            rs.close();
        } catch (SQLException e) {
            logger.error("Error during closing result set", e);
        }
    }

    public static void close(Statement st) {
        if (st == null) {
            return;
        }

        try {
            st.close();
        } catch (SQLException e) {
            logger.error("Error during closing statement", e);
        }
    }

    public static String getChildElementContent(Element element, String childElementName) throws ApplicationException {
        NodeList childElems = element.getElementsByTagName(childElementName);

        if (childElems.getLength() != 1) {
            throw new ApplicationException(
                    "Error during parsing element. Multiple children elements with same name. [child_element_name=" +
                            childElementName + ']');
        }

        String result;

        try {
            Element childElem = (Element) childElems.item(0);

            result = childElem.getTextContent().trim();
        } catch (Throwable cause) {
            throw new ApplicationException("Error during parsing element", cause);
        }

        return result;
    }
}
