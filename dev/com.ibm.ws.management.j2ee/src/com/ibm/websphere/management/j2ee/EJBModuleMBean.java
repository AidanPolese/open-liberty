/**
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
package com.ibm.websphere.management.j2ee;


/**
 * Identifies a deployed EJB JAR module.
 */
public interface EJBModuleMBean extends J2EEModuleMBean {

    /**
     * A list of EJB components contained in the deployed EJB JAR module. For
     * each EJB component contained in the deployed EJB JAR there must be one EJB
     * OBJECT_NAME in the ejbs list that identifies it.
     */
    String[] getejbs();

}
