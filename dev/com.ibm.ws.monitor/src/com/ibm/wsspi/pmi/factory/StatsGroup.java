// IBM Confidential OCO Source Material
// 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/*
 * @(#)author    Srini Rangaswamy
 * @(#)version   1.1
 * @(#)date      03/15/03
 */
package com.ibm.wsspi.pmi.factory;

import javax.management.ObjectName;

import com.ibm.websphere.pmi.stat.MBeanStatDescriptor;

/**
 * StatsGroup is a logical collection of similar StatsInstance.
 * The group will provide an aggregated view of all the instances that are part of the group by
 * aggregating the corresponding statistics in the individual instances.
 * 
 * <p>
 * The aggregation logic is handled by WebSphere Performance Monitoring Infrastructure (PMI).
 * 
 * @ibm-spi
 */

public interface StatsGroup {
    /**
     * Returns the name of the group
     * 
     * @return instance name
     */
    public String getName();

    /**
     * Return the MBean name associated with this StatsGroup.
     * Return null if no MBean is associated.
     * 
     * @return MBean ObjectName
     */
    public ObjectName getMBean();

    /**
     * Associate a managed object MBean with this StatsGroup.
     * This is required to access the statistics by calling getStats() on the managed object MBean.
     * 
     * @param mBeanName managed object ObjectName
     */
    public void setMBean(ObjectName mBeanName);

    /**
     * Return the current instrumentation/monitoring level for this StatsGroup. The instrumentation
     * level will only affect the grouping ability of the StatsGroup and not the instrumentation level
     * of the individual StatsInstance.
     * 
     * The instrumentation level is set via Administrative Console, WSAdmin, PerfMBean and PMI API.
     * The default instrumentaion level is LEVEL_NONE when the instance is created.
     * The various levels are defined in com.ibm.websphere.pmi.PmiConstants
     * 
     * @return instrumentation level
     */
    public int getInstrumentationLevel();

    /**
     * Returns the MBeanStatDescriptor for this StatsGroup.
     * If an MBean is associated with the StatsGroup then the ObjectName will be returned as part of the MBeanStatDescriptor.
     * 
     * @deprecated No replacement.
     * @return MBeanStatDescriptor of the StatsGroup
     */
    public MBeanStatDescriptor getMBeanStatDescriptor();
}
