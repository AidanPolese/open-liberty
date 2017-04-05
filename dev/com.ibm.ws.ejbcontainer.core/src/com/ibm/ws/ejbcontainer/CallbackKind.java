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
package com.ibm.ws.ejbcontainer;

/**
 * This enum is intented to be used to set a field in the BeanMetaData
 * so that the runtime can efficiently determine which of the possible
 * cases the runtime code must handle during processing of a business method
 * invocation and/or bean lifecycle events. The possibilities are:
 * <ul>
 * <li>
 * The bean does not implement any javax.ejb interface such as SessionBean
 * or MessageDrivenBean and there are no interceptor methods (either around invoke
 * or lifecycle callback events) to be invoked for this bean.
 * <li>
 * The bean implements javax.ejb.SessionBean and there are no interceptor
 * methods (either around invoke or lifecycle callback events) to invoke for this bean.
 * <li>
 * The bean implements javax.ejb.MessageDrivenBean and there are no interceptor
 * methods (either around invoke or lifecycle callback events) to invoke for this bean.
 * <li>
 * A javax.interceptor.InvocationContext must be created and passed to the
 * interceptor method, which may be either an around invoke or lifecycle callback
 * interceptor method.
 * </ul>
 */
public enum CallbackKind
{
    None, // No interceptor or javax.ejb callback methods to invoke.
    SessionBean, // Just callback methods in javax.ejb.SessionBean.
    MessageDrivenBean, // Just callback methods in javax.ejb.MessageDrivenBean.
    InvocationContext, // Use InvocationContext to call interceptor method (either
                       // around invoke or lifecycle callback).
}
