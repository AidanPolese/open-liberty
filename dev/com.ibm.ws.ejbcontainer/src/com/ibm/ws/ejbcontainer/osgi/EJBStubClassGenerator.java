/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.osgi;

import java.util.Set;

public interface EJBStubClassGenerator {

    /**
     * Returns a set of Remote classes for which the dynamically generated
     * stub classes need to be compatible with RMIC generated stubs and ties
     * for the specified application. <p>
     *
     * @param appName application name
     * @return the RMIC compatible class names; or an empty set if none exist.
     */
    Set<String> getRMICCompatibleClasses(String appName);

    /**
     * Add a set of Remote classes for which the dynamically generated stub
     * classes need to be compatible with RMIC generated stubs and ties
     * when accessed by the specified classloader.
     *
     * In tWAS, pre-EJB 3 modules are processed by ejbdeploy, and rmic is
     * used to generate stubs for remote home and interface classes. These
     * stubs need to exist so that we do not dynamically generate stubs that
     * use the "WAS EJB 3" marshalling rules.
     *
     * In Liberty, there is no separate deploy step, so we need to ensure
     * that stubs for pre-EJB 3 modules are generated with as much
     * compatibility with RMIC as we can.
     *
     * @param loader application classloader for which the RMIC compatible stubs are required
     * @param rmicCompatibleClasses the RMIC compatible class names
     */
    void addRMICCompatibleClasses(ClassLoader loader, Set<String> rmicCompatibleClasses);
}
