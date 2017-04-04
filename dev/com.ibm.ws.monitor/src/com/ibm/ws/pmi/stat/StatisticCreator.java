/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2004
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
/*
 * Created on Oct 21, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.ibm.ws.pmi.stat;

import com.ibm.wsspi.pmi.stat.SPIAverageStatistic;
import com.ibm.wsspi.pmi.stat.SPIBoundaryStatistic;
import com.ibm.wsspi.pmi.stat.SPIBoundedRangeStatistic;
import com.ibm.wsspi.pmi.stat.SPICountStatistic;
import com.ibm.wsspi.pmi.stat.SPIDoubleStatistic;
import com.ibm.wsspi.pmi.stat.SPIRangeStatistic;
import com.ibm.wsspi.pmi.stat.SPITimeStatistic;

/**
 * @author joelm
 * 
 *         To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public class StatisticCreator {

    public static SPIAverageStatistic createAverageStatistic(int dataId) {
        return new AverageStatisticImpl(dataId);
    }

    public static SPIBoundaryStatistic createBoundaryStatistic(int dataId) {
        return new BoundaryStatisticImpl(dataId);
    }

    public static SPIBoundedRangeStatistic createBoundedRangeStatistic(int dataId) {
        return new BoundedRangeStatisticImpl(dataId);
    }

    public static SPICountStatistic createCountStatistic(int dataId) {
        return new CountStatisticImpl(dataId);
    }

    public static SPIDoubleStatistic createDoubleStatistic(int dataId) {
        return new DoubleStatisticImpl(dataId);
    }

    public static SPIRangeStatistic createRangeStatistic(int dataId) {
        return new RangeStatisticImpl(dataId);
    }

    public static SPITimeStatistic createTimeStatistic(int dataId) {
        return new TimeStatisticImpl(dataId);
    }
}
