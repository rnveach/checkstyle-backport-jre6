/*
MatchXpath
query = (default)


*/

package com.puppycrawl.tools.checkstyle.checks.coding.matchxpath;

public class InputMatchXpath {
    public void test() { } // violation

    public void foo() { } // violation

    public void correct() { } // ok
}
