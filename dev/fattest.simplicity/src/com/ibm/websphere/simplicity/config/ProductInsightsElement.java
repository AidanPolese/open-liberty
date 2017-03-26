/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.websphere.simplicity.config;

import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 */
public class ProductInsightsElement extends ConfigElement {
    private String url;
    private String apiKey;
    private String proxyUrl;
    private String proxyUser;
    private String proxyPassword;
    private String sslRef;

    public String getUrl() {
        return url;
    }

    @XmlAttribute(name = "url")
    public void setUrl(String url) {
        this.url = url;
    }

    public String getApiKey() {
        return apiKey;
    }

    @XmlAttribute(name = "apiKey")
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getProxyUrl() {
        return proxyUrl;
    }

    @XmlAttribute(name = "proxyUrl")
    public void setProxyUrl(String proxyUrl) {
        this.proxyUrl = proxyUrl;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    @XmlAttribute(name = "proxyUser")
    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    @XmlAttribute(name = "proxyPassword")
    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public String getSslRef() {
        return sslRef;
    }

    @XmlAttribute(name = "sslRef")
    public void setSslRef(String sslRef) {
        this.sslRef = sslRef;
    }

    @Override
    public String toString() {
        return "ProductInsightsElement [url=" + url + ", apiKey=" + apiKey + ", proxyUrl=" + proxyUrl + ", proxyUser=" + proxyUser
               + ", proxyPassword=" + proxyPassword + ", ssl=" + sslRef + "]";
    }
}
