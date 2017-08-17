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
//This class was created from com.ibm.ws.microprofile.config.interfaces.DefaultConverters and com.ibm.ws.microprofile.config.impl.ConversionManager
//Those classes were inspired by com.netflix.archaius.DefaultDecoder

package com.ibm.ws.microprofile.faulttolerance.cdi.config;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.BitSet;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.bind.DatatypeConverter;

import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;

/**
 * The helper class returns all the built-in converters.
 *
 */
public class DefaultConverters {

    private static final TraceComponent tc = Tr.register(DefaultConverters.class);

    public static interface Converter<T> {
        T convert(String value);
    }

    public final static Converter<String> STRING_CONVERTER = v -> v;

    public final static Converter<Boolean> BOOLEAN_CONVERTER = v -> {
        if (v.equalsIgnoreCase("true") || v.equalsIgnoreCase("yes") || v.equalsIgnoreCase("y") || v.equalsIgnoreCase("on") || v.equalsIgnoreCase("1")) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    };

    public final static Converter<Integer> INTEGER_CONVERTER = Integer::valueOf;
    public final static Converter<Long> LONG_CONVERTER = Long::valueOf;
    public final static Converter<Short> SHORT_CONVERTER = Short::valueOf;
    public final static Converter<Byte> BYTE_CONVERTER = Byte::valueOf;
    public final static Converter<Double> DOUBLE_CONVERTER = Double::valueOf;
    public final static Converter<Float> FLOAT_CONVERTER = Float::valueOf;

    public final static Converter<BigInteger> BIG_INTEGER_CONVERTER = BigInteger::new;
    public final static Converter<BigDecimal> BIG_DECIMAL_CONVERTER = BigDecimal::new;

    public final static Converter<AtomicInteger> ATOMIC_INTEGER_CONVERTER = s -> new AtomicInteger(Integer.parseInt(s));
    public final static Converter<AtomicLong> ATOMIC_LONG_CONVERTER = s -> new AtomicLong(Long.parseLong(s));

    public final static Converter<Duration> DURATION_CONVERTER = (String value) -> {
        try {
            return Duration.parse(value);
        } catch (DateTimeException dte) {
            throw new IllegalArgumentException(dte);
        }
    };

    public final static Converter<Period> PERIOD_CONVERTER = (String value) -> {
        try {
            return Period.parse(value);
        } catch (DateTimeException dte) {
            throw new IllegalArgumentException(dte);
        }
    };

