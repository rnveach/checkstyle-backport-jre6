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

package org.junit.jupiter.api;

import org.junit.Assert;
import org.junit.jupiter.api.function.Executable;

public final class Assertions {
    private Assertions() {
    }

    public static void assertEquals(Object expected, Object actual, String message) {
        Assert.assertEquals(message, expected, actual);
    }

    public static void assertEquals(double expected, double actual, double delta, String message) {
        Assert.assertEquals(message, expected, actual, delta);
    }

    public static void assertNotEquals(Object expected, Object actual, String message) {
        Assert.assertNotEquals(message, expected, actual);
    }

    public static void assertNotNull(Object actual, String message) {
        Assert.assertNotNull(message, actual);
    }

    public static void assertNull(Object actual, String message) {
        Assert.assertNull(message, actual);
    }

    public static void assertArrayEquals(Object[] expected, Object[] actual, String message) {
        Assert.assertArrayEquals(message, expected, actual);
    }

    public static void assertArrayEquals(int[] expected, int[] actual, String message) {
        Assert.assertArrayEquals(message, expected, actual);
    }

    public static void assertTrue(boolean condition, String message) {
        Assert.assertTrue(message, condition);
    }

    public static void assertFalse(boolean condition, String message) {
        Assert.assertFalse(message, condition);
    }

    public static void assertNotSame(Object expected, Object actual, String message) {
        Assert.assertNotSame(message, expected, actual);
    }

    public static void assertSame(Object expected, Object actual, String message) {
        Assert.assertSame(message, expected, actual);
    }

    public static void fail(String message) {
        Assert.fail(message);
    }

    public static <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable) {
        return AssertThrows.assertThrows(expectedType, executable);
    }
}
