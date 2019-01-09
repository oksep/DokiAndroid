package com.dokiwa.dokidoki.center.plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


class EmptyApiProxy implements InvocationHandler {

    static <T> T newInstance(Class<T> clazz) {
        return (T) Proxy
                .newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new EmptyApiProxy());
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        return defaultValueByType(method.getReturnType());
    }

    private Object defaultValueByType(Class type) {
        if (type == boolean.class) {
            return false;
        } else if (type == int.class) {
            return 0;
        } else if (type == short.class) {
            return (short) 0;
        } else if (type == char.class) {
            return (char) 0;
        } else if (type == byte.class) {
            return (byte) 0;
        } else if (type == long.class) {
            return 0L;
        } else if (type == float.class) {
            return 0f;
        } else if (type == double.class) {
            return 0D;
        } else {
            return null;
        }
    }
}