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
package com.ibm.ws.artifact;

import java.io.File;
import java.io.IOException;

import com.ibm.wsspi.artifact.ArtifactEntry;

public interface ExtractableArtifactEntry extends ArtifactEntry {
    File extract() throws IOException;
}
