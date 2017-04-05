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
package com.ibm.ws.container.service.config;

import com.ibm.ws.javaee.dd.web.WebApp;
import com.ibm.ws.javaee.dd.webbnd.WebBnd;
import com.ibm.ws.javaee.dd.webext.WebExt;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;

/**
 *
 */
public interface ServletConfiguratorHelper {

    public void configureInit() throws UnableToAdaptException;

    public void configureFromWebApp(WebApp webApp) throws UnableToAdaptException;

    public void configureFromWebFragment(WebFragmentInfo webFragmentItem) throws UnableToAdaptException;

    public void configureFromAnnotations(WebFragmentInfo webFragmentItem) throws UnableToAdaptException;

    public void configureDefaults() throws UnableToAdaptException;

    public void configureWebBnd(WebBnd webBnd) throws UnableToAdaptException;

    public void configureWebExt(WebExt webExt) throws UnableToAdaptException;

    public void finish() throws UnableToAdaptException;
}
