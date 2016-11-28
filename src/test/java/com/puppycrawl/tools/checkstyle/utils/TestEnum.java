package com.puppycrawl.tools.checkstyle.utils;

public enum TestEnum {
    E1,

    E2 {
        @Override
        public final void v() {
        }
    };

    public void v() {
    }
}
