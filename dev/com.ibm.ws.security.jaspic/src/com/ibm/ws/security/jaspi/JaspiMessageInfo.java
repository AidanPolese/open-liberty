/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.jaspi;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.message.MessageInfo;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author IBM Corp.
 * 
 */
public class JaspiMessageInfo implements MessageInfo {

    private Map<?, ?> map;
    private HttpServletRequest req;
    private HttpServletResponse rsp;

    /**
	 * 
	 */
    @SuppressWarnings("unchecked")
    public JaspiMessageInfo() {
        this(new HashMap());
    }

    /**
     * @param map
     */
    public JaspiMessageInfo(Map<?, ?> map) {
        super();
        this.map = map;
    }

    /**
     * @param req
     * @param rsp
     */
    public JaspiMessageInfo(HttpServletRequest req, HttpServletResponse rsp) {
        this();
        this.req = req;
        this.rsp = rsp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.security.auth.message.MessageInfo#getMap()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map getMap() {
        if (map == null) {
            map = new HashMap();
        }
        return map;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.security.auth.message.MessageInfo#getRequestMessage()
     */
    @Override
    public Object getRequestMessage() {
        return req;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.security.auth.message.MessageInfo#getResponseMessage()
     */
    @Override
    public Object getResponseMessage() {
        return rsp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.security.auth.message.MessageInfo#setRequestMessage(java.lang.Object)
     */
    @Override
    public void setRequestMessage(Object req) {
        this.req = (HttpServletRequest) req;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.security.auth.message.MessageInfo#setResponseMessage(java.lang.Object)
     */
    @Override
    public void setResponseMessage(Object rsp) {
        // TODO behavior change here due to findbugs, it was rsp=rsp, so if something breaks...
        this.rsp = (HttpServletResponse) rsp;
    }

    @Override
    public String toString() {
        return super.toString() + "[" + req + ", " + rsp + ", map=" + map + "]";
    }

}
