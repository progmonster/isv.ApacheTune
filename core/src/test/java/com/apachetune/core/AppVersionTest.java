package com.apachetune.core;

import static org.testng.Assert.*;
import static org.testng.FileAssert.fail;
import org.testng.annotations.*;

/**
 * FIXDOC
 *
 * @author <a href="mailto:aleksey.katorgin@trustverse.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
@Test
public class AppVersionTest {
    @Test
    public void testParseVersionLineFull() {
        String versionLine = "12.14-lite-alpha-SNAPSHOT";

        AppVersion appVersion = new AppVersion(versionLine);

        assertEquals(appVersion.getMajor(), "12");
        assertEquals(appVersion.getMinor(), "14");
        assertEquals(appVersion.getEdition(), "lite");
        assertEquals(appVersion.getBuild(), "alpha-SNAPSHOT");

        versionLine = "12.14-lite-beta";

        appVersion = new AppVersion(versionLine);

        assertEquals(appVersion.getBuild(), "beta");
    }

    @Test
    public void testParseVersionLineWithoutBuild() {
        String versionLine = "12.14-lite";

        AppVersion appVersion = new AppVersion(versionLine);

        assertEquals(appVersion.getMajor(), "12");
        assertEquals(appVersion.getMinor(), "14");
        assertEquals(appVersion.getEdition(), "lite");
        assertEquals(appVersion.getBuild(), null);
    }

    @Test
    public void testParseVersionLineWithoutBuildAndEdition() {
        String versionLine = "12.14";

        AppVersion appVersion = new AppVersion(versionLine);

        assertEquals(appVersion.getMajor(), "12");
        assertEquals(appVersion.getMinor(), "14");
        assertEquals(appVersion.getEdition(), null);
        assertEquals(appVersion.getBuild(), null);
    }

    @Test
    public void testFailParseVersionLineWithoutMinor() {
        String versionLine = "12";

        try {
            new AppVersion(versionLine);

            fail();
        } catch (Exception e) {
            // No-op.
        }

        versionLine = "12.";

        try {
            new AppVersion(versionLine);

            fail();
        } catch (Exception e) {
            // No-op.
        }
    }

    @Test
    public void formatVersionLine() {
        String versionLine = "12.14-lite-alpha-SNAPSHOT";

        AppVersion appVersion = new AppVersion(versionLine);

        String formattedVersionLine = appVersion.format("//{major}:{minor}-{edition}_{build}//");        

        assertEquals(formattedVersionLine, "//12:14-lite_alpha-SNAPSHOT//");
    }

    @Test
    public void formatVersionLineWithoutBuildAndEdition() {
        String versionLine = "12.14";

        AppVersion appVersion = new AppVersion(versionLine);

        String formattedVersionLine = appVersion.format("//{major}:{minor}-{edition}_{build}//");

        assertEquals(formattedVersionLine, "//12:14//");
    }
}
