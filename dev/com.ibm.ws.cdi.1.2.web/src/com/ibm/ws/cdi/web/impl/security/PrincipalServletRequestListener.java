/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi.web.impl.security;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

/**
 * Class sets/removes HttpServletRequest to enable access to
 * HttpServletRequest.getUserPrincipal()
 */
public class PrincipalServletRequestListener implements ServletRequestListener {

    @Override
    public void requestDestroyed(ServletRequestEvent event) {
        WebSecurityContextStore.getCurrentInstance().removeHttpServletRequest();
    }

    @Override
    public void requestInitialized(ServletRequestEvent event) {
        ServletRequest request = event.getServletRequest();
        if (request instanceof HttpServletRequest) {
            WebSecurityContextStore.getCurrentInstance().storeHttpServletRequest((HttpServletRequest) request);
        }
    }

}
