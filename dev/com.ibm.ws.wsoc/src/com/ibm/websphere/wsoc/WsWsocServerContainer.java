/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.wsoc;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

/**
 * This interface provides WebSphere specific extensions to WebSocket ServerContainer support.
 * 
 * Example:
 * <pre>
 * <code>
 *     ServerContainer container = (ServerContainer) httpServletRequest.getServletContext().getAttribute("javax.websocket.server.ServerContainer");
 *     if (container instanceof WsWsocServerContainer) {
 *         WsWsocServerContainer ws = (WsWsocServerContainer) container;
 *         ...
 *     }
 * </code>
 * </pre>
 */
public interface WsWsocServerContainer extends ServerContainer {

    /**
     * Performs a WebSocket upgrade on provided HttpServletRequest and HttpServletResponse with the specified ServerEndpointConfig. After a call to doUpgrade, the servlet response
     * is committed and you will be unable to write additional data or change the response code.
     * 
     * 
     * @param req -
     * @param resp -
     * @param serverEndpointConfig - server endpoint config object representing a WebSocket endpoint - either programmatic or annotated.
     * @param pathParams - additional parameters that will be made availble thorugh wsoc Session.getRequestParameterMap
     * 
     * 
     * @throws ServletException
     * @throws IOException
     */
    public void doUpgrade(HttpServletRequest req, HttpServletResponse resp, ServerEndpointConfig sec, Map<String, String> pathParams) throws ServletException, IOException;

}
