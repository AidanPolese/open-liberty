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
package com.ibm.ws.kernel.boot.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.ibm.ws.kernel.boot.Debug;

/**
 * Determine if the server process is running based on whether or not the output
 * redirection still holds the file share lock on to console.log.
 */
public class FileShareLockProcessStatusImpl implements ProcessStatus {
    private final File file;

    public FileShareLockProcessStatusImpl(File file) {
        this.file = file;
    }

    @Override
    public boolean isPossiblyRunning() {
        if (file.exists()) {
            try {
                new FileOutputStream(file, true).close();
            } catch (FileNotFoundException e) {
                // "java.io.FileNotFoundException: C:\...\logs\console.log
                // (The process cannot access the file because it is being used
                // by another process.)"
                return true;
            } catch (IOException e) {
                Debug.printStackTrace(e);
            }
        }

        return false;
    }
}
