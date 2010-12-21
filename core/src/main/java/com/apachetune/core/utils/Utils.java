package com.apachetune.core.utils;

import com.apachetune.core.AppManager;
import com.apachetune.core.ApplicationException;
import com.apachetune.core.errorreportsystem.ErrorReportManager;
import com.apachetune.core.preferences.PreferencesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.zip.GZIPOutputStream;

import static javax.swing.JOptionPane.*;
import static org.apache.commons.lang.StringUtils.left;
import static org.apache.commons.lang.StringUtils.right;
import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notNull;

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

    public static RuntimeException createRuntimeException(Throwable cause) {
        return new RuntimeException("Internal error", cause);
    }

    public static RuntimeException createRuntimeException(String message) {
        return new RuntimeException(message);
    }

    public static RuntimeException createRuntimeException(String message, Throwable cause) {
        return new RuntimeException(message, cause);
    }

    public static String deleteSubstring(String text, int startIdx, int length) {
        notNull(text, "Argument text cannot be a null [text = \"" + text + "\"]");

        isTrue(text.length() > 0, "Argument text cannot be zero length.");

        isTrue((startIdx >= 0) && (startIdx < text.length()),
                "Invalid argument startIdx value [startIdx = " + startIdx + "]");

        isTrue((startIdx + length < text.length()), "Invalid argument length value [length = " + startIdx + "]");

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
        notNull(filePath, "Argument filePath cannot be a null [filePath = \"" + filePath + "\"]");

        isTrue(maxSize >= 1, "Argument maxSize must be greate than zero [maxSize = " + maxSize + ']');

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

    public static void showSendErrorReportDialog(Component parent, String errorMessage, Throwable cause,
                                                 AppManager appManager, PreferencesManager preferencesManager,
                                                 boolean showSendCancelDialog) {
        if (showSendCancelDialog) {
            if (showConfirmDialog(
                    parent,
                    "An error occurred.\n\n" +
                    "You can help us by sending error report to our developer team.",
                    "Error",
                    OK_CANCEL_OPTION) == CANCEL_OPTION) {
                return;
            }
        }

        ErrorReportManager.getInstance().sendErrorReport(parent, errorMessage, cause, appManager, preferencesManager);
    }

    public static byte[] gzip(String content) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            GZIPOutputStream gzipOS = new GZIPOutputStream(bos);

            gzipOS.write(content.getBytes("UTF-8"));

            gzipOS.close();
        } catch (IOException e) {
            throw createRuntimeException(e);
        }

        return bos.toByteArray();
    }
}
