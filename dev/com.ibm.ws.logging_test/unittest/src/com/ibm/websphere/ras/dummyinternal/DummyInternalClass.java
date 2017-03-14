/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.ras.dummyinternal;

import com.ibm.websphere.ras.dummyspec.ExceptionMaker;

/**
 *
 */
public class DummyInternalClass implements ExceptionMaker {

    /**
     * @param truncatableThrowableTest
     * @return
     */
    public Exception callback(ExceptionMaker maker) {
        return maker.constructException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.websphere.ras.TruncatableThrowable.ExceptionMaker#constructException()
     */
    @Override
    public Exception constructException() {
        return new Exception();
    }

}
