/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.anno.test.data;

import javax.annotation.Resource;
import javax.annotation.Resource.AuthenticationType;

import com.ibm.ws.anno.test.data.sub.SubBase;

/**
 *
 */
@Resource(name = "/B", authenticationType = AuthenticationType.APPLICATION)
public class BClass extends SubBase implements CIntf {
    static {
        int i = 0;
    }

    public BClass() {

    }

    @Override
    public void publicMethod() {}

    @Override
    public Integer publicMethod(int n) {
        return null;
    }

    private void privateMethod() {}

    @Override
    @Resource
    public Integer n() {
        // TODO Auto-generated method stub
        return null;
    }
}
