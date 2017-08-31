/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.microprofile.faulttolerance_fat.cdi.beans;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.faulttolerance.Bulkhead;

import com.ibm.ws.microprofile.faulttolerance_fat.cdi.TestConstants;

@ApplicationScoped
public class BulkheadMultiRequestBean {

    @Bulkhead(2)
    public Boolean connectC(String data) throws InterruptedException {
        Thread.sleep(TestConstants.WORK_TIME);
        return Boolean.TRUE;
    }

}
