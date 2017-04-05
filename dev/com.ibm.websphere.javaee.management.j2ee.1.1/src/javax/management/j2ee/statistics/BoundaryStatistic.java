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
 * The BoundaryStatistic interface specifies standard measurements of the upper and
 * lower limits of the value of an attribute.
 */
public interface BoundaryStatistic extends Statistic {

    /*
     * Returns the upper limit of the value of this attribute.
     */
    public long getUpperBound();

    /*
     * Returns the lower limit of the value of this attribute.
     */
    public long getLowerBound();

}
