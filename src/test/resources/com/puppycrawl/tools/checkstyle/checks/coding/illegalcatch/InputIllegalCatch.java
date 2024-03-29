/*
IllegalCatch
illegalClassNames = (default)Error, Exception, RuntimeException, Throwable, java.lang.Error, \
                    java.lang.Exception, java.lang.RuntimeException, java.lang.Throwable


*/

package com.puppycrawl.tools.checkstyle.checks.coding.illegalcatch;

public class InputIllegalCatch {
    public void foo() {
        try { //class names
        } catch (RuntimeException e) { // violation
        } catch (Exception e) {
        } catch (Throwable e) {
        }
    }

    public void bar() {
        try { /* fully qualified class names */
        } catch (java.lang.RuntimeException e) {
        } catch (java.lang.Exception e) {
        } catch (java.lang.Throwable e) {
        }
    }
}
