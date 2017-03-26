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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

public class APIDiscoveryElement extends ConfigElement {
    private String apiName;
    private Integer maxSubscriptions;
    private String publicURL;
    private String swaggerDefinition;

    @XmlElement(name = "webModuleDoc")
    private ConfigElementList<WebModuleDocElement> webModuleDocs;

    public String getApiName() {
        return apiName;
    }

    @XmlAttribute(name = "apiName")
    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public Integer getMaxSubscriptions() {
        return maxSubscriptions;
    }

    @XmlAttribute(name = "maxSubscriptions")
    public void setMaxSubscriptions(Integer maxSubscriptions) {
        this.maxSubscriptions = maxSubscriptions;
    }

    public String getPublicURL() {
        return publicURL;
    }

    @XmlAttribute(name = "publicURL")
    public void setPublicURL(String publicURL) {
        this.publicURL = publicURL;
    }

    public String getSwaggerDefinition() {
        return swaggerDefinition;
    }

    @XmlAttribute(name = "swaggerDefinition")
    public void setSwaggerDefinition(String swaggerDefinition) {
        this.swaggerDefinition = swaggerDefinition;
    }

    public ConfigElementList<WebModuleDocElement> getWebModuleDocs() {
        if (this.webModuleDocs == null) {
            this.webModuleDocs = new ConfigElementList<WebModuleDocElement>();
        }
        return this.webModuleDocs;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("APIDiscoveryElement [");
        sb.append("apiName=").append(apiName);
        sb.append(", maxSubscriptions=").append(maxSubscriptions);
        sb.append(", publicURL=").append(publicURL);
        sb.append(", swaggerDefinition=").append(swaggerDefinition);
        sb.append(", webModuleDocs=[");
        if (webModuleDocs != null) {
            for (WebModuleDocElement webModuleDoc : webModuleDocs) {
                sb.append(webModuleDoc.toString()).append(", ");
            }
        }
        sb.append("]]");
        return sb.toString();
    }

    @XmlType(name = "WebModuleDoc")
    public static class WebModuleDocElement extends ConfigElement {
        private String contextRoot;
        private String docURL;
        private Boolean enabled;
        private Boolean isPublic;

        public String getContextRoot() {
            return contextRoot;
        }

        @XmlAttribute(name = "contextRoot")
        public void setContextRoot(String contextRoot) {
            this.contextRoot = contextRoot;
        }

        public String getDocURL() {
            return docURL;
        }

        @XmlAttribute(name = "docURL")
        public void setDocURL(String docURL) {
            this.docURL = docURL;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        @XmlAttribute(name = "enabled")
        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Boolean getIsPublic() {
            return isPublic;
        }

        @XmlAttribute(name = "public")
        public void setIsPublic(Boolean isPublic) {
            this.isPublic = isPublic;
        }

        @Override
        public String toString() {
            return "WebModuleDoc [contextRoot=" + contextRoot + ", docURL=" + docURL + ", enabled=" + enabled + ", public=" + isPublic + "]";
        }
    }
}
