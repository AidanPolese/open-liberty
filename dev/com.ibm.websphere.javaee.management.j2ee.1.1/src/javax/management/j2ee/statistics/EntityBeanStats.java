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
package javax.management.j2ee.statistics;

/**
 * Specifies statistics provided by entity beans.
 */
public interface EntityBeanStats extends EJBStats {

    /*
     * Returns the number of bean instances in the ready state.
     */
    public RangeStatistic getReadyCount();

    /*
     * Returns the number of bean instances in the pooled state.
     */
    public RangeStatistic getPooledCount();

}
