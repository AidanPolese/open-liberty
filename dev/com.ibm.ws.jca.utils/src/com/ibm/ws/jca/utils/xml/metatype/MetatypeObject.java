/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jca.utils.xml.metatype;

import javax.xml.bind.annotation.XmlAttribute;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.jca.utils.Utils;

/**
 * Metatype Object
 */
@Trivial
public class MetatypeObject {
    private String ocdref;
    private MetatypeOcd matchingOcd;

    public void setMatchingOcd(MetatypeOcd ocd) {
        this.matchingOcd = ocd;
    }

    public MetatypeOcd getMatchingOcd() {
        return matchingOcd;
    }

    @XmlAttribute(name = "ocdref")
    public void setOcdref(String ocdref) {
        this.ocdref = ocdref;
    }

    public String getOcdref() {
        return this.ocdref;
    }

    @Override
    public String toString() {
        return "MetatypeObject{ocdRef='" + ocdref + "'}";
    }

    public String toMetatypeString(int padSpaces) {
        String buffer = Utils.getSpaceBufferString(padSpaces);
        StringBuilder sb = new StringBuilder(buffer).append("<Object ");

        sb.append("ocdref=\"").append(ocdref).append("\" ");

        sb.append("/>");
        return sb.toString();
    }
}
