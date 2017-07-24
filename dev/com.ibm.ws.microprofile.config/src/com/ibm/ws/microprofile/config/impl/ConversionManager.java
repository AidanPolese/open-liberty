/**
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//This class was inspired by com.netflix.archaius.DefaultDecoder

package com.ibm.ws.microprofile.config.impl;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.config.spi.Converter;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.microprofile.config.interfaces.ConfigException;
import com.ibm.ws.microprofile.config.interfaces.ConversionException;
import com.ibm.ws.microprofile.config.interfaces.ConverterNotFoundException;

public class ConversionManager {

    private static final TraceComponent tc = Tr.register(ConversionManager.class);

    private final Map<Type, Converter<?>> converters = new HashMap<>();

    /**
     * @param converters all these are stored in the instance
     */
    public ConversionManager(Map<Type, Converter<?>> converters) {
        this.converters.putAll(converters);
    }

    /**
     * Convert a String to a Type using registered converters for the Type
     *
     * @param rawString
     * @param type
     * @return
     */
    public Object convert(String rawString, Type type) {
        Object converted = null;

        if (rawString != null) {
            if (converters.containsKey(type)) {
                Converter<?> converter = converters.get(type);
                if (converter != null) {
                    try {
                        converted = converter.convert(rawString);
                    } catch (ConversionException e) {
                        throw e;
                    } catch (IllegalArgumentException e) {
                        throw new ConversionException(Tr.formatMessage(tc, "conversion.exception.CWMCG0007E", converter.getClass().getName(), rawString, e));
                    } catch (Throwable e) {
                        throw new ConfigException(Tr.formatMessage(tc, "conversion.exception.CWMCG0007E", converter.getClass().getName(), rawString, e));
                    }

                    if (converted == null) {
                        throw new ConfigException(Tr.formatMessage(tc, "converter.returned.null.CWMCG0005E", converter.getClass().getName(), rawString));
                    }
                }
            }

            if (converted == null && type instanceof Class) {
                Class<?> requestedClazz = (Class<?>) type;

                //TODO array conversion works just fine but isn't in this version of the spec
//                if (requestedClazz.isArray()) {
//                    Class<?> arrayType = requestedClazz.getComponentType();
//                    converted = convertArray(rawString, arrayType);
//                }

//                if (converted == null) {
                converted = convertCompatible(rawString, requestedClazz);
//                }

                //TODO string constructors (and valueOf methods) work just fine but isn't in this version of the spec
//                if (converted == null) {
//                    converted = standardStringConstructors(rawString, requestedClazz);
//                }
            }
        }

        if (converted == null) {
            throw new ConverterNotFoundException(Tr.formatMessage(tc, "could.not.find.converter.CWMCG0014E", type.getTypeName()));
        }

        return converted;

    }

    /**
     * Converts from String based on isAssignableFrom or instanceof
     *
     * @param rawString
     * @param type
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T> T convertCompatible(String rawString, Class<T> type) {
        T converted = null;
        for (Map.Entry<Type, Converter<?>> con : converters.entrySet()) {
            Type key = con.getKey();
            if (key instanceof Class) {
                Class<?> clazz = (Class<?>) key;
                if (type.isAssignableFrom(clazz)) {
                    converted = (T) convert(rawString, key);
                    break;
                }
            } else if (key instanceof TypeVariable) {
                TypeVariable<?> typeVariable = (TypeVariable<?>) key;
                converted = convertGenericClazz(rawString, type, typeVariable);
                if (converted != null) {
                    break;
                }
            }
        }
        return converted;
    }

    /**
     * Front end to (T)convert(rawString, typeVariable) if isAssignableFrom compatible Converter present
     *
     * @param rawString
     * @param type
     * @param typeVariable
     * @return T converted
     */
    @SuppressWarnings("unchecked")
    private <T> T convertGenericClazz(String rawString, Class<T> type, TypeVariable<?> typeVariable) {
        T converted = null;
        AnnotatedType[] bounds = typeVariable.getAnnotatedBounds();
        for (AnnotatedType bound : bounds) {
            Type bType = bound.getType();
            if (bType instanceof Class) {
                Class<?> bClazz = (Class<?>) bType;
                if (bClazz.isAssignableFrom(type)) {
                    converted = (T) convert(rawString, typeVariable);
                    break;
                }
            }
        }
        return converted;
    }

//    /**
//     * Attempt to apply a valueOf or T(String s) constructor
//     *
//     * @param rawString
//     * @param type
//     * @return a converted T object
//     */
//    @FFDCIgnore(NoSuchMethodException.class)
//    private <T> T standardStringConstructors(String rawString, Class<T> type) {
//        T converted = null;
//        try {
//            // First try valueOf(String) static method
//            converted = invokeValueOf(rawString, type);
//        } catch (NoSuchMethodException e) {
//            // No FFDC
//            // If that fails, try a T(String) constructor
//            converted = invokeStringConstructor(rawString, type);
//        }
//        return converted;
//    }
//
//    /**
//     * Apply convert across an array
//     *
//     * @param rawString
//     * @param arrayType
//     * @return an array of converted T objects.
//     */
//    @SuppressWarnings("unchecked")
//    private <T> T[] convertArray(String rawString, Class<T> arrayType) {
//        String[] elements = rawString.split(",");
//        Object rawArray = Array.newInstance(arrayType, elements.length);
//        T[] array = (T[]) rawArray;
//        for (int i = 0; i < elements.length; i++) {
//            array[i] = (T) convert(elements[i], arrayType);
//        }
//        return array;
//    }
//
//    /**
//     * Wrapper over a reflection located 'valueOf( String s)' method.
//     *
//     * @param rawString
//     * @param type
//     * @return result of valueOf
//     * @throws NoSuchMethodException
//     */
//    @SuppressWarnings("unchecked")
//    private <T> T invokeValueOf(String rawString, Class<T> type) throws NoSuchMethodException {
//        T converted = null;
//        try {
//            Method method = type.getMethod("valueOf", String.class);
//            converted = (T) method.invoke(null, rawString);
//        } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//            throw new ConversionException(e);
//        }
//        return converted;
//    }
//
//    /**
//     * Wrapper over a reflection located ( String s) constructor method.
//     *
//     * @param rawString
//     * @param type
//     * @return
//     */
//    @FFDCIgnore(NoSuchMethodException.class)
//    private <T> T invokeStringConstructor(String rawString, Class<T> type) {
//        T converted = null;
//        try {
//            Constructor<T> c = type.getConstructor(String.class);
//            converted = c.newInstance(rawString);
//        } catch (NoSuchMethodException e) {
//            //No FFDC, just return null
//        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//            throw new ConversionException(e);
//        }
//        return converted;
//    }
}
