package com.goldenglow.common.util;

import noppes.npcs.controllers.ScriptContainer;

import javax.script.ScriptEngine;
import java.lang.reflect.Field;

public class ReflectionHelper {

    public static <T> T getPrivateValue(Object obj, String fieldName) {
        try {
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return (T) f.get(obj);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setPrivateValue(Object obj, String fieldName, Object value) {
        try {
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(obj, value);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T getScriptedValue(ScriptContainer scriptContainer, String fieldName) {
        try {
            ScriptEngine engine = getPrivateValue(scriptContainer, "engine");
            return (T) engine.getContext().getAttribute(fieldName);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
