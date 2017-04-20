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

public class SimpleInterceptorMethodAnnotated extends BaseBean {

    @Override
    @Transactional(value = TxType.MANDATORY, rollbackOn = Exception.class)
    public void baseMethod() {}

}
