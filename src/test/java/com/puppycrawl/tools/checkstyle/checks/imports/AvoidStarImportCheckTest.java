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

package com.puppycrawl.tools.checkstyle.checks.imports;

import static com.puppycrawl.tools.checkstyle.checks.imports.AvoidStarImportCheck.MSG_KEY;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class AvoidStarImportCheckTest
    extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/puppycrawl/tools/checkstyle/checks/imports/avoidstarimport";
    }

    @Test
    public void testDefaultOperation()
            throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(AvoidStarImportCheck.class);
        final String[] expected = {
            "12:54: " + getCheckMessage(MSG_KEY, "com.puppycrawl."
                    + "tools.checkstyle.checks.imports.*"),
            "14:15: " + getCheckMessage(MSG_KEY, "java.io.*"),
            "15:17: " + getCheckMessage(MSG_KEY, "java.lang.*"),
            "30:42: " + getCheckMessage(MSG_KEY, "javax.swing.WindowConstants.*"),
            "31:42: " + getCheckMessage(MSG_KEY, "javax.swing.WindowConstants.*"),
            "33:27: " + getCheckMessage(MSG_KEY, "java.io.File.*"),
        };

        verify(checkConfig, getPath("InputAvoidStarImportDefault.java"),
                expected);
    }

    @Test
    public void testExcludes()
            throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(AvoidStarImportCheck.class);
        checkConfig.addProperty("excludes",
            "java.io,java.lang,javax.swing.WindowConstants.*");
        // allow the java.io/java.lang,javax.swing.WindowConstants star imports
        final String[] expected2 = {
            "12:54: " + getCheckMessage(MSG_KEY, "com.puppycrawl."
                    + "tools.checkstyle.checks.imports.*"),
            "33:27: " + getCheckMessage(MSG_KEY, "java.io.File.*"),
        };
        verify(checkConfig, getPath("InputAvoidStarImportExcludes.java"),
                expected2);
    }

    @Test
    public void testAllowClassImports() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(AvoidStarImportCheck.class);
        checkConfig.addProperty("allowClassImports", "true");
        // allow all class star imports
        final String[] expected2 = {
            "30:42: " + getCheckMessage(MSG_KEY, "javax.swing.WindowConstants.*"),
            "31:42: " + getCheckMessage(MSG_KEY, "javax.swing.WindowConstants.*"),
            "33:27: " + getCheckMessage(MSG_KEY, "java.io.File.*"), };
        verify(checkConfig, getPath("InputAvoidStarImportAllowClass.java"), expected2);
    }

    @Test
    public void testAllowStaticMemberImports() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(AvoidStarImportCheck.class);
        checkConfig.addProperty("allowStaticMemberImports", "true");
        // allow all static star imports
        final String[] expected2 = {
            "12:54: " + getCheckMessage(MSG_KEY, "com.puppycrawl."
                    + "tools.checkstyle.checks.imports.*"),
            "14:15: " + getCheckMessage(MSG_KEY, "java.io.*"),
            "15:17: " + getCheckMessage(MSG_KEY, "java.lang.*"),
        };
        verify(checkConfig, getPath("InputAvoidStarImportAllowStaticMember.java"), expected2);
    }

    @Test
    public void testGetAcceptableTokens() {
        final AvoidStarImportCheck testCheckObject =
                new AvoidStarImportCheck();
        final int[] actual = testCheckObject.getAcceptableTokens();
        final int[] expected = {TokenTypes.IMPORT, TokenTypes.STATIC_IMPORT};
        assertArrayEquals(expected, actual, "Default acceptable tokens are invalid");
    }

    @Test
    public void testGetRequiredTokens() {
        final AvoidStarImportCheck testCheckObject =
                new AvoidStarImportCheck();
        final int[] actual = testCheckObject.getRequiredTokens();
        final int[] expected = {TokenTypes.IMPORT, TokenTypes.STATIC_IMPORT};

        assertArrayEquals(expected, actual, "Default required tokens are invalid");
    }

}
