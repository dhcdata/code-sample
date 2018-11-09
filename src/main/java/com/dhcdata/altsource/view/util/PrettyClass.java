package com.dhcdata.altsource.view.util;

import java.lang.reflect.Type;

public interface PrettyClass {

    public String[] toStringArray();

    public static String[] getHeader() {
        return new String[0];
    }

    public static Type getType() {
        return null;
    }

}