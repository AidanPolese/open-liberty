/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package wlp.lib.extract;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 *
 */
public interface ExtractProgress {
    public void setFilesToExtract(int count);

    public void skippedFile();

    public void extractedFile(String f);

    public void commandsToRun(int count);

    public void commandRun(List args);

    public boolean isCanceled();

    public void downloadingFile(URL sourceUrl, File targetFile);

    public void dataDownloaded(int numBytes);
}
