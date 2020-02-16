////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2020 the original author or authors.
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

package com.puppycrawl.tools.checkstyle.checks.coding;

import static com.puppycrawl.tools.checkstyle.checks.coding.SimplifyBooleanExpressionCheck.MSG_KEY;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class SimplifyBooleanExpressionCheckTest
    extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/puppycrawl/tools/checkstyle/checks/coding/simplifybooleanexpression";
    }

    @Test
    public void testIt() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(SimplifyBooleanExpressionCheck.class);
        final String[] expected = {
            "20:18: " + getCheckMessage(MSG_KEY),
            "41:36: " + getCheckMessage(MSG_KEY),
            "42:36: " + getCheckMessage(MSG_KEY),
            "43:16: " + getCheckMessage(MSG_KEY),
            "43:32: " + getCheckMessage(MSG_KEY),
        };
        verify(checkConfig, getPath("InputSimplifyBooleanExpression.java"), expected);
    }

    @Test
    public void testTokensNotNull() {
        final SimplifyBooleanExpressionCheck check = new SimplifyBooleanExpressionCheck();
        assertNotNull(check.getAcceptableTokens(), "Acceptable tokens should not be null");
        assertNotNull(check.getDefaultTokens(), "Default tokens should not be null");
        assertNotNull(check.getRequiredTokens(), "Required tokens should not be null");
    }

}
