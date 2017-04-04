/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2005
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.ejbcontainer;

import javax.ejb.EntityContext;

/**
 * The <code>EntityContextExtension</code> interface may be used by an Entity EJB
 * to invoke WebSphere-specific EJB Container services. <p>
 * 
 * An Entity EJB may invoke the EntityContextExtension methods by casting
 * the context object passed into the EJB's setEntityContext() method, to
 * com.ibm.websphere.ejbcontainer.EntityContextExtension. Typically the
 * code in setEntityContext() assigns the context object to a bean
 * instance variable for later use by other bean methods. <p>
 * 
 * In WebSphere, all javax.ejb.EntityContext objects also implement this
 * interface. This allows the bean to use a single 'context' instance
 * variable (of type EntityContextExtension) and be able to invoke EJB
 * specification-defined methods as well as WebSphere-defined methods on
 * the same context object. It is also possible, of course, to assign
 * the context object to two instance variables, one of type
 * javax.ejb.EntityContext and another of type
 * com.ibm.websphere.ejbcontainer.EntityContextExtension. <p>
 * 
 * <b>Note: Some of the methods on this interface may result in behavior not
 * compliant with the official EJB specification.</b> If this is the case, the
 * documentation for that method will indicate so. <p>
 * 
 * @since WAS 6.0.2
 * @see EJBContextExtension
 * @ibm-api
 */

public interface EntityContextExtension
                extends EntityContext,
                EJBContextExtension
{
    // Currently no Entity specific extensions.
} // EntityContextExtension
