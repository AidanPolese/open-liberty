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
 * Specifies the statistics provided by session beans of both stateful and stateless types.
 */
public interface SessionBeanStats extends EJBStats {

    /*
     * Returns the number of beans in the method-ready state.
     */
    public RangeStatistic getMethodReadyCount();

}
