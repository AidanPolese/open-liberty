/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1998, 2001
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.cpi;

/**
 * Contains basic persister configuration data which is expected to
 * be useful for all types of persisters. Data specific to particular
 * backends must be provided via extensions. eg. JDBCPersisterMetaData
 * 
 * @see com.ibm.websphere.cpi.JDBCPersisterMetaData
 * 
 */
public interface PersisterMetaData {

    /**
     * @return (EnterpriseBean) MOF object of the associated bean
     */
    public Object getEnterpriseBean();

    /**
     * @return the ClassLoader that can load persister classes.
     */
    public ClassLoader getClassLoader();

    /**
     * @returns a PersisterConfigData that contains
     *          properties of the Perister
     */
    public PersisterConfigData getPersisterConfigData();
}
