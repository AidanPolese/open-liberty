// @(#) 1.2 SERV1/ws/code/ffdc.bundle/src/com/ibm/ffdc/util/provider/Incident.java, WAS.ffdc.bundle, WASX.SERV1, uu1231.02 11/15/07 08:01:50 [8/3/12 15:55:47]
/**
 * COMPONENT_NAME: WAS.ffdc
 *
 * ORIGINS: 27         (used for IBM originated files)
 *
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2007
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * DESCRIPTION:
 *
 * Change History:
 *
 * Reason        Version  Date        User id     Description
 * ----------------------------------------------------------------------------
 * li4366.08     7.0      10/09/2007  mcasile     Original code
 * 477704.5      7.0      11/15/2007  mcasile     Insert appropriate copyright/oco info
 */
package com.ibm.wsspi.logging;

import java.util.Date;

public interface Incident {
    public String getSourceId();
    public String getProbeId();
    public String getExceptionName();
    int getCount();
    long getTimeStamp();
    Date getDateOfFirstOccurrence();
    String getLabel();
    public long getThreadId();
    public String getIntrospectedCallerDump();
}
