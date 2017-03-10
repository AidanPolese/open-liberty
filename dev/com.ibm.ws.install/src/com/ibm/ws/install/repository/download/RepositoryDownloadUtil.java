/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.install.repository.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.ws.install.InstallConstants;
import com.ibm.ws.install.InstallException;
import com.ibm.ws.install.InstallProgressEvent;
import com.ibm.ws.install.internal.EventManager;
import com.ibm.ws.install.internal.ExceptionUtils;
import com.ibm.ws.install.internal.InstallLogUtils;
import com.ibm.ws.install.internal.InstallLogUtils.Messages;
import com.ibm.ws.install.internal.InstallUtils;
import com.ibm.ws.install.internal.InstallUtils.InputStreamFileWriter;
import com.ibm.ws.kernel.productinfo.ProductInfo;
import com.ibm.ws.repository.common.enums.AttachmentType;
import com.ibm.ws.repository.common.enums.ResourceType;
import com.ibm.ws.repository.common.enums.Visibility;
import com.ibm.ws.repository.connections.ProductDefinition;
import com.ibm.ws.repository.connections.liberty.ProductInfoProductDefinition;
import com.ibm.ws.repository.exceptions.RepositoryBackendException;
import com.ibm.ws.repository.exceptions.RepositoryResourceException;
import com.ibm.ws.repository.resources.AttachmentResource;
import com.ibm.ws.repository.resources.EsaResource;
import com.ibm.ws.repository.resources.RepositoryResource;
import com.ibm.ws.repository.resources.SampleResource;

/**
 *
 */
public class RepositoryDownloadUtil {

    private static Logger logger = InstallLogUtils.getInstallLogger();

    public static String getProductVersion(RepositoryResource installResource) {
        String resourceVersion = null;
        try {
            Collection<ProductDefinition> pdList = new ArrayList<ProductDefinition>();
            for (ProductInfo pi : ProductInfo.getAllProductInfo().values()) {
                pdList.add(new ProductInfoProductDefinition(pi));
            }
            resourceVersion = installResource.getAppliesToVersions(pdList);
        } catch (Exception e) {
            logger.log(Level.FINEST, e.getMessage(), e);
        }

        if (resourceVersion == null)
            resourceVersion = InstallConstants.NOVERSION;

        return resourceVersion;
    }

    public static boolean isPublicAsset(ResourceType resourceType, RepositoryResource installResource) {
        if (resourceType.equals(ResourceType.FEATURE) || resourceType.equals(ResourceType.ADDON)) {
            EsaResource esar = ((EsaResource) installResource);
            if (esar.getVisibility().equals(Visibility.PUBLIC) || esar.getVisibility().equals(Visibility.INSTALL)) {
                return true;
            }

        } else if (resourceType.equals(ResourceType.PRODUCTSAMPLE) || resourceType.equals(ResourceType.OPENSOURCE)) {
            return true;
        }
        return false;
    }

    private static boolean isAddon(ResourceType resourceType, RepositoryResource installResource) {
        if (resourceType.equals(ResourceType.FEATURE) || resourceType.equals(ResourceType.ADDON)) {
            EsaResource esar = ((EsaResource) installResource);
            if (esar.getVisibility().equals(Visibility.PUBLIC) || esar.getVisibility().equals(Visibility.INSTALL)) {
                if (esar.getVisibility().equals(Visibility.INSTALL)) {
                    return true;
                }
            }

        }
        return false;
    }

    public static String getAssetNameFromMassiveResource(RepositoryResource mr) {
        String assetName = "";
        ResourceType resourceType = mr.getType();
        if (isAddon(resourceType, mr)) {
            resourceType = ResourceType.ADDON;
        }

        if (resourceType.equals(ResourceType.FEATURE) || resourceType.equals(ResourceType.ADDON)) {
            assetName = ((EsaResource) mr).getShortName() == null ? ((EsaResource) mr).getProvideFeature() : ((EsaResource) mr).getShortName();
        }
        else {
            SampleResource sr = ((SampleResource) mr);
            assetName = sr.getShortName() == null ? sr.getName() : sr.getShortName();
        }
        return assetName;
    }

