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
package com.ibm.ws.kernel.feature;

import java.util.Locale;

import com.ibm.ws.ffdc.annotation.FFDCIgnore;

/**
 * An enum indicating visibility information
 */
public enum Visibility {
    /** Visible to all */
    PUBLIC,
    /** Visible to other features, but not visible externally */
    PROTECTED,
    /** Visible only to the product that contributes it */
    PRIVATE,
    /** Visible to installers but not the runtime */
    INSTALL;

    @FFDCIgnore(IllegalArgumentException.class)
    public static Visibility fromString(String s) {
        if (s == null)
            return PRIVATE;

        Visibility result;
        try {
            result = Visibility.valueOf(s.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException iae) {
            result = PRIVATE;
        }
        return result;
    }
}
