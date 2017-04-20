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

import com.ibm.tx.jta.cdi.interceptors.annotations.TransactionalStereotypeSimple3;

public class SimpleStereotypeMethodAnnotated extends BaseBean {

    @Override
    @TransactionalStereotypeSimple3
    public void baseMethod() {}
}
