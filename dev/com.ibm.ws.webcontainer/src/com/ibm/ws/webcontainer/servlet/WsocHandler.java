/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2015
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
package com.ibm.ws.webcontainer.servlet;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface to check if the request is a WebSocket request or not. Implementation of this interface is done by WebSocket component. 
 * This will invoked by WebContainer after all the filters are invoked and before invoking the servlet. 
 */
public interface WsocHandler {
    /*
     * Indicates whether this is a WebSocket request or not. Based on this WebContainer will call WebSocket servlet or vanilla html/servlet 
     * which are registered with the same URI.
     * 
     * @param ServletRequest  
     * @return true indicates that the request is a WebSocket request. False indicates that the request is not a WebSocket request
     */
    public boolean isWsocRequest(ServletRequest request) throws ServletException;
    
    public void handleRequest(HttpServletRequest request, HttpServletResponse response);
}
