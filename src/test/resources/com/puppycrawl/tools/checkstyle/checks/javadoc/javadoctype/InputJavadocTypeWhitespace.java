////////////////////////////////////////////////////////////////////////////////
// Test case file for checkstyle.
// Created: 2001
////////////////////////////////////////////////////////////////////////////////
package com . puppycrawl
    .tools.
    checkstyle.checks.javadoc.javadoctype;

/* Config:
 *
 * authorFormat = "\\S"
 */

/**
 * Class for testing javadoc issues.
 * violation missing author tag
 **/
class InputJavadocTypeWhitespace // violation
{
    /** another check */
    void donBradman(Runnable aRun)
    {
        donBradman(new Runnable() {
            public void run() {
            }
        });

        final Runnable r = new Runnable() {
            public void run() {
            }
        };
    }

    /** bug 806243 (NoWhitespaceBeforeCheck violation for anonymous inner class) */
    void bug806243()
    {
        Object o = new InputJavadocTypeWhitespace() {
            private int j ;
        };
    }
}

/**
 * Bug 806242 (NoWhitespaceBeforeCheck violation with an interface).
 * @author o_sukhodolsky
 * @version 1.0
 */
interface IFoo
{
    void foo() ;
}

/**
 * Avoid Whitespace violations in for loop.
 * @author lkuehne
 * @version 1.0
 */
class SpecialCasesInForLoop
{
    public void myMethod() {
        new Thread() {
            public void run() {
            }
        }.start();
    }
}
