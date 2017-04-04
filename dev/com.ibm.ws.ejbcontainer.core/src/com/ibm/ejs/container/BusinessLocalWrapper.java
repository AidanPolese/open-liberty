/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2006
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

/**
 * A <code>BusinessLocalWrapper</code> wraps an EJB and interposes
 * calls to the container before and after every method call on the
 * EJB. <p>
 * 
 * The <code>BusinessLocalWrapper</code> is designed to contain a minimum
 * amount of state. Its primary state, inherited from {@link EJSWrapperBase},
 * consists of a reference to the container it is in and a <code>BeanId</code>
 * instance that defines the identity of its associated EJB. Note, it does not
 * directly maintain a reference to the EJB. It relies on the container to
 * supply it with the appropriate EJB. <p>
 * 
 * Since the EJB 3.0 business interfaces do no implement either the EJBObject
 * or EJBLocalObject interfaces, the BusinessLocalWrapper has no methods
 * to implement, unlike the wrapper classes for the EJB 2.1 component
 * interface wrappers. The generated business interface implementations
 * could subclass EJSWrapperBase directly, but this class serves as a
 * 'marker' interface, so that internal EJBContainer code can easily
 * distiguish wrapper types. <p>
 * 
 * NOTE: The generated local business interface implementation to a bean
 * extends a BusinessLocaWrapper instance. For that reason,
 * BusinessLocalWrapper MUST NOT implement any methods. Otherwise,
 * there would be a potential conflict between the methods on
 * the bean's local business interface and those on
 * BusinessLocalWrapper. <p>
 **/
public class BusinessLocalWrapper extends EJSWrapperBase
{
    // --------------------------------------------------------------------------
    // Intentionally contains no additonal state or methods.
    // Used as a marker interface to distinguish between wrapper types.
    // --------------------------------------------------------------------------
}
