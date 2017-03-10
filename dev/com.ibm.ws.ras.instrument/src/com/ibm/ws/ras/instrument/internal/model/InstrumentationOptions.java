//------------------------------------------------------------------------------
// %Z% %I% %W% %G% %U% [%H% %T%]
//
// COMPONENT_NAME: WAS.sca.ras
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
//
// Defect/Feature  Date      CMVC ID   Description
// --------------  --------  --------- -----------------------------------------
// 410408          20070919  sykesm    Initial implementation
//------------------------------------------------------------------------------
package com.ibm.ws.ras.instrument.internal.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class InstrumentationOptions {

    private List<Pattern> packagesInclude = new ArrayList<Pattern>();
    private List<Pattern> packagesExclude = new ArrayList<Pattern>();
    private boolean addFFDC = false;
    private TraceType traceType = TraceType.TR;

    public InstrumentationOptions() {}

    public void addPackagesInclude(String regex) {
        Pattern pattern = Pattern.compile(regex);
        packagesInclude.add(pattern);
    }

    public void addPackagesExclude(String regex) {
        Pattern pattern = Pattern.compile(regex);
        packagesExclude.add(pattern);
    }

    public boolean isPackageIncluded(String internalPackageName) {
        if (packagesInclude.isEmpty() && packagesExclude.isEmpty()) {
            return !internalPackageName.startsWith("/java/lang");
        }
        String packageName = internalPackageName.replaceAll("/", "\\.");
        for (Pattern pi : packagesInclude) {
            if (pi.matcher(packageName).matches()) {
                for (Pattern pe : packagesExclude) {
                    if (pe.matcher(packageName).matches()) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean getAddFFDC() {
        return this.addFFDC;
    }

    public void setAddFFDC(boolean addFFDC) {
        this.addFFDC = addFFDC;
    }

    public void setTraceType(String traceType) {
        traceType = traceType == null ? "" : traceType;
        if (traceType.equalsIgnoreCase("jsr47") ||
                traceType.equalsIgnoreCase("java.util.logging") || traceType.equalsIgnoreCase("java_logging")) {
            this.traceType = TraceType.JAVA_LOGGING;
        } else if (traceType.equalsIgnoreCase("tr") || traceType.equalsIgnoreCase("websphere")) {
            this.traceType = TraceType.TR;
        } else if (traceType.equalsIgnoreCase("none")) {
            this.traceType = TraceType.NONE;
        } else {
            this.traceType = TraceType.JAVA_LOGGING;
        }
    }

    public TraceType getTraceType() {
        return this.traceType;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(";packagesInclude=").append(packagesInclude);
        sb.append(",packagesExclude=").append(packagesExclude);
        sb.append(",addFFDC=").append(addFFDC);
        sb.append(",traceType=").append(traceType);
        return sb.toString();
    }
}
