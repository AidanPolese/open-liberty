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

package com.ibm.ws.anno.util.internal;

import java.text.MessageFormat;
import java.util.Set;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.anno.service.internal.AnnotationServiceImpl_Logging;
import com.ibm.wsspi.anno.util.Util_BidirectionalMap;
import com.ibm.wsspi.anno.util.Util_Exception;
import com.ibm.wsspi.anno.util.Util_Factory;
import com.ibm.wsspi.anno.util.Util_InternMap.ValueType;

public class UtilImpl_Factory implements Util_Factory {
    private static final TraceComponent tc = Tr.register(UtilImpl_Factory.class);
    public static final String CLASS_NAME = UtilImpl_Factory.class.getName();

    //

    protected String hashText;

    @Override
    public String getHashText() {
        return hashText;
    }

    //

    public UtilImpl_Factory() {
        super();

        this.hashText = AnnotationServiceImpl_Logging.getBaseHash(this);

        if (tc.isDebugEnabled()) {
            Tr.debug(tc, MessageFormat.format("[ {0} ] Created", this.hashText));
        }
    }

    //

    public Util_Exception newUtilException(TraceComponent logger, String message) {
        Util_Exception exception = new Util_Exception(message);

        if (logger.isEventEnabled()) {
            Tr.event(logger, exception.getMessage(), exception);
        }

        return exception;
    }

    //

    @Override
    public Set<String> createIdentityStringSet() {
        return new UtilImpl_IdentityStringSet();
    }

    @Override
    public UtilImpl_InternMap createInternMap(ValueType valueType, String name) {
        return new UtilImpl_InternMap(this, valueType, name);
    }

    @Override
    public UtilImpl_BidirectionalMap createBidirectionalMap(ValueType holderType, String holderTag,
                                                            ValueType heldType, String heldTag) {
        return createBidirectionalMap(holderType, holderTag,
                                      heldType, heldTag,
                                      Util_BidirectionalMap.IS_ENABLED);
    }

    @Override
    public UtilImpl_BidirectionalMap createBidirectionalMap(ValueType holderType, String holderTag,
                                                            ValueType heldType, String heldTag,
                                                            boolean isEnabled) {

        UtilImpl_InternMap heldInternMap = createInternMap(holderType, holderTag);
        UtilImpl_InternMap holderInternMap = createInternMap(heldType, heldTag);

        return createBidirectionalMap(heldTag, heldInternMap, holderTag, holderInternMap, isEnabled);
    }

    //

    public UtilImpl_BidirectionalMap createBidirectionalMap(String holderTag, UtilImpl_InternMap holderInternMap,
                                                            String heldTag, UtilImpl_InternMap heldInternMap) {
        return createBidirectionalMap(holderTag, holderInternMap,
                                      heldTag, heldInternMap,
                                      Util_BidirectionalMap.IS_ENABLED);
    }

    public UtilImpl_BidirectionalMap createBidirectionalMap(String holderTag, UtilImpl_InternMap holderInternMap,
                                                            String heldTag, UtilImpl_InternMap heldInternMap,
                                                            boolean isEnabled) {
        return new UtilImpl_BidirectionalMap(this, holderTag, heldTag, holderInternMap, heldInternMap, isEnabled);
    }

}