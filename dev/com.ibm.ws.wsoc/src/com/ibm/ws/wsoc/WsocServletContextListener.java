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
package com.ibm.ws.wsoc;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.kernel.service.utils.FrameworkState;

/**
 *
 */
public class WsocServletContextListener implements ServletContextListener {

    EndpointManager endpointManager = null;
    private static final TraceComponent tc = Tr.register(WsocServletContextListener.class);

    public void initialize(EndpointManager em) {
        endpointManager = em;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        if (endpointManager != null) {
            if (!FrameworkState.isStopping()) {
                endpointManager.closeAllOpenSessions();
                endpointManager.clear();
            }
            else {
                if (tc.isDebugEnabled()) {
                    Tr.debug(tc, "Server is being shutdown, no need to stop stop clients");
                }
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent arg0) {

    }

}
