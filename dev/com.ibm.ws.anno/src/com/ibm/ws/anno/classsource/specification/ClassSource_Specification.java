/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.anno.classsource.specification;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.anno.classsource.ClassSource_Aggregate;
import com.ibm.wsspi.anno.classsource.ClassSource_Exception;
import com.ibm.wsspi.anno.classsource.ClassSource_Factory;

public interface ClassSource_Specification {
    String getHashText();

    void logState();

    void log(TraceComponent logger);

    //

    ClassSource_Factory getFactory();

    //

    ClassSource_Aggregate createClassSource(String targetName, ClassLoader rootClassLoader)
                    throws ClassSource_Exception;
}
