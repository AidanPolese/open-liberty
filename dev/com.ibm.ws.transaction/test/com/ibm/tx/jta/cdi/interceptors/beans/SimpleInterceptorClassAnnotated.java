/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2015
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.tx.jta.cdi.interceptors.beans;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

@Transactional(value = TxType.MANDATORY, dontRollbackOn = RuntimeException.class)
public class SimpleInterceptorClassAnnotated extends BaseBean {

}
