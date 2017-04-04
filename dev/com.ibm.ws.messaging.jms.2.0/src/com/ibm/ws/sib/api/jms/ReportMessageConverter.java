/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason       Date    Origin  Description
 * ------------ ------  ------- -----------------------------------------------
 * 325186.1     051123  susana  Original
 * ============================================================================
 */
package com.ibm.ws.sib.api.jms;

import javax.jms.JMSException;

import com.ibm.wsspi.sib.core.SIBusMessage;

public interface ReportMessageConverter {

  public void setIntegerReportOption(String propName, int propValue, SIBusMessage coreMsg)throws JMSException;

  public Object getReportOption(String propName, SIBusMessage coreMsg);

}
