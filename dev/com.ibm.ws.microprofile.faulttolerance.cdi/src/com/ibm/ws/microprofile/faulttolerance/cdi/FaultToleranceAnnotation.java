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
package com.ibm.ws.microprofile.faulttolerance.cdi;

import java.lang.annotation.Annotation;

/**
 *
 */
public class FaultToleranceAnnotation implements Annotation {

    /** {@inheritDoc} */
    @Override
    public Class<? extends Annotation> annotationType() {
        return FaultTolerance.class;
    }

}
