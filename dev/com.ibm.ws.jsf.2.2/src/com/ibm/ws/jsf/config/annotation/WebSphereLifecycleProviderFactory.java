// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2010
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

package com.ibm.ws.jsf.config.annotation;

import java.util.HashMap;

import javax.faces.context.ExternalContext;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.myfaces.config.annotation.LifecycleProvider;
import org.apache.myfaces.config.annotation.LifecycleProviderFactory;
import org.apache.myfaces.config.annotation.NoAnnotationLifecyleProvider;
import org.apache.myfaces.shared.util.ClassUtils;

import com.ibm.ws.jsf.config.annotation.WebSphereAnnotationLifecycleProvider;

public class WebSphereLifecycleProviderFactory extends LifecycleProviderFactory {
    private static HashMap<ClassLoader, LifecycleProvider> lifecycleProviders;
    private static final Logger log = Logger.getLogger(WebSphereLifecycleProviderFactory.class.getName());

    public WebSphereLifecycleProviderFactory() {
        if (lifecycleProviders == null) {
            lifecycleProviders = new HashMap<ClassLoader, LifecycleProvider>();
        }
    }

    public LifecycleProvider getLifecycleProvider(ExternalContext externalContext) {
        ClassLoader cl = ClassUtils.getContextClassLoader();
        LifecycleProvider provider = null;

        provider = lifecycleProviders.get(cl);
        if (provider == null) {
            if (externalContext != null) {
                provider = new WebSphereAnnotationLifecycleProvider(externalContext);
                lifecycleProviders.put(cl, provider);
            } else {
                if (log.isLoggable(Level.FINEST)) {
                    log.finest("ExternalContext not found, resolve fallback LifecycleProvider");
                }
                provider = new NoAnnotationLifecyleProvider();
            }

        }

        return provider;
    }

    @Override
    public void release() {
        if (log.isLoggable(Level.FINEST)) {
            log.finest("about to clear lifecycle provider map");
        }
        lifecycleProviders.clear();
    }
}