    private static void writeResourcesToDiskRepo(Map<String, Collection<String>> downloaded, File toDir, RepositoryResource installResource, EventManager eventManager, int progress) throws RepositoryBackendException, RepositoryResourceException, IOException, InstallException {
        ResourceType resourceType = installResource.getType();
        if (isAddon(resourceType, installResource)) {
            resourceType = ResourceType.ADDON;
        }
        if (resourceType.equals(ResourceType.FEATURE) || resourceType.equals(ResourceType.ADDON) || resourceType.equals(ResourceType.PRODUCTSAMPLE) || resourceType.equals(ResourceType.OPENSOURCE)) {
            //get resource type 
            StringBuffer relativeUrl = new StringBuffer(); //

            String assetName = getAssetNameFromMassiveResource(installResource);
            String resourceTypeString = resourceType.getURLForType();
            String resourceVersion = getProductVersion(installResource);

            relativeUrl.append(resourceTypeString);
            relativeUrl.append(File.separator);
            relativeUrl.append(resourceVersion);
            relativeUrl.append(File.separator);

            String mainAttachmentName = null;
            File contentDir = new File(toDir, relativeUrl.toString());
            contentDir.mkdirs();
            for (AttachmentResource ar : installResource.getAttachments()) {
                if (ar.getType() == AttachmentType.CONTENT) {
                    mainAttachmentName = new File(ar.getName()).getName();
                    break;
                }
            }

            for (AttachmentResource ar : installResource.getAttachments()) {
                File resourceFile = null;
                switch (ar.getType()) {
                    case CONTENT:
                        resourceFile = new File(contentDir, mainAttachmentName);
                        if (isPublicAsset(resourceType, installResource)) {
                            try {
                                eventManager.fireProgressEvent(InstallProgressEvent.DOWNLOAD, progress,
                                                               Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("STATE_DOWNLOADING", assetName));
                            } catch (Exception e) {
                                logger.log(Level.FINEST, "writeResourcesToDiskRepo: failed to fire event", e);
                            }
                        }
                        break;
                    default:
                        resourceFile = null;
                        break;
                }
                if (resourceFile != null) {
                    if (resourceFile.exists()) {
                        resourceFile.delete();
                    }
                    if (!resourceFile.exists()) {
                        String url = ar.getURL();
                        logger.log(Level.FINEST,
                                   "Downloading " + ar.getName() + " from "
                                                   + (url.contains("public.dhe.ibm.com") ?
                                                                   "the IBM WebSphere Liberty Repository" :
                                                                   url + " of the repository " + installResource.getRepositoryConnection().getRepositoryLocation())
                                                   + " to " + contentDir.getAbsolutePath());
                        InputStreamFileWriter isfw = new InputStreamFileWriter(ar.getInputStream());

                        isfw.writeToFile(resourceFile);
                        logger.log(Level.FINEST,
                                   "Downloaded " + ar.getName() + " from "
                                                   + (url.contains("public.dhe.ibm.com") ?
                                                                   "the IBM WebSphere Liberty Repository" :
                                                                   url + " of the repository " + installResource.getRepositoryConnection().getRepositoryLocation())
                                                   + " to " + contentDir.getAbsolutePath());
                        InstallUtils.validateDownloaded(resourceFile, installResource);
                        if (ar.getType() == AttachmentType.CONTENT && isPublicAsset(resourceType, installResource))
                            if (!downloaded.containsKey(resourceTypeString)) {
                                Collection<String> typeResourcesList = new ArrayList<String>();
                                typeResourcesList.add(assetName);
                                downloaded.put(resourceTypeString, typeResourcesList);
                            } else {
                                downloaded.get(resourceTypeString).add(assetName);
                            }
                    }
                }
            }

            File json = new File(contentDir, mainAttachmentName + ".json");
            FileOutputStream os = new FileOutputStream(json);
            try {
                installResource.writeDiskRepoJSONToStream(os);
            } finally {
                os.close();
            }
            logger.log(Level.FINEST, "Downloaded " + mainAttachmentName + " to " + contentDir.getAbsolutePath());
        }
    }

    public static Map<String, Collection<String>> writeResourcesToDiskRepo(Map<String, Collection<String>> downloaded, File toDir,
                                                                           Map<String, List<List<RepositoryResource>>> installResources,
                                                                           String productVersion,
                                                                           EventManager eventManager, boolean defaultRepo) throws InstallException {

        int progress = 10;
        int interval1 = installResources.size() == 0 ? 90 : 90 / installResources.size();

        for (List<List<RepositoryResource>> targetList : installResources.values()) {
            for (List<RepositoryResource> mrList : targetList) {
                int interval2 = mrList.size() == 0 ? interval1 : interval1 / mrList.size();
                for (RepositoryResource installResource : mrList) {
                    try {
                        writeResourcesToDiskRepo(downloaded, toDir, installResource, eventManager, progress += interval2);
                    } catch (InstallException e) {
                        throw e;
                    } catch (Exception e) {
                        throw ExceptionUtils.createFailedToDownload(installResource, e, toDir);
                    }
                }
            }
        }
        return downloaded;
    }
}
