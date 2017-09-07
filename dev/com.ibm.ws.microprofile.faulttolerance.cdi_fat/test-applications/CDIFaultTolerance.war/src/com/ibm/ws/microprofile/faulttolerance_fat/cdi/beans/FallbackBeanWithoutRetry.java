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

import javax.enterprise.context.RequestScoped;

import org.eclipse.microprofile.faulttolerance.Fallback;

import com.ibm.ws.microprofile.faulttolerance_fat.util.ConnectException;
import com.ibm.ws.microprofile.faulttolerance_fat.util.Connection;

@RequestScoped
@Fallback(MyFallbackHandler.class)
public class FallbackBeanWithoutRetry {

    private int connectCountA = 0;

    public Connection connectA() throws ConnectException {
        throw new ConnectException("FallbackBean.connectA: " + (++connectCountA));
    }

    public int getConnectCountA() {
        return connectCountA;
    }

}
