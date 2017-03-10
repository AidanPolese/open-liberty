//------------------------------------------------------------------------------
//%Z% %I% %W% %G% %U% [%H% %T%]

//COMPONENT_NAME: WAS.sca.ras

//IBM Confidential OCO Source Material
//5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2007, 2013
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.

//Change History:

//Defect/Feature  Date      CMVC ID   Description
//--------------  --------  --------- -----------------------------------------
//410408          20070919  sykesm    Initial implementation
//------------------------------------------------------------------------------
package com.ibm.ws.ras.instrument.internal.model;

import java.util.ArrayList;
import java.util.List;

public class TraceOptionsData {

    private final List<String> traceGroups = new ArrayList<String>();
    private String messageBundle;
    private boolean traceExceptionThrow;
    private boolean traceExceptionHandling;

    public TraceOptionsData() {
        super();
    }

    public TraceOptionsData(List<String> traceGroups, String messageBundle, boolean traceExceptionThrow, boolean traceExceptionHandling) {
        for (String traceGroup : traceGroups) {
            addTraceGroup(traceGroup);
        }
        setMessageBundle(messageBundle);
        this.traceExceptionThrow = traceExceptionThrow;
        this.traceExceptionHandling = traceExceptionHandling;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object != null && object.getClass() == TraceOptionsData.class) {
            TraceOptionsData traceOptions = (TraceOptionsData) object;
            return traceGroups.equals(traceOptions.traceGroups) &&
                   (messageBundle == null ? traceOptions.messageBundle == null : messageBundle.equals(traceOptions.messageBundle)) &&
                   traceExceptionThrow == traceOptions.traceExceptionThrow &&
                   traceExceptionHandling == traceOptions.traceExceptionHandling;
        }

        return false;
    }

    public List<String> getTraceGroups() {
        return traceGroups;
    }

    public void addTraceGroup(String traceGroup) {
        if (traceGroup != null && !traceGroup.equals("") && !traceGroups.contains(traceGroup)) {
            traceGroups.add(traceGroup);
        }
    }

    public boolean isTraceExceptionHandling() {
        return traceExceptionHandling;
    }

    public void setTraceExceptionHandling(boolean traceExceptionHandling) {
        this.traceExceptionHandling = traceExceptionHandling;
    }

    public boolean isTraceExceptionThrow() {
        return traceExceptionThrow;
    }

    public void setTraceExceptionThrow(boolean traceExceptionThrow) {
        this.traceExceptionThrow = traceExceptionThrow;
    }

    public void setMessageBundle(String messageBundle) {
        if (messageBundle != null && !messageBundle.equals("")) {
            this.messageBundle = messageBundle;
        }
    }

    public String getMessageBundle() {
        return this.messageBundle;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(";traceGroups=").append(traceGroups);
        sb.append(",messageBundle=").append(messageBundle);
        sb.append(",traceExceptionThrow=").append(traceExceptionThrow);
        sb.append(",traceExceptionHandling=").append(traceExceptionHandling);
        return sb.toString();
    }
}
