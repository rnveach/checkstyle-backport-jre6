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

public final class AssertThrows {
    private AssertThrows() {
    }

    static <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable) {
        return assertThrows(expectedType, executable, (String) null);
    }

    // -@cs[ReturnCount] Unsafe to actually change this.
    @SuppressWarnings("unchecked")
    private static <T extends Throwable> T assertThrows(Class<T> expectedType,
            Executable executable, String givenMessage) {
        try {
            executable.execute();
        }
        catch (Throwable actualException) {
            if (expectedType.isInstance(actualException)) {
                return (T) actualException;
            }
            else {
                Assert.assertEquals(getMessage(givenMessage) + "Unexpected exception type thrown",
                        expectedType, actualException.getClass());
            }
        }

        Assert.fail(getMessage(givenMessage)
                + String.format("Expected %s to be thrown, but nothing was thrown.",
                        expectedType.getCanonicalName()));
        return null;
    }

    private static String getMessage(String message) {
        final String result;

        if (message == null) {
            result = "";
        }
        else {
            result = message + " ==> ";
        }

        return result;
    }
}
