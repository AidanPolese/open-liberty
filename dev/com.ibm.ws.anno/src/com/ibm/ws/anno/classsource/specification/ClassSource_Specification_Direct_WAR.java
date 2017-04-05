/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011,2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.ws.anno.classsource.specification;

import java.util.List;
import java.util.Set;

public interface ClassSource_Specification_Direct_WAR extends ClassSource_Specification_Direct {
    // When set, use this as the classes location; otherwise, default to
    // a location relative to the immediate location.

    String getWARClassesPath();

    void setWARClassesPath(String warClassesPath);

    // Three cases:
    //
    // No WAR library path, no WAR library paths:
    //   Select from the location relative to the immediate location.
    // Set WAR library path:
    //   Select from the specified path.
    // Set WAR library paths:
    //   Use the specified paths.

    String getWARLibraryPath();

    void setWARLibraryPath(String warLibraryPath);

    boolean getUseWARLibraryJarPaths();

    void setUseWARLibraryJarPaths(boolean useWARLibraryJarPaths);

    List<String> getWARLibraryJarPaths();

    void addWARLibraryJarPath(String warLibraryJarPath);

    void addWARLibraryJarPaths(List<String> warLibraryJarPaths);

    // Optional selection of a subset of the WAR libraries.
    // This is necessary for distinguishing the non-metadata complete fragments.
    // Metadata-complete fragments are not seed locations.

    Set<String> getWARIncludedJarPaths();

    void addWARIncludedJarPath(String includedJarPath);

    void addWARIncludedJarPaths(Set<String> includedJarPaths);
}
