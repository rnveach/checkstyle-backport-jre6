////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2021 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.checks.javadoc;

import static com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocContentLocationCheck.MSG_JAVADOC_CONTENT_FIRST_LINE;
import static com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocContentLocationCheck.MSG_JAVADOC_CONTENT_SECOND_LINE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

public class JavadocContentLocationCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/puppycrawl/tools/checkstyle/checks/javadoc/javadoccontentlocation";
    }

    @Test
    public void testGetAcceptableTokens() {
        final JavadocContentLocationCheck checkObj = new JavadocContentLocationCheck();
        final int[] expected = {TokenTypes.BLOCK_COMMENT_BEGIN };
        assertArrayEquals(expected, checkObj.getAcceptableTokens(),
                "Acceptable tokens are invalid");
    }

    @Test
    public void testGetDefaultTokens() {
        final JavadocContentLocationCheck checkObj = new JavadocContentLocationCheck();
        final int[] expected = {TokenTypes.BLOCK_COMMENT_BEGIN };
        assertArrayEquals(expected, checkObj.getDefaultTokens(), "Default tokens are invalid");
    }

    @Test
    public void testDefault() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(JavadocContentLocationCheck.class);
        final String[] expected = {
            "17:5: " + getCheckMessage(MSG_JAVADOC_CONTENT_SECOND_LINE),
            "21:5: " + getCheckMessage(MSG_JAVADOC_CONTENT_SECOND_LINE),
        };
        verify(checkConfig, getPath("InputJavadocContentLocationDefault.java"), expected);
    }

    @Test
    public void testFirstLine() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(JavadocContentLocationCheck.class);
        checkConfig.addProperty("location", "FIRST_LINE");
        final String[] expected = {
            "12:5: " + getCheckMessage(MSG_JAVADOC_CONTENT_FIRST_LINE),
            "21:5: " + getCheckMessage(MSG_JAVADOC_CONTENT_FIRST_LINE),
        };
        verify(checkConfig, getPath("InputJavadocContentLocationFirstLine.java"), expected);
    }

    @Test
    public void testPackage() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(JavadocContentLocationCheck.class);
        checkConfig.addProperty("location", "SECOND_LINE");
        final String[] expected = {
            "8:1: " + getCheckMessage(MSG_JAVADOC_CONTENT_SECOND_LINE),
        };
        verify(checkConfig, getPath("package-info.java"), expected);
    }

    @Test
    public void testInterface() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(JavadocContentLocationCheck.class);
        checkConfig.addProperty("location", "FIRST_LINE");
        final String[] expected = {
            "10:1: " + getCheckMessage(MSG_JAVADOC_CONTENT_FIRST_LINE),
        };
        verify(checkConfig, getPath("InputJavadocContentLocationInterface.java"), expected);
    }

    @Test
    public void testOptionalSpacesAndAsterisks() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(JavadocContentLocationCheck.class);
        final String[] expected = CommonUtil.EMPTY_STRING_ARRAY;
        verify(checkConfig, getPath("InputJavadocContentLocationTrailingSpace.java"), expected);
    }

}
