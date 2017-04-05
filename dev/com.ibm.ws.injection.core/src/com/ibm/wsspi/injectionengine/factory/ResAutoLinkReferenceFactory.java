/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2007, 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.injectionengine.factory;

import javax.naming.Reference;

/**
 * Instances of this interface are used to create Reference objects with
 * JNDI lookup information for Resource References, which the caller then binds
 * to a JNDI name space. When the object is looked up, the associated factory
 * uses the JNDI information and component or environment specific information
 * to obtain an initial JNDI context and perform a lookup on the initial context.
 * <p>
 * This factory is very similar to a javax.naming.IndirectJndiLookupFactory,
 * but is intended specifically for Resource References, and allows the lookup
 * to take into account the environment of the caller, such as the current
 * ComponentMetaData (server) or local naming context (client). <p>
 **/
public interface ResAutoLinkReferenceFactory
{
    /**
     * This method creates a Resource JNDI Lookup Reference based on a
     * JNDI lookup name, for the specified resource.
     */
    public Reference createResAutoLinkReference(ResourceInfo resourceInfo);

}
