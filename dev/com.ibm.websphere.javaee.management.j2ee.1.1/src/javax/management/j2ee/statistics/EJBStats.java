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
 * The EJBStats interface specifies statistics provided by all EJB component types.
 */
public interface EJBStats extends Stats {

    /*
     * Returns the number of times the beans create method was called.
     */
    public CountStatistic getCreateCount();

    /*
     * Returns the number of times the beans remove method was called.
     */
    public CountStatistic getRemoveCount();

}
