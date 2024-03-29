/*
SummaryJavadocCheckTest
violateExecutionOnNonTightHtml = (default)false
forbiddenSummaryFragments = (default)^$
period = _


*/

package com.puppycrawl.tools.checkstyle.checks.javadoc.summaryjavadoc;

public class InputSummaryJavadocPeriod
{
    /** // violation
     * As of JDK 1.1, replaced by {@link #setBounds(int,int,int,int)}
     */
    void foo3() {}

    /**
     * Blabla
     */
    void foo4() throws Exception {}

    /** An especially short bit of Javadoc_ */
    void foo5() {}

    /**
     * An especially short bit of Javadoc_
     */
    void foo6() {}

    /**
     * {@summary An especially short bit of Javadoc_}
     */
    void foo7(){}

    /**
     * {@summary An especially short bit of Javadoc}
     */
    void foo8() {}
}
