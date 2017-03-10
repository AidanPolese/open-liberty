//%Z% %I% %W% %G% %U% [%H% %T%]
/**
 * COMPONENT_NAME: WAS.ras
 *
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70(C) COPYRIGHT International Business Machines Corp. 2004, 2009
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * DESCRIPTION:
 *
 * Change History:
 *
 * Reason      Version   Date        User id     Description
 * ----------------------------------------------------------------------------
 * LIDB1241        6.0   12-29-2003  dbourne     JSR47
 * LIDB2667        6.0   02-05-2004  dbourne     CBE  
 * D211881         6.0   06-23-2004  dbourne     Populate msgId for message level Tr requests
 * D210661         6.0   07-14-2004  dbourne     use RasHelper throwableToString for stacks    
 * D199289         6.0   08-04-2004  dbourne     PERF: code change to avoid instanceof for TraceEvent and MessageEvent
 * D211668         6.0   08-13-2004  dbourne     PERF: reduce getResourceAsStream calls in startup
 * D212374         6.0   08-18-2004  dbourne     Add support for SystemOut with Trace
 * D222959         6.0   08-24-2004  vratnala    PERF: reduce CBE getExtendedDataElements calls and use less expensive Content Handlers
 * D225693         6.0   09-09-2004  vratnala    CBE Code Review Changes
 * D228260         6.0   09-16-2004  vratnala    Using Optimized constructor for CommonBaseEventLogRecord from hyades 3.0.1
 * D234703         6.0   09-26-2004  dbourne     Use root instead of empty string for root logger name (workaround for empty string EDE bug)
 * LIDB2667.13     6.0   10-03-2004  dbourne     CBE Logging on z/OS
 * D215160       6.0.1   01-12-2004  dbourne     update for special byte array handling
 * D252997       6.0.1   02-02-2005  dbourne     fix for NPE
 * D255260       6.0.1   02-14-2005  dbourne     fix for ArrayOutOfBoundsException
 * D272643       6.0.2   05-07-2005  dbourne     fix for compliance of CBE
 * LIDB3816-39.1   7.0   09-12-2005  mcasile     Extension for DPID in LogRecord
 * PK13816       6.0.2   10-25-2005  tomasz      added "for service log only" flag to records created from event with service type
 * PK24050       6.0.2   05-04-2006  andymc      Fix to enable zOS WTO on Tr.AUDIT types - also ref d376503
 * D377723       7.0     07-17-2006  andymc      Removed check for isWTO - ref PK24050
 * PK52871       7.0     09-14-2007  andymc      If we cannot create a CBELR, use WsLogRecord instead
 * F001340       8.0     09/04/2009  spaungam    Update for HPEL
 */

package com.ibm.ws.logging.object.hpel;

import java.util.logging.LogRecord;

import com.ibm.ws.logging.internal.WsLogRecord;

public class HpelLogRecordFactory {

    /**
     * If logRecord is convertible to WsLogRecord then convert and return
     * Otherwise return null
     * 
     * @param logRecord
     * @return WsLogRecord
     */
    public static WsLogRecord getWsLogRecordIfConvertible(LogRecord logRecord) {

        if (logRecord instanceof WsLogRecord)
            return (WsLogRecord) logRecord;

        return null;
    }
}
