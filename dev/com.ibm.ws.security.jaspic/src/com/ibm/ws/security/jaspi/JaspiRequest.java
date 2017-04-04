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
package com.ibm.ws.security.jaspi;

import java.util.List;

import javax.security.auth.message.MessageInfo;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.ws.webcontainer.security.WebRequest;
import com.ibm.ws.webcontainer.security.WebSecurityContext;
import com.ibm.ws.webcontainer.security.metadata.LoginConfiguration;
import com.ibm.ws.webcontainer.security.util.WebConfigUtils;
import com.ibm.wsspi.webcontainer.webapp.WebAppConfig;

/**
 * This class contains the JASPI related items that on twas
 * were added to the WebRequest object.
 */
public class JaspiRequest {

    private WebRequest webRequest = null;
    private MessageInfo msgInfo = null;
    private boolean isLogoutMethod = false;
    private String userid = null;
    @Sensitive
    private String password = null;
    private String appContext = null;
    private String appName = null;
    private String moduleName = null;
    private WebAppConfig wac = null;

    /**
     * @param webRequest
     */
    public JaspiRequest(WebRequest webRequest, WebAppConfig wac) {
        this.webRequest = webRequest;
        this.wac = wac;
    }

    /**
     * @return
     */
    public String getAppContext() {
        if (appContext == null) {
            String vHost = null;
            String contextRoot = null;
            WebAppConfig appCfg = WebConfigUtils.getWebAppConfig();
            if (appCfg != null) {
                vHost = appCfg.getVirtualHostName();
                contextRoot = appCfg.getContextRoot();
                appContext = vHost + " " + contextRoot;
            }
            else {
                if (wac != null) {
                    vHost = wac.getVirtualHostName();
                    contextRoot = wac.getContextRoot();
                    appContext = vHost + " " + contextRoot;
                }
            }
        }
        return appContext;
    }

    /**
     * @return
     */
    public WebSecurityContext getWebSecurityContext() {
        return webRequest.getWebSecurityContext();
    }

    /**
     * @return
     */
    public MessageInfo getMessageInfo() {
        return msgInfo;
    }

    /**
     * @param msgInfo
     */
    public void setMessageInfo(MessageInfo messageInfo) {
        msgInfo = messageInfo;
    }

    /**
     * @return
     */
    public HttpServletRequest getHttpServletRequest() {
        return webRequest.getHttpServletRequest();
    }

    /**
     * @return
     */
    public HttpServletResponse getHttpServletResponse() {
        return webRequest.getHttpServletResponse();
    }

    /**
     * @return
     */
    public boolean isLogoutMethod() {
        return isLogoutMethod;
    }

    public void setLogoutMethod(boolean isLogout) {
        isLogoutMethod = isLogout;
    }

    /**
     * @return
     */
    public String getUserid() {
        return userid;
    }

    /**
     * 
     * @param id
     */
    public void setUserid(String id) {
        userid = id;
    }

    /**
     * @return
     */
    public String getPassword() {
        return password;
    }

    public void setPassword(@Sensitive String pwd) {
        password = pwd;
    }

    /**
     * @return
     */
    public LoginConfiguration getLoginConfig() {
        return webRequest.getLoginConfig();
    }

    /**
     * The request is protected if there are required roles
     * 
     * @return
     */
    public boolean isProtected() {
        List<String> requiredRoles = null;
        return webRequest.getMatchResponse() != null &&
               (requiredRoles = webRequest.getRequiredRoles()) != null &&
               !requiredRoles.isEmpty();
    }

    /**
     * @return
     */
    public String getApplicationName() {
        if (appName == null) {
            WebAppConfig appCfg = WebConfigUtils.getWebAppConfig();
            if (appCfg != null) {
                appName = appCfg.getModuleName();
            }
            else {
                if (wac != null) {
                    appName = wac.getModuleName();
                }
            }
        }
        return appName;
    }

    /**
     * @return
     */
    public String getModuleName() {
        if (moduleName == null) {
            WebAppConfig appCfg = WebConfigUtils.getWebAppConfig();
            if (appCfg != null) {
                moduleName = appCfg.getModuleName();
            }
            else {
                if (wac != null) {
                    moduleName = wac.getModuleName();
                }
            }
        }
        return moduleName;
    }

    /**
     * 
     */
    public WebRequest getWebRequest() {
        return webRequest;
    }
}