    public final static Converter<LocalDateTime> LOCAL_DATE_TIME_CONVERTER = (String value) -> {
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeException dte) {
            throw new IllegalArgumentException(dte);
        }
    };

    public final static Converter<LocalDate> LOCAL_DATE_CONVERTER = (String value) -> {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeException dte) {
            throw new IllegalArgumentException(dte);
        }
    };

    public final static Converter<LocalTime> LOCAL_TIME_CONVERTER = (String value) -> {
        try {
            return LocalTime.parse(value);
        } catch (DateTimeException dte) {
            throw new IllegalArgumentException(dte);
        }
    };

    public final static Converter<OffsetDateTime> OFFSET_DATE_TIME_CONVERTER = (String value) -> {
        try {
            return OffsetDateTime.parse(value);
        } catch (DateTimeException dte) {
            throw new IllegalArgumentException(dte);
        }
    };

    public final static Converter<OffsetTime> OFFSET_TIME_CONVERTER = (String value) -> {
        try {
            return OffsetTime.parse(value);
        } catch (DateTimeException dte) {
            throw new IllegalArgumentException(dte);
        }
    };

    public final static Converter<ZonedDateTime> ZONED_DATE_TIME_CONVERTER = (String value) -> {
        try {
            return ZonedDateTime.parse(value);
        } catch (DateTimeException dte) {
            throw new IllegalArgumentException(dte);
        }
    };

    public final static Converter<Instant> INSTANT_CONVERTER = (String value) -> {
        try {
            return Instant.from(OffsetDateTime.parse(value));
        } catch (DateTimeException dte) {
            throw new IllegalArgumentException(dte);
        }
    };

    @FFDCIgnore(DateTimeParseException.class)
    private final static Instant parseZonedDateTime(String value) {
        Instant instant = null;
        try {
            instant = ZonedDateTime.parse(value, DateTimeFormatter.ISO_ZONED_DATE_TIME).toInstant();
        } catch (DateTimeParseException e) {
            //ignore
        } catch (DateTimeException e) {
            throw new IllegalArgumentException(e);
        }
        return instant;
    }

    @FFDCIgnore(DateTimeParseException.class)
    private final static Instant parseLocalDateTime(String value) {
        Instant instant = null;
        try {
            instant = LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME).atZone(ZoneOffset.UTC).toInstant();
        } catch (DateTimeParseException e) {
            //ignore
        } catch (DateTimeException e) {
            throw new IllegalArgumentException(e);
        }
        return instant;
    }

    private final static Instant parseLocalDate(String value) {
        Instant instant = null;
        try {
            instant = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(ZoneOffset.UTC).toInstant();
        } catch (DateTimeException e) {
            throw new IllegalArgumentException(e);
        }
        return instant;
    }

    public final static Converter<Currency> CURRENCY_CONVERTER = Currency::getInstance;

    public final static Converter<BitSet> BIT_SET_CONVERTER = v -> BitSet.valueOf(DatatypeConverter.parseHexBinary(v));

    /**
     * Convert the string to a URI or throw ConvertException if unable to convert
     *
     */
    public final static Converter<URI> URI_CONVERTER = (String value) -> {
        URI uri = null;
        try {
            uri = new URI(value);
        } catch (URISyntaxException use) {
            throw new IllegalArgumentException(use);
        }
        return uri;
    };

    /**
     * Convert the string to a URL or throw ConvertException if unable to convert
     */
    public final static Converter<URL> URL_CONVERTER = (String value) -> {
        URL url = null;
        try {
            url = new URL(value);
        } catch (MalformedURLException mfue) {
            throw new IllegalArgumentException(mfue);
        }
        return url;
    };

    //============== Extra converters added for FT =======================

    public final static Converter<ChronoUnit> CHRONO_UNIT_CONVERTER = ChronoUnit::valueOf;

    public final static Converter<Class<?>> CLASS_CONVERTER = (String className) -> {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
        return clazz;
    };

    //====================================================================

    private static Map<Class<?>, Converter<?>> defaultConverters = new HashMap<Class<?>, Converter<?>>();

    static {
        defaultConverters.put(String.class, STRING_CONVERTER);

        defaultConverters.put(Boolean.class, BOOLEAN_CONVERTER);
        defaultConverters.put(boolean.class, BOOLEAN_CONVERTER);

        defaultConverters.put(Integer.class, INTEGER_CONVERTER);
        defaultConverters.put(int.class, INTEGER_CONVERTER);

        defaultConverters.put(Long.class, LONG_CONVERTER);
        defaultConverters.put(long.class, LONG_CONVERTER);

        defaultConverters.put(Short.class, SHORT_CONVERTER);
        defaultConverters.put(short.class, SHORT_CONVERTER);

        defaultConverters.put(Byte.class, BYTE_CONVERTER);
        defaultConverters.put(byte.class, BYTE_CONVERTER);

        defaultConverters.put(Double.class, DOUBLE_CONVERTER);
        defaultConverters.put(double.class, DOUBLE_CONVERTER);

        defaultConverters.put(Float.class, FLOAT_CONVERTER);
        defaultConverters.put(float.class, FLOAT_CONVERTER);

        defaultConverters.put(BigInteger.class, BIG_INTEGER_CONVERTER);
        defaultConverters.put(BigDecimal.class, BIG_DECIMAL_CONVERTER);

        defaultConverters.put(AtomicInteger.class, ATOMIC_INTEGER_CONVERTER);
        defaultConverters.put(AtomicLong.class, ATOMIC_LONG_CONVERTER);

        defaultConverters.put(Duration.class, DURATION_CONVERTER);
        defaultConverters.put(Period.class, PERIOD_CONVERTER);

        defaultConverters.put(LocalDateTime.class, LOCAL_DATE_TIME_CONVERTER);
        defaultConverters.put(LocalDate.class, LOCAL_DATE_CONVERTER);
        defaultConverters.put(LocalTime.class, LOCAL_TIME_CONVERTER);

        defaultConverters.put(OffsetDateTime.class, OFFSET_DATE_TIME_CONVERTER);
        defaultConverters.put(OffsetTime.class, OFFSET_TIME_CONVERTER);
        defaultConverters.put(ZonedDateTime.class, ZONED_DATE_TIME_CONVERTER);

        defaultConverters.put(Instant.class, INSTANT_CONVERTER);

        defaultConverters.put(Currency.class, CURRENCY_CONVERTER);
        defaultConverters.put(BitSet.class, BIT_SET_CONVERTER);

        defaultConverters.put(URL.class, URL_CONVERTER);
        defaultConverters.put(URI.class, URI_CONVERTER);

        defaultConverters.put(ChronoUnit.class, CHRONO_UNIT_CONVERTER);
        defaultConverters.put(Class.class, CLASS_CONVERTER);

        defaultConverters = Collections.unmodifiableMap(defaultConverters);

    }

    /**
     * Convert a String to a Type using registered converters for the Type
     *
     * @param <S>
     *
     * @param rawString
     * @param type
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(String rawString, Class<T> type) {
        T converted = null;

        if (rawString != null) {
            if (defaultConverters.containsKey(type)) {
                Converter<?> converter = defaultConverters.get(type);
                if (converter != null) {
                    try {
                        converted = (T) converter.convert(rawString);
                    } catch (Throwable e) {
                        //TODO NLS - see conversion.exception.CWMCG0007E
                        throw new FaultToleranceException(e);
                    }

                    if (converted == null) {
                        //TODO NLS - see converter.returned.null.CWMCG0005E
                        throw new FaultToleranceException();
                    }
                }
            }

            if (converted == null && type instanceof Class) {
                Class<?> requestedClazz = type;

                if (requestedClazz.isArray()) {
                    Class<?> arrayType = requestedClazz.getComponentType();
                    converted = (T) convertArray(rawString, arrayType);
                }

                if (converted == null) {
                    converted = (T) convertCompatible(rawString, requestedClazz);
                }

                if (converted == null) {
                    converted = (T) standardStringConstructors(rawString, requestedClazz);
                }
            }
        }

        if (converted == null) {
            //TODO NLS - see could.not.find.converter.CWMCG0014E
            throw new FaultToleranceException();
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
    private static <T> T convertCompatible(String rawString, Class<T> type) {
        T converted = null;
        for (Map.Entry<Class<?>, Converter<?>> con : defaultConverters.entrySet()) {
            Class<?> key = con.getKey();
            Class<?> clazz = key;
            if (type.isAssignableFrom(clazz)) {
                converted = (T) convert(rawString, key);
                break;
            }
        }
        return converted;
    }

    /**
     * Attempt to apply a valueOf or T(String s) constructor
     *
     * @param rawString
     * @param type
     * @return a converted T object
     */
    @FFDCIgnore(NoSuchMethodException.class)
    private static <T> T standardStringConstructors(String rawString, Class<T> type) {
        T converted = null;
        try {
            // First try valueOf(String) static method
            converted = invokeValueOf(rawString, type);
        } catch (NoSuchMethodException e) {
            // No FFDC
            // If that fails, try a T(String) constructor
            converted = invokeStringConstructor(rawString, type);
        }
        return converted;
    }

    /**
     * Apply convert across an array
     *
     * @param rawString
     * @param arrayType
     * @return an array of converted T objects.
     */
    @SuppressWarnings("unchecked")
    private static <T> T[] convertArray(String rawString, Class<T> arrayType) {
        String[] elements = rawString.split(",");
        Object rawArray = Array.newInstance(arrayType, elements.length);
        T[] array = (T[]) rawArray;
        for (int i = 0; i < elements.length; i++) {
            array[i] = convert(elements[i], arrayType);
        }
        return array;
    }

    /**
     * Wrapper over a reflection located 'valueOf( String s)' method.
     *
     * @param rawString
     * @param type
     * @return result of valueOf
     * @throws NoSuchMethodException
     */
    @SuppressWarnings("unchecked")
    private static <T> T invokeValueOf(String rawString, Class<T> type) throws NoSuchMethodException {
        T converted = null;
        try {
            Method method = type.getMethod("valueOf", String.class);
            converted = (T) method.invoke(null, rawString);
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new FaultToleranceException(e);
        }
        return converted;
    }

    /**
     * Wrapper over a reflection located ( String s) constructor method.
     *
     * @param rawString
     * @param type
     * @return
     */
    @FFDCIgnore(NoSuchMethodException.class)
    private static <T> T invokeStringConstructor(String rawString, Class<T> type) {
        T converted = null;
        try {
            Constructor<T> c = type.getConstructor(String.class);
            converted = c.newInstance(rawString);
        } catch (NoSuchMethodException e) {
            //No FFDC, just return null
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new FaultToleranceException(e);
        }
        return converted;
    }

}
