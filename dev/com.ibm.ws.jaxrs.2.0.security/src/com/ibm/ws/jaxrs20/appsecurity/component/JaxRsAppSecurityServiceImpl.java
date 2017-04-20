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
package com.ibm.ws.jaxrs20.appsecurity.component;

import java.util.Map;

import javax.net.ssl.SSLSocketFactory;
import javax.servlet.http.Cookie;
import javax.ws.rs.ProcessingException;

import org.osgi.service.component.annotations.Component;

import com.ibm.websphere.security.web.WebSecurityHelper;
import com.ibm.ws.jaxrs20.api.JaxRsAppSecurityService;
import com.ibm.ws.jaxrs20.appsecurity.security.JaxRsSSLManager;

@Component(name = "com.ibm.ws.jaxrs20.JaxRsAppSecurityServiceImpl", property = { "service.vendor=IBM" })
public class JaxRsAppSecurityServiceImpl implements JaxRsAppSecurityService {

    @Override
    public Cookie getSSOCookieFromSSOToken() {

        Cookie ssoCookie;
        try {
            ssoCookie = WebSecurityHelper.getSSOCookieFromSSOToken();
        } catch (Exception e) {
            throw new ProcessingException(e);
        }

        return ssoCookie;
    }

    @Override
    public SSLSocketFactory getSSLSocketFactory(String sslRef, Map<String, Object> props) {

        SSLSocketFactory sslSocketFactory = JaxRsSSLManager.getProxySSLSocketFactoryBySSLRef(sslRef, null);
        return sslSocketFactory;
    }

}
