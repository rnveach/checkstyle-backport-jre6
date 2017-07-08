////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2017 the original author or authors.
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

package com.puppycrawl.tools.checkstyle.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Test;

/**
 * Test cases for {@link SeverityLevel} enumeration.
 * @author Mehmet Can Cömert
 */
public class SeverityLevelTest {

    /* Additional test for jacoco, since valueOf()
     * is generated by javac and jacoco reports that
     * valueOf() is uncovered.
     */
    @Test
    public void testSeverityLevelValueOf() {
        final SeverityLevel level = SeverityLevel.valueOf("INFO");
        assertEquals(SeverityLevel.INFO, level);
    }

    @Test
    public void testMisc() {
        final SeverityLevel severityLevel = SeverityLevel.getInstance("info");
        assertNotNull(severityLevel);
        assertEquals("info", severityLevel.toString());
        assertEquals("info", severityLevel.getName());

        try {
            SeverityLevel.getInstance("unknown");
            fail("exception expected");
        }
        catch (IllegalArgumentException ex) {
            assertEquals(
                    "No enum const class com.puppycrawl.tools.checkstyle.api.SeverityLevel.UNKNOWN",
                    ex.getMessage());
        }
    }

    @Test
    public void testMixedCaseSpaces() {
        SeverityLevel.getInstance("IgnoRe ");
        SeverityLevel.getInstance(" iNfo");
        SeverityLevel.getInstance(" WarniNg");
        SeverityLevel.getInstance("    ERROR ");
    }

    @Test
    public void testMixedCaseSpacesWithDifferentLocales() {
        final Locale[] differentLocales = {new Locale("TR", "tr") };
        final Locale defaultLocale = Locale.getDefault();
        try {
            for (Locale differentLocale : differentLocales) {
                Locale.setDefault(differentLocale);
                testMixedCaseSpaces();
            }
        }
        finally {
            Locale.setDefault(defaultLocale);
        }
    }
}
