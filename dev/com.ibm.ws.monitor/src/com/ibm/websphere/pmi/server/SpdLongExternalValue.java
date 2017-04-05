// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/**
 * History:
 * CMVC 86523: create the file - wenjian
 *
 * 
 * SpdLongExternalValue: interface to return a long  
 */

package com.ibm.websphere.pmi.server;

import com.ibm.wsspi.pmi.stat.SPIStatistic;

public interface SpdLongExternalValue {
    public SPIStatistic getLongValue();

    public void updateStatistic();
}
