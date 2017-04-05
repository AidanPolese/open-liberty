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

package com.ibm.ws.monitor.internal.command;

import java.util.List;

public abstract class MonitorCommandProvider {

    public interface MessageStream {
        public void print(Object o);

        public void println(Object o);
    }

    protected String getCommandName() {
        return "monitor";
    }

    protected String getDescription() {
        return "---Monitor Command Description---";
    }

    protected String getSyntaxInformation() {
        StringBuilder sb = new StringBuilder();
        sb.append("\tmonitor commands ...\n");
        sb.append("monitor add probes <class name> ...\n");
        sb.append("monitor add meter <probe name> <metric name>\n");
        return sb.toString();
    }

    protected void executeCommand(List<String> args, MessageStream outputStream, MessageStream errorStream) {
        outputStream.println("Args were: " + args);
    }

    public void addProbe(String probeSpec) {}

    public void removeProbe(String probeSpec) {}
}
