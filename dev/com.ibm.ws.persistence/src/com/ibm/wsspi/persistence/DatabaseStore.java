/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.persistence;

/**
 * Enables configuration of a database as a persistent store that can be shared by multiple components.
 */
public interface DatabaseStore {
    /**
     * Create a persistence service unit for the specified entity classes.
     * The invoker of this method is responsible for closing the persistence service unit
     * and for participating in DDL generation.
     * 
     * The persistence service feature is currently not supported for informix.
     * 
     * @param loader class loader for the entity classes.
     * @param entityClasses list of entity classes.
     * @return the persistence service unit.
     * @throws Exception if a failure occurs.
     */
    PersistenceServiceUnit createPersistenceServiceUnit(ClassLoader loader, String... entityClassNames) throws Exception;

    /**
     * Get the configured schema name.
     */
    public String getSchema();

    /**
     * Get the configured table prefix.
     */
    public String getTablePrefix();
}
