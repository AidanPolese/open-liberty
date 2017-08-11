/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.jsf.mojarra.cdi;

import javax.el.ELResolver;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.application.Application;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.weld.el.WeldELContextListener;

public class CDIJSFInitializerImpl {

    public static void initializeJSF(Application application) {

        //CDIService cdiService = cdiServiceRef.getService();
        //BeanManager beanManager = cdiService.getCurrentBeanManager();
        try {
            BeanManager beanManager = InitialContext.doLookup("java:comp/BeanManager");
            if (beanManager != null) {
                application.addELContextListener(new WeldELContextListener());

                //CDIRuntime cdiRuntime = (CDIRuntime) cdiService;
                // TODO: Need to verify lookup of 'java:app/Appname' is equivalent to:
                //       cdiRuntime.getCurrentApplicationContextID();
                String appname = InitialContext.doLookup("java:app/AppName");
                //application.setViewHandler(new IBMViewHandler(application.getViewHandler(), contextID));
                application.setViewHandler(new IBMViewHandlerProxy(application.getViewHandler(), appname));

                ELResolver elResolver = beanManager.getELResolver();
                application.addELResolver(elResolver);
            }
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
}
