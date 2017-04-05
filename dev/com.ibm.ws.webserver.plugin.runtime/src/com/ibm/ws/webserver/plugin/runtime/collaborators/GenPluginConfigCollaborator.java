package com.ibm.ws.webserver.plugin.runtime.collaborators;

/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */

import javax.servlet.SessionCookieConfig;

import org.osgi.service.component.annotations.Component;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.webcontainer.webapp.WebApp;
import com.ibm.ws.webcontainer.webapp.WebAppConfiguration;
import com.ibm.ws.webserver.plugin.runtime.listeners.GeneratePluginConfigListener;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.webcontainer.collaborator.WebAppInitializationCollaborator;
import com.ibm.wsspi.webcontainer.webapp.WebAppConfig;

/**
 * This class provides a plug point into the web container at a certain state of the servlets.
 * We are specifically interested in the state when the servlets are stopping.
 */
@Component(service = { WebAppInitializationCollaborator.class },
                immediate = true,
                property = { "service.vendor=IBM" })
public class GenPluginConfigCollaborator implements WebAppInitializationCollaborator {
    private static final TraceComponent tc = Tr.register(GenPluginConfigCollaborator.class);

    /** {@inheritDoc} */
    @Override
    public void starting(Container moduleContainer) {
        // Do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void started(Container moduleContainer) {

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(this, tc, "application started. moduleContainer: " + moduleContainer.getName());

        WebAppConfiguration webAppConfig;
        try {
            webAppConfig = (WebAppConfiguration) moduleContainer.adapt(WebAppConfig.class);
            WebApp webApp = webAppConfig.getWebApp();
            SessionCookieConfig sccfg = webApp.getSessionCookieConfig();

            GeneratePluginConfigListener gpcl = GeneratePluginConfigListener.getGeneratePluginConfigListener();
            if (gpcl != null) {
                gpcl.applicationInitialized(webApp, sccfg);
            }
        } catch (UnableToAdaptException e) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(this, tc, "application started. cannot adapt to webapp - ignore");
        }

    }

    /** {@inheritDoc} */
    @Override
    public void stopping(Container moduleContainer) {

        // Do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void stopped(Container moduleContainer) {
        // Do nothing
    }
}