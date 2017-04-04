/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package com.ibm.websphere.monitor.annotation;

public @interface Elapsed {

    public enum Time {
        CLOCK,
        CPU,
        USER
    };

    public Time value() default Time.CLOCK;

}
