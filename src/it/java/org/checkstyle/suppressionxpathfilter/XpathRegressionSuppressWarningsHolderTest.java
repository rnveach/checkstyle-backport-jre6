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

package org.checkstyle.suppressionxpathfilter;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.checks.SuppressWarningsHolder;

public class XpathRegressionSuppressWarningsHolderTest extends AbstractXpathTestSupport {

    private final String checkName = SuppressWarningsHolder.class.getSimpleName();

    @Override
    protected String getCheckName() {
        return checkName;
    }

    @Test
    public void testOne() throws Exception {
        final File fileToProcess =
                new File(getPath("SuppressionXpathRegressionSuppressWarningsHolderOne.java"));

        final DefaultConfiguration moduleConfig =
                createModuleConfig(SuppressWarningsHolder.class);

        final String[] expectedViolation = {
            "10:14: " + getCheckMessage(SuppressWarningsHolder.class,
                     SuppressWarningsHolder.MSG_KEY),
        };

        final List<String> expectedXpathQueries = Arrays.asList(
                "/CLASS_DEF[./IDENT[@text='SuppressionXpathRegressionSuppressWarningsHolderOne']]"
                    + "/OBJBLOCK/METHOD_DEF[./IDENT[@text='main']]"
                    + "/SLIST/LITERAL_TRY/RESOURCE_SPECIFICATION/RESOURCES",
                "/CLASS_DEF[./IDENT[@text='SuppressionXpathRegressionSuppressWarningsHolderOne']]"
                    + "/OBJBLOCK/METHOD_DEF[./IDENT[@text='main']]"
                    + "/SLIST/LITERAL_TRY/RESOURCE_SPECIFICATION/RESOURCES"
                    + "/RESOURCE[./IDENT[@text='resource']]",
                "/CLASS_DEF[./IDENT[@text='SuppressionXpathRegressionSuppressWarningsHolderOne']]"
                    + "/OBJBLOCK/METHOD_DEF[./IDENT[@text='main']]"
                    + "/SLIST/LITERAL_TRY/RESOURCE_SPECIFICATION/RESOURCES"
                    + "/RESOURCE[./IDENT[@text='resource']]/MODIFIERS",
                "/CLASS_DEF[./IDENT[@text='SuppressionXpathRegressionSuppressWarningsHolderOne']]"
                    + "/OBJBLOCK/METHOD_DEF[./IDENT[@text='main']]"
                    + "/SLIST/LITERAL_TRY/RESOURCE_SPECIFICATION/RESOURCES"
                    + "/RESOURCE[./IDENT[@text='resource']]/MODIFIERS"
                    + "/ANNOTATION[./IDENT[@text='SuppressWarnings']]",
                "/CLASS_DEF[./IDENT[@text='SuppressionXpathRegressionSuppressWarningsHolderOne']]"
                    + "/OBJBLOCK/METHOD_DEF[./IDENT[@text='main']]"
                    + "/SLIST/LITERAL_TRY/RESOURCE_SPECIFICATION/RESOURCES"
                    + "/RESOURCE[./IDENT[@text='resource']]/MODIFIERS"
                    + "/ANNOTATION[./IDENT[@text='SuppressWarnings']]/AT"
        );

        runVerifications(moduleConfig, fileToProcess, expectedViolation,
                expectedXpathQueries);
    }

}
