//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.taglib.config;

public class AvailabilityCondition {
    private AvailabilityConditionType type = null;
    private String value = null;
    
    public AvailabilityCondition(AvailabilityConditionType type, String value) {
        this.type = type;
        this.value = value;
    }
    
    public AvailabilityConditionType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

}
