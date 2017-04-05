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
 * A marker interface for all wrapper proxies. A wrapper proxy is an indirect
 * local proxy. All wrapper proxy objects must contain a {@link WrapperProxyState}, which can be obtained using {@link WrapperProxyState#getWrapperProxyState}. Wrapper proxies must
 * implement all the same methods as the corresponding wrapper, including {@link Object#equals} and {@link Object#hashCode}, such that a conforming
 * client cannot distinguish a wrapper proxy from a wrapper.
 */
public interface WrapperProxy
{
    // --------------------------------------------------------------------------
    // Intentionally contains no additional state or methods.
    // Used as a marker interface to distinguish wrapper proxies.
    // --------------------------------------------------------------------------
}
