package com.apachetune;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.testng.Reporter;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public final class TestUtils {
    public static final int EXPECTED_EXCEPTIONS_REPORT_LEVEL = 10;

    public static void reportExpectedException(Throwable cause) {
        Reporter.log(ExceptionUtils.getFullStackTrace(cause), EXPECTED_EXCEPTIONS_REPORT_LEVEL, true);
    }

    public static void failBecauseExceptionWasExpected() {
        org.testng.Assert.fail("It should raise an exception.");
    }
}
