/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.microprofile.faulttolerance.cdi.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceException;

public class AbstractAnnotationConfig<T extends Annotation> implements Annotation {

    private static final String KEY_PREFIX = "ft$";

    private final Class<T> annotationType;
    private final HashMap<String, Object> config = new HashMap<>();

    public AbstractAnnotationConfig(Class<?> annotatedClass, T annotation) {
        this(getPropertyKeyPrefix(annotatedClass), annotation);
    }

    public AbstractAnnotationConfig(Method annotatedMethod, T annotation) {
        this(getPropertyKeyPrefix(annotatedMethod), annotation);
    }

    private AbstractAnnotationConfig(String prefix, T annotation) {
        this.annotationType = (Class<T>) annotation.getClass();

        Method[] methods = this.annotationType.getDeclaredMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            try {
                Object value = method.invoke(annotation);
                String key = getPropertyKey(prefix, annotationType, methodName);
                Object override = getSystemProperty(key, method.getReturnType());

                if (override == null) {
                    key = getPropertyKey(KEY_PREFIX, annotationType, methodName);
                    override = getSystemProperty(key, method.getReturnType());
                }

                if (override != null) {
                    value = override;
                }

                if (value != null) {
                    config.put(methodName, value);
                }

            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                //TODO NLS (when I have merged Ben's changes in)
                throw new FaultToleranceException();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Class<T> annotationType() {
        return annotationType;
    }

    protected <S> S getValue(String key, Class<S> type) {
        @SuppressWarnings("unchecked")
        S value = (S) config.get(key);
        return value;
    }

    private static String getSystemProperty(String key) {
        String value = System.getProperty(key);
        return value;
    }

    private static <S> S getSystemProperty(String key, Class<S> type) {
        String strValue = getSystemProperty(key);
        S value = null;

        if (strValue != null) {
            value = DefaultConverters.convert(strValue, type);
        }

        return value;
    }

    private static String getPropertyKey(String prefix, Class<?> annotationType, String parameter) {
        // <prefix>Annotation/parameter
        String key = prefix + annotationType.getSimpleName() + "/" + parameter;
        return key;
    }

    private static String getPropertyKeyPrefix(Method method) {
        // ft$<classname>/methodname/......
        Class<?> clazz = method.getDeclaringClass();
        String key = KEY_PREFIX + clazz.getName() + "/" + method.getName() + "/";
        return key;
    }

    private static String getPropertyKeyPrefix(Class<?> clazz) {
        // ft$<classname>/.......
        String key = KEY_PREFIX + clazz.getName() + "/";
        return key;
    }
}
