/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

/**
 * The marker interface for wrapper proxies of no-interface views. The actual
 * implementation will be a subclass of the bean class and will contain an
 * instance of {@link BusinessLocalWrapperProxy}.
 * 
 * @see WrapperProxy
 */
public interface LocalBeanWrapperProxy
                extends WrapperProxy
{
    // --------------------------------------------------------------------------
    // Intentionally contains no additional state or methods.
    // Used as a marker interface to distinguish local bean wrapper proxies.
    // --------------------------------------------------------------------------
}
