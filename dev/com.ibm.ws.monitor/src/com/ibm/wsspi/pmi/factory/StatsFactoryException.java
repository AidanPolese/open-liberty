// IBM Confidential OCO Source Material
// 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/*
 * @(#)author    Srini Rangaswamy
 * @(#)version   1.1
 * @(#)date      03/15/03
 */

package com.ibm.wsspi.pmi.factory;

/**
 * StatsFactoryException is thrown from StatsFactory to indicate an error while creating a StatsInstance or a StatsGroup.
 * 
 * @ibm-spi
 */

public class StatsFactoryException extends java.lang.Exception {
    private static final long serialVersionUID = 3038181289491772545L;

    /**
     * Default Constructor
     */
    public StatsFactoryException() {
        super();
    }

    /**
     * Constructor taking a String.
     * 
     * @param s message
     */
    public StatsFactoryException(String s) {
        super(s);
    }
}
