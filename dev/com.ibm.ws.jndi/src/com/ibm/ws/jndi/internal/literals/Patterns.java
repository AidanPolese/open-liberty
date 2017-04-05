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
package com.ibm.ws.jndi.internal.literals;

final class Patterns {
    private static final String DIGIT_TAIL = "(?:_*\\d)*";
    private static final String DIGITS = "\\d" + DIGIT_TAIL;
    private static final String DOT = "\\.";
    private static final String DECIMAL = "(" + DOT + DIGITS + "|" + DIGITS + DOT + "|" + DIGITS + DOT + DIGITS + ")";
    private static final String SIGN = "[+-]";
    private static final String EXPONENT = "[Ee]" + optional(SIGN) + DIGITS;
    private static final String DOUBLE_QUALIFIER = "[Dd]";
    private static final String FLOAT_QUALIFIER = "[Ff]";

    /** A regular expression to match a Java 7 double literal */
    static final String DOUBLE = optional(SIGN)
                                 + "(?:"
                                 + DECIMAL + optional(EXPONENT) + optional(DOUBLE_QUALIFIER)
                                 + "|"
                                 + DIGITS + optional(EXPONENT) + DOUBLE_QUALIFIER
                                 + "|"
                                 + DIGITS + EXPONENT
                                 + ")";

    /** A regular expression to match a Java 7 float literal */
    static final String FLOAT = optional(SIGN) + "(?:" + DECIMAL + "|" + DIGITS + ")" + optional(EXPONENT) + FLOAT_QUALIFIER;

    private Patterns() {} // no instances, please!

    private static String optional(String expr) {
        return "(?:" + expr + ")?";
    }
}
