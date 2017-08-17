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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;

import com.ibm.ws.microprofile.faulttolerance_fat.util.Connection;

@Dependent
public class MyFallbackHandler implements FallbackHandler<Connection> {

    @Inject
    private DataBean dataBean;

    @Override
    public Connection handle(ExecutionContext context) {
        System.out.println("Fallback: " + context);
        return new Connection() {

            @Override
            public String getData() {
                // TODO Auto-generated method stub
                return "Fallback for: " + context + " - " + dataBean.getData();
            }
        };
    }

}
