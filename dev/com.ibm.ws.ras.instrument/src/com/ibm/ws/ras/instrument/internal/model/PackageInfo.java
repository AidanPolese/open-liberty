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

public class PackageInfo {

    private String packageName;
    private String internalPackageName;
    private boolean trivial;
    private TraceOptionsData traceOptionsData = new TraceOptionsData();

    public PackageInfo() {}

    public PackageInfo(String packageName, boolean trivial, TraceOptionsData traceOptionsData) {
        setPackageName(packageName);
        this.trivial = trivial;
        if (traceOptionsData != null) {
            this.traceOptionsData = traceOptionsData;
        }
    }

    public boolean isTrivial() {
        return trivial;
    }

    public void setTrivial(boolean trivial) {
        this.trivial = trivial;
    }

    public String getInternalPackageName() {
        return internalPackageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName.replaceAll("/", "\\.");
        this.internalPackageName = packageName.replaceAll("\\.", "/");
    }

    public TraceOptionsData getTraceOptionsData() {
        return traceOptionsData;
    }

    public void setTraceOptionsData(TraceOptionsData traceOptionsData) {
        this.traceOptionsData = traceOptionsData;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(";packageName=").append(packageName);
        sb.append(",trivial=").append(trivial);
        sb.append(",traceOptionsData=").append(traceOptionsData);
        return sb.toString();
    }
}
