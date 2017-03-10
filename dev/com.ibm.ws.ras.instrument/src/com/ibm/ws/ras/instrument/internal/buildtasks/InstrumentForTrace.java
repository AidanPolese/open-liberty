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

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Commandline;

import com.ibm.ws.ras.instrument.internal.main.AbstractInstrumentation;
import com.ibm.ws.ras.instrument.internal.main.AlpineTracePreprocessInstrumentation;

public class InstrumentForTrace extends AbstractInstrumentationTask {

    /**
     * Should FFDC be injected as well
     */
    boolean ffdc = false;

    /**
     * Should trace be injected by the task rather than just pre-processed.
     */
    boolean taskInjection = false;

    /**
     * The type of trace to inject.
     */
    String api = "alpine";

    /**
     * Indicate whether or not the task should instrument classes with FFDC.
     * FFDC is enabled by default.
     */
    public void setFfdc(boolean ffdc) {
        this.ffdc = ffdc;
    }

    /**
     * Indicate whether or not the task should perform static trace injection
     * at task execution rather than at runtime.
     */
    public void setTaskInjection(boolean taskInjection) {
        this.taskInjection = taskInjection;
    }

    /**
     * Set the type of trace API to use.
     * 
     * @param api
     *            the type of trace interface to use
     */
    public void setApi(String api) {
        api = api.trim();
        if ("alpine".equalsIgnoreCase(api)) {
            this.api = "alpine";
        } else if ("websphere".equalsIgnoreCase(api)) {
            this.api = "tr";
        } else if ("tr".equalsIgnoreCase(api)) {
            this.api = "tr";
        } else if ("jsr47".equalsIgnoreCase(api)) {
            this.api = "java-logging";
        } else if ("java".equalsIgnoreCase(api)) {
            this.api = "java-logging";
        } else if ("java.logging".equalsIgnoreCase(api)) {
            this.api = "java-logging";
        } else if ("java-logging".equalsIgnoreCase(api)) {
            this.api = "java-logging";
        } else {
            log("Invalid trace type " + api, Project.MSG_ERR);
        }
    }

    @Override
    protected Commandline getCommandline() {
        Commandline cmdl = super.getCommandline();
        if (ffdc) {
            cmdl.createArgument().setValue("--ffdc");
        }
        if (taskInjection) {
            cmdl.createArgument().setValue("--static");
        }
        if (api != null) {
            cmdl.createArgument().setValue("--" + api);
        }
        return cmdl;
    }

    @Override
    protected AbstractInstrumentation createInstrumentation() {
        return new AlpineTracePreprocessInstrumentation();
    }

}
