/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2007
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.injectionengine;

/**
 * This exception is thrown when the injection engine detects that the user
 * configuration of an injection is incorrect or invalid.
 */
public class InjectionConfigurationException extends InjectionException {

    private static final long serialVersionUID = -6795675132453992281L;

    public InjectionConfigurationException()
    {
        // intentionally left blank.
    }

    public InjectionConfigurationException(String message)
    {
        super(message);
    }

    public InjectionConfigurationException(Throwable throwable)
    {
        super(throwable);
    }

    public InjectionConfigurationException(String detailMessage, Throwable throwable)
    {
        super(detailMessage, throwable);
    }
}
