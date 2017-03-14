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
package com.ibm.websphere.ras.dummyspec;

/**
 *
 */
public class AnotherDummySpecClass implements ExceptionMaker {

    private final ExceptionMaker maker;

    /**
     * @param dummyInternalClass
     */
    public AnotherDummySpecClass(ExceptionMaker maker) {
        this.maker = maker;
    }

    public AnotherDummySpecClass() {
        this.maker = null;
    }

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
        if (maker == null) {
            return new Exception();
        } else {
            return maker.constructException();
        }

    }

}
