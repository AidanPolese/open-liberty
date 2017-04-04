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

import com.ibm.wsspi.anno.util.Util_InternMap.ValueType;

public interface Util_Factory {
    String getHashText();

    Set<String> createIdentityStringSet();

    Util_InternMap createInternMap(ValueType valueType, String name);

    Util_BidirectionalMap createBidirectionalMap(ValueType holderType, String holderTag,
                                                 ValueType heldType, String heldTag);

    Util_BidirectionalMap createBidirectionalMap(ValueType holderType, String holderTag,
                                                 ValueType heldType, String heldTag,
                                                 boolean isEnabled);
}
