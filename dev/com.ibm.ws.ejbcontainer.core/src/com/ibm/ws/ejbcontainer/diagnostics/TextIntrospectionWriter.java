/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.diagnostics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class TextIntrospectionWriter implements IntrospectionWriter {
    protected static final String HEADING_0 = "----";
    protected static final String HEADING_1 = "****";
    private static final String INDENT = "   ";

    final List<String> titles = new ArrayList<String>();
    boolean previousEnded;
    boolean currentContent;
    final StringBuilder indent = new StringBuilder();

    private void flushNewline(boolean force) {
        if (previousEnded || force) {
            writeln("");
            previousEnded = false;
        }
    }

    @Override
    public final void begin(String title) {
        flushNewline(currentContent);
        currentContent = false;

        if (title != null) {
            if (titles.isEmpty()) {
                String line = HEADING_0 + ' ' + title + ' ' + HEADING_0;
                writeln(line);

                char[] separatorline = new char[line.length()];
                Arrays.fill(separatorline, '-');
                writeln(new String(separatorline));
            } else {
                String marker = titles.size() == 1 ? HEADING_0 : HEADING_1;
                writeln(indent + marker + ' ' + title + ' ' + marker);

                indent.append(INDENT);
            }
        }

        titles.add(title);
    }

    @Override
    public final void end() {
        String title = titles.remove(titles.size() - 1);
        if (title != null) {
            if (indent.length() > 0) {
                indent.setLength(indent.length() - INDENT.length());
            }

            if (titles.isEmpty()) {
                writeln(indent + HEADING_0 + " " + title + " End " + HEADING_0);
            }
        }

        previousEnded = true;
    }

    @Override
    public final void println(String line) {
        flushNewline(false);
        writeln(indent + line);
        currentContent = true;
    }

    @Override
    public final void dump(String[] dumpData) {
        for (String line : dumpData) {
            println(line);
        }
    }

    protected abstract void writeln(String s);
}
