// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// %I% %W% %G% %U%
//
// IBM Confidential OCO Source Material
// 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 2000,2004
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  J2EENameFactoryImpl.java
//
// Source File Description:
//
//     J2EENameFactoryImpl.
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// f118829   ASV50     20020311 tkb      : New copyright/prologue
// LIDB3133-8  WASX    20040506 rschnier : Added ibm-private tag
// --------- --------- -------- --------- -----------------------------------------

package com.ibm.ws.container.service.metadata.internal;

import com.ibm.websphere.csi.J2EEName;
import com.ibm.websphere.csi.J2EENameFactory;

public class J2EENameFactoryImpl
                implements J2EENameFactory {

    @Override
    public J2EEName create(byte[] bytes) {
        return new J2EENameImpl(bytes);
    }

    @Override
    public J2EEName create(String app, String module, String component) {
        return new J2EENameImpl(app, module, component);
    }

}
