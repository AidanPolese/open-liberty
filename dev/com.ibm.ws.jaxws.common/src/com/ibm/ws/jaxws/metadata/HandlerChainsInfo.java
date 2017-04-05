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
package com.ibm.ws.jaxws.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class HandlerChainsInfo implements Serializable {

    private static final long serialVersionUID = -3595218623301350295L;

    private final List<HandlerChainInfo> handlerChainInfos = new ArrayList<HandlerChainInfo>();

    public List<HandlerChainInfo> getHandlerChainInfos() {
        return Collections.unmodifiableList(handlerChainInfos);
    }

    public boolean addHandlerChainInfo(HandlerChainInfo handlerChainInfo) {
        return handlerChainInfos.add(handlerChainInfo);
    }

    public boolean removeHandlerChainInfo(HandlerChainInfo handlerChainInfo) {
        return handlerChainInfos.remove(handlerChainInfo);
    }

    public List<HandlerInfo> getAllHandlerInfos() {
        List<HandlerInfo> handlerInfos = new ArrayList<HandlerInfo>();

        for (HandlerChainInfo chainInfo : handlerChainInfos) {
            handlerInfos.addAll(chainInfo.getHandlerInfos());
        }
        return handlerInfos;
    }

}
