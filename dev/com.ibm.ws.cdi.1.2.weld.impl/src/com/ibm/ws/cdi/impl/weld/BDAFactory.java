/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi.impl.weld;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.ibm.ws.cdi.CDIException;
import com.ibm.ws.cdi.interfaces.ArchiveType;
import com.ibm.ws.cdi.interfaces.CDIArchive;
import com.ibm.ws.cdi.interfaces.CDIRuntime;
import com.ibm.ws.cdi.interfaces.ExtensionArchive;

/**
 * The implementation of Weld spi BeanDeploymentArchive to represent a CDI bean
 * archive.
 * 
 * 
 */
public class BDAFactory {

    /**
     * Need to cache runtime extension as they don't change per live server and the bda containing the same classes for every application
     */
    private static ConcurrentHashMap<String, Set<String>> runtimeBDAClasses = new ConcurrentHashMap<String, Set<String>>();

    public static WebSphereBeanDeploymentArchive createBDA(WebSphereCDIDeployment deployment,
                                                           String archiveID,
                                                           CDIArchive archive,
                                                           CDIRuntime cdiRuntime) throws CDIException {
        Set<String> additionalClasses = Collections.<String> emptySet();
        Set<String> additionalAnnotations = Collections.<String> emptySet();

        WebSphereBeanDeploymentArchive bda = createBDA(deployment,
                                                       archiveID,
                                                       archive,
                                                       cdiRuntime,
                                                       additionalClasses,
                                                       additionalAnnotations,
                                                       false,
                                                       false);
        return bda;
    }

    public static WebSphereBeanDeploymentArchive createBDA(WebSphereCDIDeployment deployment,
                                                           ExtensionArchive extensionArchive,
                                                           CDIRuntime cdiRuntime) throws CDIException {

        Set<String> additionalClasses = extensionArchive.getExtraClasses();
        Set<String> additionalAnnotations = extensionArchive.getExtraBeanDefiningAnnotations();
        boolean extensionCanSeeApplicationBDAs = extensionArchive.applicationBDAsVisible();

        boolean extClassesOnlyBDA = extensionArchive.isExtClassesOnly();
        String archiveID = deployment.getDeploymentID() + "#" + extensionArchive.getName() + ".additionalClasses";

        WebSphereBeanDeploymentArchive bda = createBDA(deployment,
                                                       archiveID,
                                                       extensionArchive,
                                                       cdiRuntime,
                                                       additionalClasses,
                                                       additionalAnnotations,
                                                       extensionCanSeeApplicationBDAs,
                                                       extClassesOnlyBDA);
        return bda;
    }

    public static WebSphereBeanDeploymentArchive createBDA(WebSphereCDIDeployment deployment, OnDemandArchive onDemandArchive, CDIRuntime cdiRuntime) throws CDIException {

        String archiveID = deployment.getDeploymentID() + "#" + onDemandArchive.getName() + ".additionalClasses";
        return createBDA(deployment, archiveID, onDemandArchive, cdiRuntime);
    }

    private static WebSphereBeanDeploymentArchive createBDA(WebSphereCDIDeployment deployment,
                                                            String archiveID,
                                                            CDIArchive archive,
                                                            CDIRuntime cdiRuntime,
                                                            Set<String> additionalClasses,
                                                            Set<String> additionalBeanDefiningAnnotations,
                                                            boolean extensionCanSeeApplicationBDAs,
                                                            boolean extensionClassesOnlyBDA) throws CDIException {
        Set<String> extensionClassNames = archive.getExtensionClasses();
        Set<String> allClassNames = new HashSet<String>();
        if (archive.getType() == ArchiveType.RUNTIME_EXTENSION) {
            if (extensionClassesOnlyBDA) {
                // no need to scan as we just create a bda with the extension classes
                allClassNames.addAll(extensionClassNames);
            } else {
                // trying to see whether we have cached the classes before. If not, scan the bundle
                String key = archive.getName();
                Set<String> clazzes = runtimeBDAClasses.get(key);
                if (clazzes == null) {
                    clazzes = archive.getClassNames();
                    Set<String> result = runtimeBDAClasses.putIfAbsent(key, clazzes);
                    if (result == null) {
                        result = clazzes;
                    }
                    allClassNames.addAll(result);
                } else {
                    allClassNames.addAll(clazzes);
                }
            }
        } else {
            allClassNames.addAll(archive.getClassNames());

        }

        return new BeanDeploymentArchiveImpl(deployment, archiveID, archive, cdiRuntime, allClassNames, additionalClasses, additionalBeanDefiningAnnotations, extensionCanSeeApplicationBDAs, extensionClassNames);
    }
}
