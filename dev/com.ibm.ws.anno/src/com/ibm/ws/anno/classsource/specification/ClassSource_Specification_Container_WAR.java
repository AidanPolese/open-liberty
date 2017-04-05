/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corporation 2011,2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.ws.anno.classsource.specification;

import java.util.List;
import java.util.Set;

import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.anno.classsource.ClassSource_Exception;

public interface ClassSource_Specification_Container_WAR
                extends ClassSource_Specification_Container {

    // When set, use this as the classes location; otherwise, default to
    // a location relative to the immediate location.

    String getWARClassesContainerName();

    Container getWARClassesContainer();

    void setWARClassesContainerData(String name, Container warClassesContainer);

    ContainerData getWARClassesContainerData();

    void setWARClassesContainerData(ContainerData warClassesContainerData);

    ContainerData getResolvedWARClassesContainerData() throws ClassSource_Exception;

    // Three cases:
    //
    // No WAR library container, no WAR library containers:
    //   Select from the location relative to the immediate location.
    // Set WAR library container:
    //   Select from the specified container.
    // Set WAR library containers:
    //   Use the specified containers.

    String getWARLibraryContainerName();

    Container getWARLibraryContainer();

    void setWARLibraryContainerData(String name, Container warLibraryContainer);

    void setWARLibraryContainerData(ContainerData warLibraryContainerData);

    ContainerData getWARLibraryContainerData();

    ContainerData getResolvedWARLibraryContainerData() throws ClassSource_Exception;

    //

    boolean getUseWARLibrariesContainerData();

    void setUseWARLibrariesContainerData(boolean useWARLibrariesContainerData);

    List<ContainerData> getWARLibrariesContainerData();

    ContainerData addWARLibrariesContainerData(String name, Container warLibraryContainer);

    void addWARLibrariesContainerData(ContainerData warLibrariesContainerData);

    void addWARLibrariesContainerData(List<ContainerData> warLibrariesContainerData);

    // Optional selection of a subset of the WAR libraries.
    // This is necessary for distinguishing the non-metadata complete fragments.
    // Metadata-complete fragments are not seed locations.

    Set<ContainerData> getWARIncludedContainerData();

    void addWARIncludedContainerData(ContainerData warLibrariesIncludedContainerData);

    void addWARIncludedContainerData(Set<ContainerData> warLibrariesIncludedContainerData);
}
