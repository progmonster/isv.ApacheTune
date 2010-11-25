package com.apachetune.core;

import com.apachetune.core.utils.Utils;
import org.apache.commons.lang.StringUtils;

import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notNull;

/**
 * FIXDOC
 *
 * @author <a href="mailto:progmonster@gmail.com">Aleksey V. Katorgin</a>
 * @version 1.0
 */
public class AppVersion {
    private static final String DEFAULT_FORMAT = "{major}.{minor}-{edition}-{build}";

    private String major;

    private String minor;

    private String edition;

    private String build;

    private static final String MAJOR_SUBSTITUTION = "{major}";

    private static final String MINOR_SUBSTITUTION = "{minor}";
    
    private static final String EDITION_SUBSTITUTION = "{edition}";

    private static final String UPPER_EDITION_SUBSTITUTION = "{Edition}";

    private static final String BUILD_SUBSTITUTION = "{build}";

    /**
     * <p>Parses a version given in single line to its components.</p>
     *
     * <p>Format: <i><b>&lt;major&gt;.&lt;minor&gt;[-&lt;edition&gt;[-&lt;build&gt;]]</b></i>, where
     * <i><b>&lt;major&gt;</b></i> is a major version number, <i><b>&lt;minor&gt;</b></i> is a minor version number,
     * <i><b>&lt;edition&gt;</b></i> (optional) - a product edition (for example &quot;lite&quot;,
     * &quot;professional&quot;), <i><b>&lt;build&gt;</b></i> (optional) - any additional info about application version
     * (for example &quot;alpha-SNAPSHOT&quot;).</p>   
     *
     * @param versionLine a version as a single line.
     */
    public AppVersion(String versionLine) {
        notNull(versionLine, "Argument versionLine cannot be a null");

        parseVersionLine(versionLine);
    }

    private void parseVersionLine(String versionLine) {
        int majorSeparatorIndex = versionLine.indexOf('.');

        isTrue(majorSeparatorIndex != -1, "Invalid versionLine format");

        major = versionLine.substring(0, majorSeparatorIndex);

        isTrue(majorSeparatorIndex + 1 != versionLine.length(), "Invalid versionLine format");

        int minorSeparatorIndex = versionLine.indexOf('-', majorSeparatorIndex + 1);

        if (minorSeparatorIndex == -1) {
            minorSeparatorIndex = versionLine.length(); 
        }

        minor = versionLine.substring(majorSeparatorIndex + 1, minorSeparatorIndex);

        if (minorSeparatorIndex == versionLine.length()) {
            return;            
        }

        int editionSeparatorIndex = versionLine.indexOf('-', minorSeparatorIndex + 1);

        if (editionSeparatorIndex == -1) {
            editionSeparatorIndex = versionLine.length();
        }

        edition = versionLine.substring(minorSeparatorIndex + 1, editionSeparatorIndex);
        
        if (editionSeparatorIndex == versionLine.length()) {
            return;
        }

        build = versionLine.substring(editionSeparatorIndex + 1);
    }

    /**
     * <p>Format version components by given format string.</p>
     *
     * <p>Format string is regular string that may contains special words to be substituted by version components.<br />
     * There are such words:
     * <ul>
     *      <li>{major} - major version;</li>
     *      <li>{minor} - minor version;</li>
     *      <li>{edition} or {Edition} - product edition (in second case name of edition will start with an upper case
     * char);</li>
     *      <li>{build} - additional build info.</li>
     * </ul>
     * </p>
     *
     * <p>PATCH: If edition or build component is absent then one char before ones will be removed as well.</p>
     *
     * @param format a format string.
     * 
     * @return Version as string formatted by given format string.
     */
    public String format(String format) {
        notNull(format, "Argument format cannot be a null");

        isTrue(EDITION_SUBSTITUTION.length() == UPPER_EDITION_SUBSTITUTION.length());

        String result = StringUtils.replace(format, MAJOR_SUBSTITUTION, getMajor());

        result = StringUtils.replace(result, MINOR_SUBSTITUTION, getMinor());

        if (getEdition() == null) {
            int editionIdx = StringUtils.indexOf(result, EDITION_SUBSTITUTION);
            
            if ((editionIdx != -1) && (editionIdx > 0)) {
                result = Utils.deleteSubstring(result, editionIdx - 1, 1);
            }
        }

        String edition = StringUtils.defaultString(getEdition());

        result = StringUtils.replace(result, EDITION_SUBSTITUTION, edition);

        String upperEdition = StringUtils.capitalize(edition);

        result = StringUtils.replace(result, UPPER_EDITION_SUBSTITUTION, upperEdition);

        if (getBuild() == null) {
            int buildIdx = StringUtils.indexOf(result, BUILD_SUBSTITUTION);
            
            if ((buildIdx != -1) && (buildIdx > 0)) {
                result = Utils.deleteSubstring(result, buildIdx - 1, 1);
            }
        }

        result = StringUtils.replace(result, BUILD_SUBSTITUTION, StringUtils.defaultString(getBuild()));

        return result;
    }

    /**
     * <p>Formats a version to string with default format <i><b>{major}.{minor}-{Edition}-{build}</b></i>.</p>
     *
     * @return A version line formatted with default format.
     * 
     * @see #format(String) 
     */
    public String format() {
        return format(DEFAULT_FORMAT);
    }

    public String getMajor() {
        return major;
    }

    public String getMinor() {
        return minor;
    }

    public String getEdition() {
        return edition;
    }

    public String getBuild() {
        return build;
    }
}
