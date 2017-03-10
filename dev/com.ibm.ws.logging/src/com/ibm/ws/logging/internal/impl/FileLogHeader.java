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
package com.ibm.ws.logging.internal.impl;

import java.io.PrintStream;

import com.ibm.websphere.ras.TrConfigurator;

public class FileLogHeader {
    private final String header;
    private final boolean javaLangInstrument;
    private final boolean trace;

    public FileLogHeader(String header, boolean trace, boolean javaLangInstrument) {
        this.header = header;
        this.trace = trace;
        this.javaLangInstrument = javaLangInstrument;
    }

    public void print(PrintStream ps) {
        ps.println(BaseTraceFormatter.banner);

        ps.print(header);

        if (trace) {
            ps.println("trace.specification = " + TrConfigurator.getEffectiveTraceSpec());

            if (!javaLangInstrument) {
                ps.println("java.lang.instrument = " + javaLangInstrument);
            }
        }

        ps.println(BaseTraceFormatter.banner);
    }
}
