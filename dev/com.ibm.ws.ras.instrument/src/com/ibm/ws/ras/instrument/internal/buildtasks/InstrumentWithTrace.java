//-------------------------------------------------------------------------------
//%Z% %I% %W% %G% %U% [%H% %T%]

//COMPONENT_NAME: WAS.sca.ras

//IBM Confidential OCO Source Material
//5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2006, 2007
//The source code for this program is not published or otherwise
//divested of its trade secrets, irrespective of what has been
//deposited with the U.S. Copyright Office.

//Change History:

//Defect/Feature  Date      CMVC ID   Description
//--------------  --------  --------  --------------------------
//395481         20060929  sykesm    Initial implementation
//401246         20061027  sykesm    Fix compiler warning
//444684         20070607  sykesm    Fix support for single file
//-------------------------------------------------------------------------------
package com.ibm.ws.ras.instrument.internal.buildtasks;

import org.apache.tools.ant.types.Commandline;

/**
 * Build task to instrument classes and jars with trace.
 */
public class InstrumentWithTrace extends InstrumentWithFFDC {

    protected String traceType = null;

    /**
     * InstrumentWithTrace task constructor.
     */
    public InstrumentWithTrace() {}

    /**
     * Set the style of trace to use when instrumenting.
     */
    public void setTraceType(String traceType) {
        this.traceType = traceType;
    }

    protected Commandline getCommandline() {
        Commandline cmdl = super.getCommandline();
        if (traceType != null) {
            cmdl.createArgument().setValue("--" + traceType);
        }
        return cmdl;
    }
}
