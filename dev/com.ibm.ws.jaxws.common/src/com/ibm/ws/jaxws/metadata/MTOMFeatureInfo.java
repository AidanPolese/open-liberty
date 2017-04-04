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

import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOM;
import javax.xml.ws.soap.MTOMFeature;

public class MTOMFeatureInfo implements WebServiceFeatureInfo {

    private static final long serialVersionUID = -2551460185663043236L;

    private boolean enabled;

    private int threshold;

    public MTOMFeatureInfo() {
        enabled = true;
        threshold = 0;
    }

    public MTOMFeatureInfo(MTOM mtom) {
        this(mtom.enabled(), mtom.threshold());
    }

    public MTOMFeatureInfo(boolean enabled, int threshold) {
        this.enabled = enabled;
        this.threshold = threshold;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public WebServiceFeature getWebServiceFeature() {
        return new MTOMFeature(enabled, threshold);
    }

    @Override
    public String toString() {
        return "MTOMFeatureInfo [enabled=" + enabled + ", threshold=" + threshold + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (enabled ? 1231 : 1237);
        result = prime * result + threshold;
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
        MTOMFeatureInfo other = (MTOMFeatureInfo) obj;
        if (enabled != other.enabled)
            return false;
        if (threshold != other.threshold)
            return false;
        return true;
    }

}
