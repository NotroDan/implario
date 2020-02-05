package net.minecraft.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Reflect {
    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... args) {
        return Exceptions.getThrowsEx(() -> clazz.getConstructor(args), true);
    }

    public static <T> T create(Constructor<T> constructor, Object... args) {
        return Exceptions.getThrowsEx(() -> constructor.newInstance(args), false);
    }

    public static Object getFromField(Field field, Object invoke) {
        return Exceptions.getThrowsEx(() -> field.get(invoke));
    }

    public static void setToField(Field field, Object invoke, Object set){
        Exceptions.runThrowsEx(() -> field.set(invoke, set), false);
    }

    public static Object voidExecute(Object invoke, Method method, boolean ignoreEx, Object... args){
        return Exceptions.getThrowsEx(() -> method.invoke(invoke, args), ignoreEx);
    }

    public static Object voidExecute(Object invoke, Method method, Object... args){
        return voidExecute(invoke, method, true, args);
    }

    public static Object voidExecute(Object invoke, String method){
        return voidExecute(invoke, getMethod(invoke.getClass(), method));
    }

    public static Method getMethod(Class clazz, String method, Class... args){
        return Exceptions.getThrowsEx(() -> clazz.getMethod(method, args));
    }

    public static String getEnumName(Object invoke){
        return voidExecute(invoke, "name").toString();
    }

    public static Object getPrimitive(Field field, Object object) {
        if(object == null)return null;
        Class clazz = field.getType();
        return Exceptions.getThrowsEx(() -> {
            if(clazz == String.class)return getFromField(field, object).toString();
            if(clazz == boolean.class)return field.getBoolean(object);
            if(clazz == long.class)return field.getLong(object);
            if(clazz == int.class)return field.getInt(object);
            if(clazz == short.class)return field.getShort(object);
            if(clazz == byte.class)return field.getByte(object);
            return null;
        }, false);
    }

    public static boolean isFinal(Field field){
        return Modifier.isFinal(field.getModifiers());
    }

    public static boolean isStatic(Field field){
        return Modifier.isStatic(field.getModifiers());
    }

    public static boolean isTransient(Field field){
        return Modifier.isTransient(field.getModifiers());
    }

    public static boolean isEditable(Field field){
        return !isFinal(field) && !isStatic(field) && !isTransient(field);
    }
}
