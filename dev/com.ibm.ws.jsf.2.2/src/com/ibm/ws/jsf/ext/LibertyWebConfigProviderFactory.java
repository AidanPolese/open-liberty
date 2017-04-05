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
package com.ibm.ws.jsf.ext;

import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;

import org.apache.myfaces.shared.util.ClassUtils;
import org.apache.myfaces.spi.ServiceProviderFinderFactory;
import org.apache.myfaces.spi.WebConfigProvider;
import org.apache.myfaces.spi.WebConfigProviderFactory;

/**
 * return the LibertyWebConfigProvider as the default WebConfigProvider
 * if user did not defint its SPI service
 */
public class LibertyWebConfigProviderFactory extends WebConfigProviderFactory {

    public static final String WEB_CONFIG_PROVIDER = WebConfigProvider.class.getName();

    public static final String WEB_CONFIG_PROVIDER_LIST = WebConfigProvider.class.getName() + ".LIST";

    private Logger getLogger() {
        return Logger.getLogger(LibertyWebConfigProviderFactory.class.getName());
    }

    @Override
    public WebConfigProvider getWebConfigProvider(ExternalContext externalContext) {
        
        WebConfigProvider returnValue = null;
        final ExternalContext extContext = externalContext;
        try {
            if (System.getSecurityManager() != null) {
                returnValue = AccessController.doPrivileged(new PrivilegedExceptionAction<WebConfigProvider>()
                        {
                            public WebConfigProvider run() throws ClassNotFoundException,
                                            NoClassDefFoundError,
                                            InstantiationException,
                                            IllegalAccessException,
                                            InvocationTargetException,
                                            PrivilegedActionException
                            {
                                return resolveWebXmlProviderFromService(extContext);
                            }
                        });
            } else {
                returnValue = resolveWebXmlProviderFromService(extContext);
            }
        } catch (ClassNotFoundException e) {
            // ignore
        } catch (NoClassDefFoundError e) {
            // ignore
        } catch (InstantiationException e) {
            getLogger().log(Level.SEVERE, "", e);
        } catch (IllegalAccessException e) {
            getLogger().log(Level.SEVERE, "", e);
        } catch (InvocationTargetException e) {
            getLogger().log(Level.SEVERE, "", e);
        } catch (PrivilegedActionException e) {
            throw new FacesException(e);
        }

        return returnValue;
    }

    private WebConfigProvider resolveWebXmlProviderFromService(
                                                               ExternalContext externalContext) throws ClassNotFoundException,
                    NoClassDefFoundError,
                    InstantiationException,
                    IllegalAccessException,
                    InvocationTargetException,
                    PrivilegedActionException {
        
        List<String> classList = (List<String>) externalContext.getApplicationMap().get(WEB_CONFIG_PROVIDER_LIST);
        if (classList == null) {
            classList = ServiceProviderFinderFactory.getServiceProviderFinder(externalContext).
                            getServiceProviderList(WEB_CONFIG_PROVIDER);
            externalContext.getApplicationMap().put(WEB_CONFIG_PROVIDER_LIST, classList);
        }

        return ClassUtils.buildApplicationObject(WebConfigProvider.class, classList, new LibertyWebConfigProvider());
    }

}
