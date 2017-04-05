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
package com.ibm.ws.jaxws.metadata;

import javax.xml.ws.RespectBinding;
import javax.xml.ws.RespectBindingFeature;
import javax.xml.ws.WebServiceFeature;

public class RespectBindingFeatureInfo implements WebServiceFeatureInfo {

    private static final long serialVersionUID = -1840143187268640173L;

    private boolean enabled;

    public RespectBindingFeatureInfo() {
        enabled = true;
    }

    public RespectBindingFeatureInfo(RespectBinding respectBinding) {
        this(respectBinding.enabled());
    }

    public RespectBindingFeatureInfo(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public WebServiceFeature getWebServiceFeature() {
        return new RespectBindingFeature(enabled);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "RespectBindingFeatureInfo [enabled=" + enabled + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (enabled ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RespectBindingFeatureInfo other = (RespectBindingFeatureInfo) obj;
        if (enabled != other.enabled)
            return false;
        return true;
    }

}
