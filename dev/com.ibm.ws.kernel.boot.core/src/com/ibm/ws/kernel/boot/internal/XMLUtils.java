/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.boot.internal;

import java.lang.reflect.Field;
import java.util.Set;

import javax.xml.stream.XMLStreamReader;

/**
 * Using the class to process XML files
 */
public class XMLUtils {
    /**
     * Get the element attribute value
     * 
     * @param reader
     * @param localName
     * @return
     */
    public static String getAttribute(XMLStreamReader reader, String localName) {
        int count = reader.getAttributeCount();
        for (int i = 0; i < count; i++) {
            String name = reader.getAttributeLocalName(i);
            if (localName.equals(name)) {
                return reader.getAttributeValue(i);
            }
        }
        return null;
    }

    /**
     * Create the instance by parse the element, the instance's class must have the empty construct.
     * The clazz must have the fields in attrNames and all the fields type must be String
     * 
     * @param reader
     * @param clazz
     * @param attrNames
     * @return
     */
    public static <T> T createInstanceByElement(XMLStreamReader reader, Class<T> clazz, Set<String> attrNames) {
        if (reader == null || clazz == null || attrNames == null)
            return null;

        try {
            T instance = clazz.newInstance();

            int count = reader.getAttributeCount();
            int matchCount = attrNames.size();

            for (int i = 0; i < count && matchCount > 0; ++i) {
                String name = reader.getAttributeLocalName(i);
                String value = reader.getAttributeValue(i);

                if (attrNames.contains(name)) {
                    Field field = clazz.getDeclaredField(name);
                    field.setAccessible(true);
                    field.set(instance, value);
                    matchCount--;
                }
            }
            return instance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
