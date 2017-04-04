/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.ws.anno.classsource.specification;

import java.util.List;

public interface ClassSource_Specification_Direct extends ClassSource_Specification {
    // Not always needed; the WAR will disregard this location
    // if both the classes location and the WAR libraries are specified.

    String getImmediatePath();

    void setImmediatePath(String immediatePath);

    // Two cases:
    //
    // The library path is set:
    //   Select from the specified path.
    // The library path is not set:
    //   Use the specified paths.

    String getApplicationLibraryPath();

    void setApplicationLibraryPath(String applicationLibraryPath);

    List<String> getApplicationLibraryJarPaths();

    void addApplicationLibraryJarPath(String applicationLibraryJarPath);

    void addApplicationLibraryJarPaths(List<String> applicationLibraryJarPaths);

    // These are always specified as a list.  (Generally, the locations
    // are resolved relative to the container of the parent application.)

    List<String> getManifestJarPaths();

    void addManifestJarPath(String manifestJarPath);

    void addManifestJarPaths(List<String> manifestJarPaths);
}
