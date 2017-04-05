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
 * Specifies the statistics provided by a stateful session bean.
 */
public interface StatefulSessionBeanStats extends SessionBeanStats {

    /*
     * Returns the number of beans that are in the passivated state.
     */
    public RangeStatistic getPassiveCount();

}
