/*
MultipleStringLiterals
allowedDuplicates = 2
ignoreStringsRegexp = (default)^""$
ignoreOccurrenceContext = (default)ANNOTATION


*/

package com.puppycrawl.tools.checkstyle.checks.coding.multiplestringliterals;

public class InputMultipleStringLiterals2
{   /*string literals*/
    String m = "StringContents"; // violation
    String m1 = "SingleString";
    String m2 = "DoubleString" + "DoubleString";
    String m3 = "" + "";
    String m4 = "" + "";
    String debugStr = ", " + ", " + ", ";

    void method1() {
        String a1 = "StringContents";
        System.identityHashCode("StringContents");
        // The following is not reported, since it is two string literals.
        String a2 = "String" + "Contents";
    }

    @SuppressWarnings("unchecked")
    void method2(){}

    @SuppressWarnings("unchecked")
    void method3(){}

    @SuppressWarnings("unchecked")
    void method4(){}

    @SuppressWarnings("unchecked")
    void method5(){}
}
