// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// %I% %W% %G% %U%
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2010
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  JPALookupDelegate.java
//
// Source File Description:
//
//     WAS JPA Service interface. Used to delegate the search for JPA resources
//     if they cannot be found in the Java EE container.
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// d673962   WAS80     20101018 timoward : Initial Release
// --------- --------- -------- --------- -----------------------------------------
package com.ibm.ws.jpa;

import java.util.Map;

import com.ibm.websphere.csi.J2EEName;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * WAS JPA lookup delegation interface.
 */
public interface JPALookupDelegate
{

    /**
     * Returns the EntityManagerFactory defines by the application/module/persistence unit spcified.
     * This is used by the resolver and naming object factory to retrieve the factory for
     * 
     * @PersistenceUnit.
     * 
     * @param unitName
     *            The name of the persistence unit to locate
     * @param j2eeName
     *            JavaEE unique identifier for the component, identifying the
     *            java:comp/env context used.
     * @return The EntityManagerFactory for the specified unit, or null if none is found.
     */
    public EntityManagerFactory getEntityManagerFactory(String unitName,
                                                        J2EEName j2eeName);

    /**
     * Returns the EntityManager defines by the application/module/persistence unit specified. This
     * is used by the naming object factory to retrieve the entity manager for
     * 
     * @PersistenceContext.
     * 
     * @param unitName
     *            The name of the persistence unit to locate
     * @param j2eeName
     *            JavaEE unique identifier for the component, identifying the
     *            java:comp/env context used.
     * @param isExtendedContextType
     *            {@code true} if the EntityManager is extended scope.
     * @param properties
     *            additional properties to create the EntityManager with
     * 
     * @return A managed EntityManager for the specified unit or null if none is found.
     */
    public EntityManager getEntityManager(String unitName,
                                          J2EEName j2eeName,
                                          boolean isExtendedContextType,
                                          Map<?, ?> properties);
}
