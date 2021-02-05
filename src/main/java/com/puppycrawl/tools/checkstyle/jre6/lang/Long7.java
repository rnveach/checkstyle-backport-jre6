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

public final class Long7 {
    public static final long MIN_VALUE = 0x8000000000000000L;

    private Long7() {
    }

    public static long parseUnsignedLong(String s, int radix) throws NumberFormatException {
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
                // Long.MAX_VALUE in Character.MAX_RADIX is 13 digits
                len <= 12
                    // Long.MAX_VALUE in base 10 is 19 digits
                    || (radix == 10 && len <= 18)) {
                result = Long.parseLong(s, radix);
            }
            else {
                // No need for range checks on len due to testing above.
                final long first = Long.parseLong(s.substring(0, len - 1), radix);
                final int second = Character.digit(s.charAt(len - 1), radix);
                if (second < 0) {
                    throw new NumberFormatException("Bad digit at end of " + s);
                }
                result = first * radix + second;
                if (compareUnsigned(result, first) < 0) {
                    /*
                     * The maximum unsigned value, (2^64)-1, takes at most one
                     * more digit to represent than the maximum signed value,
                     * (2^63)-1. Therefore, parsing (len - 1) digits will be
                     * appropriately in-range of the signed parsing. In other
                     * words, if parsing (len -1) digits overflows signed
                     * parsing, parsing len digits will certainly overflow
                     * unsigned parsing.
                     *
                     * The compareUnsigned check above catches situations where
                     * an unsigned overflow occurs incorporating the
                     * contribution of the final digit.
                     */
                    throw new NumberFormatException(String.format("String value %s exceeds "
                            + "range of unsigned long.", s));
                }
            }
            return result;
        }

        throw new NumberFormatException("For input string: \"" + s + "\"");
    }

    public static int compareUnsigned(long x, long y) {
        return compare(x + MIN_VALUE, y + MIN_VALUE);
    }

    public static int compare(long x, long y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }
}
