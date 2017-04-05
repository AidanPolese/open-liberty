/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corporation 2011, 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.wsspi.anno.info;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.anno.classsource.ClassSource_Aggregate;
import com.ibm.wsspi.anno.util.Util_Factory;

public interface InfoStoreFactory {
    String getHashText();

    //

    Util_Factory getUtilFactory();

    //

    InfoStoreException newInfoStoreException(TraceComponent logger, String message);

    InfoStoreException wrapIntoInfoStoreException(TraceComponent logger,
                                                  String callingClassName,
                                                  String callingMethodName,
                                                  String message, Throwable th);

    //

    InfoStore createInfoStore(ClassSource_Aggregate classSource) throws InfoStoreException;
}
