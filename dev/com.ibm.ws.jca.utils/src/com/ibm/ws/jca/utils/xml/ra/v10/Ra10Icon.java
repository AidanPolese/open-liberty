/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jca.utils.xml.ra.v10;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 */
@XmlType(name = "IconType", propOrder = { "smallIcon", "largeIcon" })
public class Ra10Icon {

    @XmlElement(name = "small-icon")
    private String smallIcon;
    @XmlElement(name = "large-icon")
    private String largeIcon;

    /**
     * @return the smallIcon
     */
    public String getSmallIcon() {
        return smallIcon;
    }

    /**
     * @return the largeIcon
     */
    public String getLargeIcon() {
        return largeIcon;
    }

}
