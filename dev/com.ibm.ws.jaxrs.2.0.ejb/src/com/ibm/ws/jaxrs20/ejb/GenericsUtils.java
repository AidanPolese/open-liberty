/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20.ejb;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

public class GenericsUtils {
    public static Type getGenericInterfaceParamType(Class<?> cls, Class<?> rawType) {
        while (cls != null) {
            Type[] interfaces = cls.getGenericInterfaces();
            for (Type type : interfaces) {
                if (type instanceof ParameterizedType) {
                    ParameterizedType pType = (ParameterizedType) type;
                    if (pType.getRawType() == rawType) {
                        return pType.getActualTypeArguments()[0];
                    } else {
                        continue;
                    }
                }
                // look through the base interfaces of the current interface
                Type interfaceType = getGenericInterfaceParamType((Class<?>) type, rawType);
                if (interfaceType != null) {
                    return interfaceType;
                }
            }
            cls = cls.getSuperclass();
        }
        // After recursive calls, perhaps the interface is not parameterized
        return null;
    }

    public static Class<?> getClassType(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return (Class<?>) parameterizedType.getRawType();
        }

        if (type instanceof GenericArrayType) {
            GenericArrayType genericArray = (GenericArrayType) type;
            Class<?> classType = getClassType(genericArray.getGenericComponentType());
            return Array.newInstance(classType, 0).getClass();
        }

        if (type instanceof TypeVariable<?>) {
            return getClassType(((TypeVariable<?>) type).getBounds()[0]);
        }

        if (type instanceof WildcardType) {
            return getClassType(((WildcardType) type).getUpperBounds()[0]);
        }
        return null;
    }

}
