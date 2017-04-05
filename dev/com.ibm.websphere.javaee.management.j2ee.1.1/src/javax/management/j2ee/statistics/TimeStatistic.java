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
 * Specifies standard timing measurements for a given operation.
 */
public interface TimeStatistic extends Statistic {

    /*
     * Returns the number of times the operation was invoked since the beginning of
     * this measurement.
     */
    public long getCount();

    /*
     * Returns the maximum amount of time taken to complete one invocation of
     * this operation since the beginning of this measurement.
     */
    public long getMaxTime();

    /*
     * Returns the minimum amount of time taken to complete one invocation of this
     * operation since the beginning of this measurement.
     */
    public long getMinTime();

    /*
     * Returns the sum total of time taken to complete every invocation of this
     * operation since the beginning of this measurement. Dividing totalTime by
     * count will give you the average execution time for this operation.
     */
    public long getTotalTime();

}
