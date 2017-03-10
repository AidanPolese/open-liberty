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
package com.ibm.ws.install;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.ibm.ws.install.internal.asset.ServerAsset;
import com.ibm.ws.install.internal.asset.ServerPackageAsset;
import com.ibm.ws.repository.connections.RepositoryConnectionList;
import com.ibm.ws.repository.connections.RestRepositoryConnectionProxy;

/**
 * This interface provides the APIs for the install clients to perform Liberty installation interactively.
 */
public interface InstallKernelInteractive {

    public void setUserAgent(String kernelUser);

    public void setRepositoryProperties(Properties repoProperties);

    public Set<String> getInstalledLicense();

    public void addListener(InstallEventListener listener, String notificationType);

    public void removeListener(InstallEventListener listener);

    public void resolve(Collection<String> assetIds, boolean download) throws InstallException;

    public void resolve(String feature, File esaFile, String toExtension) throws InstallException;

    public boolean resolveExistingAssetsFromDirectoryRepo(Collection<String> featureNames, File repoDir, boolean isOverwrite) throws InstallException;

    public void checkResources() throws InstallException;

    public Set<InstallLicense> getFeatureLicense(Locale locale) throws InstallException;

    public Collection<String> getSampleLicense(Locale locale) throws InstallException;

    public Collection<String> getSamplesOrOpenSources();

    public int getInstallResourcesSize();

    public int getLocalInstallAssetsSize();

    public int getPublicInstallResourcesSize();

    public int getPublicLocalInstallAssetsSize();

    public Map<String, Collection<String>> install(String toExtension, boolean rollbackAll, boolean downloadDependencies) throws InstallException;

    public RepositoryConnectionList getLoginInfo() throws InstallException;

    public void setLoginInfo(RepositoryConnectionList loginInfo);

    public void setProxy(RestRepositoryConnectionProxy proxy);

    public void setFirePublicAssetOnly(boolean firePublicAssetOnly);

    public Collection<String> getServerFeaturesToInstall(Set<ServerAsset> servers, boolean offlineOnly) throws InstallException, IOException;

    public ServerPackageAsset deployServerPackage(File archiveFile, String toExtension, boolean downloadDependencies) throws InstallException;

    public void checkAssetsNotInstalled(Collection<String> assetIds) throws InstallException;
}
