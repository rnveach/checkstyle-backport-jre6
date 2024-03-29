/*
DescendantToken
limitedTokens = LITERAL_THIS, LITERAL_NULL
minimumDepth = (default)0
maximumDepth = 1
minimumNumber = (default)0
maximumNumber = 1
sumTokenCounts = true
minimumMessage = this cannot be null.
maximumMessage = (default)null
tokens = NOT_EQUAL, EQUAL


*/

package com.puppycrawl.tools.checkstyle.checks.descendanttoken;

public class InputDescendantTokenReturnFromFinally3 {
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
            if (System.currentTimeMillis() == 0) {
                return; // return from if statement
            }
        }
    }
    public void thisNull() {
        boolean result = (this == null) || (null == this); // violation
        boolean result2 = (this != null) && (null != this);
        boolean result3 = (this.getClass().getName()
            == String.valueOf(null == System.getProperty("abc")));
    }
}
