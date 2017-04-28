/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.websphere.simplicity.config;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

public class OpenAPIElement extends ConfigElement {

    private String publicURL;
    private String customization;
    private Boolean enablePrivateURL;

    @XmlElement(name = "webModuleDoc")
    private ConfigElementList<WebModuleDocElement> webModuleDocs;

    public String getPublicURL() {
        return publicURL;
    }

    @XmlAttribute(name = "publicURL")
    public void setPublicURL(String publicURL) {
        this.publicURL = publicURL;
    }

    public String getCustomization() {
        return customization;
    }

    @XmlAttribute(name = "customization")
    public void setCustomization(String customization) {
        this.customization = customization;
    }

    public Boolean getEnablePrivateURL() {
        return enablePrivateURL;
    }

    @XmlAttribute(name = "enablePrivateURL")
    public void setEnablePrivateURL(Boolean enablePrivateURL) {
        this.enablePrivateURL = enablePrivateURL;
    }

    public ConfigElementList<WebModuleDocElement> getWebModuleDocs() {
        if (this.webModuleDocs == null) {
            this.webModuleDocs = new ConfigElementList<WebModuleDocElement>();
        }
        return this.webModuleDocs;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("OpenAPIElement [");
        sb.append("publicURL=").append(publicURL);
        sb.append(", customization=").append(customization);
        sb.append(", enablePrivateURL=").append(enablePrivateURL);
        sb.append(", webModuleDocs=[");
        if (webModuleDocs != null) {
            for (WebModuleDocElement webModuleDoc : webModuleDocs) {
                sb.append(webModuleDoc.toString()).append(", ");
            }
        }
        sb.append("]]");
        return sb.toString();
    }

    @XmlType(name = "OpenAPIWebModuleDoc")
    public static class WebModuleDocElement extends ConfigElement {

        private String contextRoot;
        private Boolean enabled;
        private Boolean isPublic;

        public String getContextRoot() {
            return contextRoot;
        }

        @XmlAttribute(name = "contextRoot")
        public void setContextRoot(String contextRoot) {
            this.contextRoot = contextRoot;
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
            return "WebModuleDoc [contextRoot=" + contextRoot + ", enabled=" + enabled + ", public=" + isPublic + "]";
        }
    }
}
