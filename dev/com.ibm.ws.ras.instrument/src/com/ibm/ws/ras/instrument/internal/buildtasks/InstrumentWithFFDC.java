/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package com.ibm.ws.ras.instrument.internal.buildtasks;

import org.apache.tools.ant.types.Commandline;

import com.ibm.ws.ras.instrument.internal.main.AbstractInstrumentation;
import com.ibm.ws.ras.instrument.internal.main.StaticTraceInstrumentation;

public class InstrumentWithFFDC extends AbstractInstrumentationTask {

    protected boolean ffdc = true;

    @Override
    protected Commandline getCommandline() {
        Commandline cmdl = super.getCommandline();
        if (ffdc) {
            cmdl.createArgument().setValue("--ffdc");
        }
        cmdl.createArgument().setValue("--none");
        return cmdl;
    }

    /**
     * Indicate whether or not the task should instrument classes with FFDC.
     * FFDC is enabled by default.
     */
    public void setFfdc(boolean ffdc) {
        this.ffdc = ffdc;
    }

    @Override
    protected AbstractInstrumentation createInstrumentation() {
        return new StaticTraceInstrumentation();
    }

}
