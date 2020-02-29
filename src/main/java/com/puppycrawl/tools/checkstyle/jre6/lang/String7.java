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

package com.puppycrawl.tools.checkstyle.jre6.lang;

import java.util.Arrays;

public final class String7 {
    private String7() {
    }

    public static String join(String delimiter, String[] elements) {
        return join(delimiter, Arrays.asList(elements));
    }

    public static String join(String delimiter, Iterable<String> elements) {
        String result = "";
        boolean first = true;

        for (String element : elements) {
            if (first) {
                first = false;
            }
            else {
                result += delimiter;
            }

            result += element;
        }

        return result;
    }
}
