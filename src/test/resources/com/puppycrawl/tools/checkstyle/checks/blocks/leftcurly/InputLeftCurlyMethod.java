package com.puppycrawl.tools.checkstyle.checks.blocks.leftcurly;

/*
 * Config: default
 */
class InputLeftCurlyMethod
{ // violation
    InputLeftCurlyMethod() {}
    InputLeftCurlyMethod(String aOne) { // ok
    }
    InputLeftCurlyMethod(int aOne)
    { // violation
    }

    void method1() {}
    void method2() { // ok
    }
    void method3()
    { // violation
    }
    void                                                               method4()
    { // violation
    }
    void method5(String aOne,
                 String aTwo)
    { // violation
    }
    void method6(String aOne,
                 String aTwo) { // ok
    }
}

enum InputLeftCurlyMethodEnum
{ // violation
    CONSTANT1("hello")
    { // violation
        void method1() {}
        void method2() { // ok
        }
        void method3()
        { // violation
        }
        void                                                               method4()
        { // violation
        }
        void method5(String aOne,
                     String aTwo)
        { // violation
        }
        void method6(String aOne,
                     String aTwo) { // ok
        }
    },

    CONSTANT2("hello") { // ok

    },

    CONSTANT3("hellohellohellohellohellohellohellohellohellohellohellohellohellohello")
    { // violation
    };

    private InputLeftCurlyMethodEnum(String value)
    { // violation

    }

    void method1() {}
    void method2() { // ok
    }
    void method3()
    { // violation
    }
    void                                                               method4()
    { // violation
    }
    void method5(String aOne,
                 String aTwo)
    { // violation
    }
    void method6(String aOne,
                 String aTwo) { // ok
    }
}

