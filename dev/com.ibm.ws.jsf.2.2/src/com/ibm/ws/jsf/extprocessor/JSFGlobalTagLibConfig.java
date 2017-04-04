/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ws.jsf.extprocessor;

import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;

import com.ibm.wsspi.jsp.taglib.config.GlobalTagLibConfig;
import com.ibm.wsspi.jsp.taglib.config.TldPathConfig;

/**
 * The JSFGlobalTagLibConfig provides the definition of the tag libraries provided by the
 * JSF code and how those tag libraries can be located.
 * 
 * The values are provided are derived from the taglibcacheconfig.xml file
 * (Version 1.2 of the WASX.SERV1 file SERV1/ws/code/jsf.myfaces/src-ibm/2.0.0-SNAPSHOT/META-INF/taglibcacheconfig.xml)
 * 
 */
public class JSFGlobalTagLibConfig extends GlobalTagLibConfig {
    public JSFGlobalTagLibConfig() {
        super();

        setJarName("jsf-tld.jar");

        addtoTldPathList(new TldPathConfig("META-INF/myfaces_core.tld", "http://java.sun.com/jsf/core", "true"));
        addtoTldPathList(new TldPathConfig("META-INF/myfaces_html.tld", "http://java.sun.com/jsf/html", null));

        setClassloader(AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                return JSFGlobalTagLibConfig.class.getClassLoader();
            }
        }));

        setJarURL(AccessController.doPrivileged(new PrivilegedAction<URL>() {
            public URL run() {
                return getClassloader().getResource("META-INF/myfaces_core.tld");
            }
        }));
    }

    @SuppressWarnings("unchecked")
    private void addtoTldPathList(TldPathConfig tldPathConfig) {
        getTldPathList().add(tldPathConfig);
    }
}
