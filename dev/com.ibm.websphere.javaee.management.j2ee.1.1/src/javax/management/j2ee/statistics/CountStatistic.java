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
 * The CountStatistic interface specifies standard count measurements.
 */
public interface CountStatistic extends Statistic {

    /*
     * Returns the count since the measurement started.
     */
    public long getCount();

}
