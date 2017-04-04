/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.injectionengine.factory;

import javax.naming.Reference;

import com.ibm.wsspi.injectionengine.InjectionException;

/**
 * Instances of this interface are used to create Reference objects with
 * JNDI lookup information which the caller then binds to a JNDI name space.
 * When the object is looked up, the associated factory uses the JNDI
 * information to obtain an initial JNDI context and perform a lookup on
 * the initial context. <p>
 *
 * This factory is very similar to a com.ibm.ws.naming.util.IndirectJndiLookupFactory
 * (and an implementation may just wrapper one), but provides a layer of
 * abstraction for the injection service, and also returns Reference objects
 * rather than Referenceable objects. <p>
 **/
public interface IndirectJndiLookupReferenceFactory
{
    /**
     * This method creates an Indirect JNDI Lookup Reference based on a
     * JNDI lookup name and target class type. <p>
     *
     * @param refName The reference name.
     * @param boundToJndiName The JNDI lookup name.
     * @param type The class name of the object returned from the Indirect JNDI
     *            lookup. Setting this value causes JNDI list operations to return the
     *            refClassName instead of the Reference object itself.
     *
     * @return The Indirect JNDI Lookup Reference that was created.
     */
    public Reference createIndirectJndiLookup(String refName,
                                              String boundToJndiName,
                                              String type)
                    throws InjectionException;

    /**
     * This method creates an Indirect JNDI Lookup Reference by mapping
     * the reference JNDI name to a reference by the specified JNDI name
     * in java:comp/env JNDI context of the consumer. This is useful for
     * managed objects that do not define a java:comp/env JNDI context
     * of their own, but have visibility to the contexts of a consuming
     * component, such as when a ManagedBean is injected into an EJB. <p>
     *
     * @param refName The reference name.
     * @param boundToJndiName The JNDI lookup name.
     * @param type The class name of the object returned from the Indirect JNDI
     *            lookup. Setting this value causes JNDI list operations to return the
     *            refClassName instead of the Reference object itself.
     *
     * @return The Indirect JNDI Lookup Reference that was created.
     */
    public Reference createIndirectJndiLookupInConsumerContext(String refName,
                                                               String boundToJndiName,
                                                               String type)
                    throws InjectionException;
}
