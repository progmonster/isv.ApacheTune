package com.apachetune.core;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class AppVersionTest {
    @Test
    public void testParseVersionLineFull() {
        String versionLine = "12.14-lite-alpha-SNAPSHOT";

        AppVersion appVersion = new AppVersion(versionLine);

        assertThat(appVersion.getMajor()).isEqualTo("12");
        assertThat(appVersion.getMinor()).isEqualTo("14");
        assertThat(appVersion.getEdition()).isEqualTo("lite");
        assertThat(appVersion.getBuild()).isEqualTo("alpha-SNAPSHOT");

        versionLine = "12.14-lite-beta";

        appVersion = new AppVersion(versionLine);

        assertThat(appVersion.getBuild()).isEqualTo("beta");
    }

    @Test
    public void testParseVersionLineWithoutBuild() {
        String versionLine = "12.14-lite";

        AppVersion appVersion = new AppVersion(versionLine);

        assertThat(appVersion.getMajor()).isEqualTo("12");
        assertThat(appVersion.getMinor()).isEqualTo("14");
        assertThat(appVersion.getEdition()).isEqualTo("lite");
        assertThat(appVersion.getBuild()).isNull();
    }

    @Test
    public void testParseVersionLineWithoutBuildAndEdition() {
        String versionLine = "12.14";

        AppVersion appVersion = new AppVersion(versionLine);

        assertThat(appVersion.getMajor()).isEqualTo("12");
        assertThat(appVersion.getMinor()).isEqualTo("14");
        assertThat(appVersion.getEdition()).isNull();
        assertThat(appVersion.getBuild()).isNull();
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

        assertThat(formattedVersionLine).isEqualTo("//12:14-lite_alpha-SNAPSHOT//");
    }

    @Test
    public void formatVersionLineWithoutBuildAndEdition() {
        String versionLine = "12.14";

        AppVersion appVersion = new AppVersion(versionLine);

        String formattedVersionLine = appVersion.format("//{major}:{minor}-{edition}_{build}//");

        assertThat(formattedVersionLine).isEqualTo("//12:14//");
    }
}
