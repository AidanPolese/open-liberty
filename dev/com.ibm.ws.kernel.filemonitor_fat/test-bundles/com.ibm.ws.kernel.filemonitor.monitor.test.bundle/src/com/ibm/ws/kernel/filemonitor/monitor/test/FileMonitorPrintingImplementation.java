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
package com.ibm.ws.kernel.filemonitor.monitor.test;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;

import com.ibm.wsspi.kernel.filemonitor.FileMonitor;

public class FileMonitorPrintingImplementation implements FileMonitor {
    // Not thread-safe, so keep as an instance variable
    private final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss:SSS");

    private static final String EOR = "<eor>";
    private final String eyecatcher;

    public FileMonitorPrintingImplementation(String eyecatcher) {
        this.eyecatcher = eyecatcher;
    }

    @Override
    public void onBaseline(Collection<File> baseline) {
        System.out.println(timestamp() + "onBaseline" + toString(baseline) + EOR);

    }

    @Override
    public void onChange(Collection<File> createdFiles,
                         Collection<File> modifiedFiles, Collection<File> deletedFiles) {
        System.out.println(timestamp() + "onChange" + toString(createdFiles) + toString(modifiedFiles) + toString(deletedFiles) + EOR);
    }

    private String toString(Collection<File> files) {
        return Arrays.toString(files.toArray());
    }

    /**
     * @return
     */
    private String timestamp() {
        return DATE_FORMAT.format(System.currentTimeMillis()) + eyecatcher;
    }

}
