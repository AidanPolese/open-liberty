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
 * Specifies standard measurements of the lowest and highest values an attribute has
 * held as well as its current value.
 */
public interface RangeStatistic extends Statistic {

    /*
     * Returns the highest value this attribute has held since the beginning of the
     * measurement.
     */
    public long getHighWaterMark();

    /*
     * Returns the lowest value this attribute has held since the beginning of the
     * measurement.
     */
    public long getLowWaterMark();

    /*
     * Returns the current value of this attribute.
     */
    public long getCurrent();

}
