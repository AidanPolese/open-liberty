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
 * The Statistic interface and its subinterfaces specify the required accessors which
 * provide the performance data described by the specific attributes in the Stats
 * interfaces. The Statistic subinterfaces specify accessors which provide statistical
 * data about count, time, and both bounded and unbounded ranges.
 */
public interface Statistic {

    /*
     * Returns the name of this Statistic. The name must always correspond to the
     * name of the Stats accessor that is providing the data for this statistic.
     */
    public String getName();

    /*
     * Returns the unit of measurement for this Statistic.
     * Valid values for TimeStatistic measurements are “HOUR”, “MINUTE”,
     * “SECOND”, “MILLISECOND”, “MICROSECOND” and “NANOSECOND”.
     */
    public String getUnit();

    /*
     * Returns a human-readable description of the Statistic.
     */
    public String getDescription();

    /*
     * Returns the time the first measurment was taken represented as a long, whose
     * value is the number of milliseconds since January 1, 1970, 00:00:00.
     */
    public long getStartTime();

    /*
     * Returns the time the most recent measurment was taken represented as a long,
     * whose value is the number of milliseconds since January 1, 1970, 00:00:00.
     */
    public long getLastSampleTime();

}
