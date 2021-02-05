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

public final class Integer7 {
    public static final int BYTES = Integer.SIZE / Byte.SIZE;

    private Integer7() {
    }

    public static int compare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    public static int parseUnsignedInt(String s, int radix) throws NumberFormatException {
        if (s == null) {
            throw new NumberFormatException("null");
        }

        final int len = s.length();
        if (len > 0) {
            final char firstChar = s.charAt(0);
            if (firstChar == '-') {
                throw new NumberFormatException(String.format("Illegal leading minus sign "
                        + "on unsigned string %s.", s));
            }

            final long result;

            if (
                // Integer.MAX_VALUE in Character.MAX_RADIX is 6 digits
                len <= 5
                    // Integer.MAX_VALUE in base 10 is 10 digits
                    || (radix == 10 && len <= 9)) {
                result = Integer.parseInt(s, radix);
            }
            else {
                result = Long.parseLong(s, radix);
                if ((result & 0xffffffff00000000L) != 0) {
                    throw new NumberFormatException(String.format("String value %s exceeds "
                            + "range of unsigned int.", s));
                }
            }

            return (int) result;
        }

        throw new NumberFormatException("For input string: \"" + s + "\"");
    }
}
