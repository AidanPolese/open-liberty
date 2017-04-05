/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1998, 2005
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

/**
 * This exception is thrown to indicate an attempt has been made to use
 * a disabled home.
 */

public class HomeDisabledException
                extends RuntimeException
{
    private static final long serialVersionUID = 8801205964764897441L;

    /**
     * Create a new <code>HomeDisabledException</code> instance. <p>
     */

    public HomeDisabledException(String s) {
        super(s);
    } // HomeDisabledException

} // HomeDisabledException
