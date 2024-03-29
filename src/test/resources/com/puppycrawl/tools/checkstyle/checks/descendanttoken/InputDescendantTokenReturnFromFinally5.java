/*
DescendantToken
limitedTokens = LITERAL_THIS, LITERAL_NULL
minimumDepth = (default)0
maximumDepth = (default)2147483647
minimumNumber = 3
maximumNumber = (default)2147483647
sumTokenCounts = true
minimumMessage = (default)null
maximumMessage = (default)null
tokens = NOT_EQUAL, EQUAL


*/

package com.puppycrawl.tools.checkstyle.checks.descendanttoken;

public class InputDescendantTokenReturnFromFinally5 {
    public void foo() {
        try {
            System.currentTimeMillis();
        } finally {
            return;
        }
    }

    public void bar() {
        try {
            System.currentTimeMillis();
        } finally {
            if (System.currentTimeMillis() == 0) { // violation
                return; // return from if statement
            }
        }
    }
    public void thisNull() {
        boolean result = (this == null) || (null == this);
        boolean result2 = (this != null) && (null != this);
        boolean result3 = (this.getClass().getName()
            == String.valueOf(null == System.getProperty("abc")));
    }
}
