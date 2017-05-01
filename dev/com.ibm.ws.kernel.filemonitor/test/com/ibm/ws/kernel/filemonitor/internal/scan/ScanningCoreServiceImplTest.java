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
package com.ibm.ws.kernel.filemonitor.internal.scan;

import com.ibm.ws.kernel.filemonitor.internal.CoreServiceImpl;
import com.ibm.ws.kernel.filemonitor.internal.CoreServiceImplTestParent;

/**
 *
 */
public class ScanningCoreServiceImplTest extends CoreServiceImplTestParent {

    /**
     * @return
     */
    @Override
    protected CoreServiceImpl instantiateCoreService() {
        return new ScanningCoreServiceImpl();
    }

}
