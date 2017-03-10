/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.kernel.service.utils;

/**
 * Utilities for working with OSGi filters.
 */
public class FilterUtils {

    /**
     * Creates a filter string that matches an attribute value exactly.
     * Characters in the value with special meaning will be escaped.
     * 
     * @param name a valid attribute name
     * @param value the exact attribute value
     */
    public static String createPropertyFilter(String name, String value) {
        assert name.matches("[^=><~()]+");

        StringBuilder builder = new StringBuilder(name.length() + 3 + (value == null ? 0 : value.length() * 2));
        builder.append('(').append(name).append('=');

        int begin = 0;
        if (value != null) {
            for (int i = 0; i < value.length(); i++) {
                if ("\\*()".indexOf(value.charAt(i)) != -1) {
                    builder.append(value, begin, i).append('\\');
                    begin = i;
                }
            }

            return builder.append(value, begin, value.length()).append(')').toString();
        } else {
            return builder.append(')').toString();
        }
    }

}
