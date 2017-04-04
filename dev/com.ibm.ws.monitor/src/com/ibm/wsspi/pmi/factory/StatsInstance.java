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
import com.ibm.wsspi.pmi.stat.SPIStatistic;

/**
 * StatsInstance represents a single instance of the Stats template.
 * The instance will have all the statistics defined in the template.
 * The Stats template XML file is defined using the stats.dtd at com/ibm/websphere/pmi/xml
 * 
 * @ibm-spi
 */

public interface StatsInstance {
    /**
     * Returns the name of the instance
     * 
     * @return instance name
     */
    public String getName();

    /**
     * Return the MBean name associated with this StatsInstance.
     * Return null if no MBean is associated.
     * 
     * @return MBean ObjectName
     */
    public ObjectName getMBean();

    /**
     * Associate a managed object MBean with this StatsInstance.
     * This is required to access the statistics by calling getStats() on the managed object MBean.
     * 
     * @param mBeanName managed object ObjectName
     */
    public void setMBean(ObjectName mBeanName);

    /**
     * Return the current instrumentation/monitoring level for this StatsInstance.
     * The instrumentation level is set via Administrative Console, WSAdmin, PerfMBean and PMI API.
     * The default instrumentaion level is LEVEL_NONE when the instance is created.
     * The various levels are defined in com.ibm.websphere.pmi.PmiConstants
     * 
     * @return instrumentation level
     */
    public int getInstrumentationLevel();

    /**
     * Returns a statistic by ID. The ID is defined in the Stats template.
     * 
     * @param id Statistic ID
     * @return Statistic
     */
    public SPIStatistic getStatistic(int id);

    /**
     * Returns the MBeanStatDescriptor for this StatsInstance.
     * If an MBean is associated with the StatsInstance then the ObjectName will be returned as part of the MBeanStatDescriptor.
     * 
     * @deprecated No replacement.
     * @return MBeanStatDescriptor of the StatsInstance
     */
    public MBeanStatDescriptor getMBeanStatDescriptor();

    /*
     * ~~~~~~~~~~~~~~ commented ~~~~~~~~~~~~~~
     * /--
     * Add a StatisticCreatedListener to the StatsInstance
     * 
     * @param scl StatisticCreatedListener
     * -/
     * public void addStatisticCreatedListener (StatisticCreatedListener scl);
     * 
     * /--
     * Remove a StatisticCreatedListener from the StatsInstance
     * 
     * @param scl StatisticCreatedListener
     * -/
     * public void removeStatisticCreatedListener (StatisticCreatedListener scl);
     * ~~~~~~~~~~~~~~ commented ~~~~~~~~~~~~~~
     */
}
