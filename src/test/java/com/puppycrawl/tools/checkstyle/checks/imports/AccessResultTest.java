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

package com.puppycrawl.tools.checkstyle.checks.imports;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AccessResultTest {

    /* Additional test for jacoco, since valueOf()
     * is generated by javac and jacoco reports that
     * valueOf() is uncovered.
     */
    @Test
    public void testAccessResultValueOf() {
        final AccessResult result = AccessResult.valueOf("ALLOWED");
        assertEquals("Invalid access result", AccessResult.ALLOWED, result);
    }

    /* Additional test for jacoco, since values()
     * is generated by javac and jacoco reports that
     * values() is uncovered.
     */
    @Test
    public void testAccessResultValues() {
        final AccessResult[] actual = AccessResult.values();
        final AccessResult[] expected = {
            AccessResult.ALLOWED,
            AccessResult.DISALLOWED,
            AccessResult.UNKNOWN,
        };
        assertArrayEquals("Invalid access result values", expected, actual);
    }
}
