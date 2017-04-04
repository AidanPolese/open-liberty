/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corporation 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.wsspi.anno.util;

import java.util.Set;

import com.ibm.websphere.ras.TraceComponent;

// Implementation intention:
//
// className -> Set<annotationName> : tell what annotations a class has
// annotationName -> Set<className> : tell what classes have an annotation

public interface Util_BidirectionalMap {

    String getHashText();

    String getHolderTag();

    String getHeldTag();

    void logState();

    void log(TraceComponent tc);

    //

    Util_Factory getFactory();

    //

    boolean IS_ENABLED = true;
    boolean IS_NOT_ENABLED = false;

    boolean getIsEnabled();

    Util_InternMap getHolderInternMap();

    Util_InternMap getHeldInternMap();

    boolean containsHolder(String holdName);

    Set<String> getHolderSet();

    boolean containsHeld(String heldName);

    Set<String> getHeldSet();

    boolean holds(String holderName, String heldName);

    Set<String> selectHeldOf(String holderName);

    Set<String> selectHoldersOf(String heldName);
}
