/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013, 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.install.internal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;

import com.ibm.ws.install.CancelException;
import com.ibm.ws.install.InstallConstants;
import com.ibm.ws.install.InstallConstants.AssetType;
import com.ibm.ws.install.InstallConstants.DownloadOption;
import com.ibm.ws.install.InstallConstants.ExistsAction;
import com.ibm.ws.install.InstallEventListener;
import com.ibm.ws.install.InstallException;
import com.ibm.ws.install.InstallLicense;
import com.ibm.ws.install.InstallProgressEvent;
import com.ibm.ws.install.InstalledFeature;
import com.ibm.ws.install.InstalledFeatureCollection;
import com.ibm.ws.install.ReapplyFixException;
import com.ibm.ws.install.internal.InstallLogUtils.Messages;
import com.ibm.ws.install.internal.asset.ESAAsset;
import com.ibm.ws.install.internal.asset.FixAsset;
import com.ibm.ws.install.internal.asset.InstallAsset;
import com.ibm.ws.install.internal.asset.InstalledAssetImpl;
import com.ibm.ws.install.internal.asset.JarAsset;
import com.ibm.ws.install.internal.asset.OpenSourceAsset;
import com.ibm.ws.install.internal.asset.SampleAsset;
import com.ibm.ws.install.internal.asset.ServerAsset;
import com.ibm.ws.install.internal.asset.ServerPackageAsset;
import com.ibm.ws.install.internal.asset.ServerPackageJarAsset;
import com.ibm.ws.install.internal.asset.ServerPackageZipAsset;
import com.ibm.ws.install.internal.cmdline.ExeInstallAction;
import com.ibm.ws.install.internal.platform.InstallPlatformUtils;
import com.ibm.ws.install.repository.download.RepositoryDownloadUtil;
import com.ibm.ws.kernel.boot.cmdline.Utils;
import com.ibm.ws.kernel.feature.internal.cmdline.FeatureToolException;
import com.ibm.ws.kernel.feature.provisioning.ProvisioningFeatureDefinition;
import com.ibm.ws.kernel.productinfo.DuplicateProductInfoException;
import com.ibm.ws.kernel.productinfo.ProductInfo;
import com.ibm.ws.kernel.productinfo.ProductInfoParseException;
import com.ibm.ws.kernel.productinfo.ProductInfoReplaceException;
import com.ibm.ws.kernel.provisioning.BundleRepositoryRegistry;
import com.ibm.ws.product.utility.extension.ValidateCommandTask;
import com.ibm.ws.repository.common.enums.ResourceType;
import com.ibm.ws.repository.common.enums.Visibility;
import com.ibm.ws.repository.connections.ProductDefinition;
import com.ibm.ws.repository.connections.RepositoryConnectionList;
import com.ibm.ws.repository.connections.RestRepositoryConnectionProxy;
import com.ibm.ws.repository.connections.liberty.ProductInfoProductDefinition;
import com.ibm.ws.repository.exceptions.RepositoryException;
import com.ibm.ws.repository.resources.AttachmentResource;
import com.ibm.ws.repository.resources.EsaResource;
import com.ibm.ws.repository.resources.IfixResource;
import com.ibm.ws.repository.resources.RepositoryResource;
import com.ibm.ws.repository.resources.SampleResource;

import wlp.lib.extract.LicenseProvider;
import wlp.lib.extract.SelfExtractUtils;
import wlp.lib.extract.SelfExtractor;

public class Director extends AbstractDirector {

    private List<List<InstallAsset>> installAssets;
    private final Engine engine;
    private boolean setScriptsPermission;
    private boolean firePublicAssetOnly = true;

    private ResolveDirector resolveDirector;
    private UninstallDirector uninstallDirector;

    public Director(File installRoot) {
        super(new Product(installRoot), new EventManager(), InstallLogUtils.getInstallLogger());
        this.engine = new Engine(this.product);
        this.setScriptsPermission = false;
        this.logger.setUseParentHandlers(false);
        this.logger.setLevel(Level.FINEST);
    }

    public Director() {
        this(null);
    }

    /**
     * Set the repositoryUrl of the market place
     *
     * @param repositoryUrl the repositoryUrl to set
     */
    public void setRepositoryUrl(String repositoryUrl) {
        getResolveDirector().setRepositoryUrl(repositoryUrl);
    }

    public void setRepositoryProperties(Properties repoProperties) {
        getResolveDirector().setRepositoryProperties(repoProperties);
    }

    public void setLoginInfo(RepositoryConnectionList repositoryConnectionList) {
        getResolveDirector().setRepositoryConnectionList(repositoryConnectionList);
    }

    public void addListener(InstallEventListener listener, String notificationType) {
        eventManager.addListener(listener, notificationType);
    }

    public void removeListener(InstallEventListener listener) {
        eventManager.removeListener(listener);
    }

    private void fireProgressEvent(int state, int progress, String messageKey, RepositoryResource installResource) throws InstallException {
        String resourceName = null;
        if (installResource instanceof EsaResource) {
            EsaResource esar = ((EsaResource) installResource);
            if (esar.getVisibility().equals(Visibility.PUBLIC) || esar.getVisibility().equals(Visibility.INSTALL)) {
                resourceName = (esar.getShortName() == null) ? installResource.getName() : esar.getShortName();
            } else if (!firePublicAssetOnly && messageKey.equals("STATE_DOWNLOADING")) {
                messageKey = "STATE_DOWNLOADING_DEPENDENCY";
                resourceName = "";
            } else {
                return;
            }
        }
        if (installResource instanceof SampleResource) {
            SampleResource sr = ((SampleResource) installResource);
            resourceName = sr.getShortName() == null ? installResource.getName() : sr.getShortName();
        } else {
            resourceName = installResource.getName();
        }
        fireProgressEvent(state, progress,
                          Messages.INSTALL_KERNEL_MESSAGES.getLogMessage(messageKey, resourceName));
    }

    public void fireInstallProgressEvent(int progress, InstallAsset installAsset) throws InstallException {
        if (installAsset.isServerPackage())
            fireProgressEvent(InstallProgressEvent.DEPLOY, progress, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("STATE_DEPLOYING", installAsset.toString()), true);
        else if (installAsset.isFeature()) {
            ESAAsset esaa = ((ESAAsset) installAsset);
            if (esaa.isPublic()) {
                String resourceName = (esaa.getShortName() == null) ? esaa.getDisplayName() : esaa.getShortName();
                fireProgressEvent(InstallProgressEvent.INSTALL, progress, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("STATE_INSTALLING", resourceName), true);
            } else if (!firePublicAssetOnly) {
                fireProgressEvent(InstallProgressEvent.INSTALL, progress, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("STATE_INSTALLING_DEPENDENCY"), true);
            }
        } else {
            fireProgressEvent(InstallProgressEvent.INSTALL, progress, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("STATE_INSTALLING", installAsset.toString()), true);
        }
    }

    public void fireDownloadProgressEvent(int progress, RepositoryResource installResource) throws InstallException {
        if (installResource.getType().equals(ResourceType.FEATURE)) {
            Visibility v = ((EsaResource) installResource).getVisibility();
            if (v.equals(Visibility.PUBLIC) || v.equals(Visibility.INSTALL)) {
                EsaResource esar = (EsaResource) installResource;
                String resourceName = (esar.getShortName() == null) ? installResource.getName() : esar.getShortName();
                fireProgressEvent(InstallProgressEvent.DOWNLOAD, progress, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("STATE_DOWNLOADING", resourceName), true);
            } else if (!firePublicAssetOnly) {
                fireProgressEvent(InstallProgressEvent.DOWNLOAD, progress, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("STATE_DOWNLOADING_DEPENDENCY"), true);
            }
        } else if (installResource.getType().equals(ResourceType.PRODUCTSAMPLE) ||
                   installResource.getType().equals(ResourceType.OPENSOURCE)) {
            fireProgressEvent(InstallProgressEvent.DOWNLOAD, progress, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("STATE_DOWNLOADING", installResource.getName()), true);
        }
    }

    private Collection<String> getFeaturesToBeInstalled() {
        HashSet<String> features = new HashSet<String>();
        for (List<InstallAsset> iaList : installAssets) {
            for (InstallAsset installAsset : iaList) {
                if (installAsset.isFeature())
                    features.add(((ESAAsset) installAsset).getProvideFeature());
            }
        }
        return features;
    }

    public void installFeatures(Collection<String> featureNames, String toExtension, boolean acceptLicense, String userId, String password) throws InstallException {
        installFeatures(featureNames, toExtension, acceptLicense, userId, password, 1);
    }

    public void installFeatures(Collection<String> featureNames, String toExtension, boolean acceptLicense, String userId, String password,
                                int checkProgress) throws InstallException {
        installFeatures(featureNames, toExtension, acceptLicense, false, userId, password, checkProgress);
    }

    public void installFeatures(Collection<String> featureNames, String toExtension, boolean acceptLicense, boolean allowAlreadyInstalled, String userId, String password,
                                int checkProgress) throws InstallException {
        fireProgressEvent(InstallProgressEvent.CHECK, checkProgress, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("STATE_CHECKING"));

        if (featureNames == null || featureNames.isEmpty()) {
            throw ExceptionUtils.createByKey("ERROR_FEATURES_LIST_INVALID");
        }

        List<List<RepositoryResource>> installResources = getResolveDirector().resolve(featureNames, DownloadOption.required, userId, password);
        if (isEmpty(installResources)) {
            if (allowAlreadyInstalled)
                return;
            throw ExceptionUtils.createByKey(InstallException.ALREADY_EXISTS, "ALREADY_INSTALLED",
                                             InstallUtils.getShortNames(product.getFeatureDefinitions(), featureNames).toString());
        }

        if (!acceptLicense) {
            throw ExceptionUtils.createByKey("ERROR_LICENSES_NOT_ACCEPTED");
        }

        this.installAssets = new ArrayList<List<InstallAsset>>(installResources.size());
        int progress = 10;
        int interval1 = installResources.size() == 0 ? 40 : 40 / installResources.size();
        for (List<RepositoryResource> mrList : installResources) {
            List<InstallAsset> iaList = new ArrayList<InstallAsset>(mrList.size());
            installAssets.add(iaList);
            int interval2 = mrList.size() == 0 ? interval1 : interval1 / mrList.size();
            for (RepositoryResource installResource : mrList) {
                fireDownloadProgressEvent(progress, installResource);
                progress += interval2;
                File d = null;
                try {
                    d = InstallUtils.download(this.product.getInstallTempDir(), installResource);
                    if (d != null && installResource.getType().equals(ResourceType.FEATURE)) {
                        Visibility v = ((EsaResource) installResource).getVisibility();
                        if (v.equals(Visibility.PUBLIC) || v.equals(Visibility.INSTALL)) {
                            log(Level.FINE, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("MSG_DOWNLOAD_SUCCESS", installResource.getName()));
                        }
                    }
                    log(Level.FINEST, d == null ? installResource.getName() + " is an unsupported type " + installResource.getType()
                                                  + " to be downloaded." : "Downloaded " + installResource.getName() + " to " + d.getAbsolutePath());
                } catch (InstallException e) {
                    throw e;
                } catch (Exception e) {
                    throw ExceptionUtils.createFailedToDownload(installResource, e, (d == null ? this.product.getInstallTempDir() : d));
                }
                if (installResource.getType().equals(ResourceType.FEATURE)) {
                    EsaResource esa = (EsaResource) installResource;
                    ESAAsset esaAsset;
                    try {
                        esaAsset = new ESAAsset(esa.getName(), esa.getProvideFeature(), toExtension, d, true);
                        if (esaAsset.getSubsystemEntry() == null) {
                            throw ExceptionUtils.create(Messages.PROVISIONER_MESSAGES.getLogMessage("tool.install.content.no.subsystem.manifest"),
                                                        InstallException.BAD_FEATURE_DEFINITION);
                        }
                        ProvisioningFeatureDefinition fd = esaAsset.getProvisioningFeatureDefinition();
                        if (!fd.isSupportedFeatureVersion()) {
                            throw ExceptionUtils.create(Messages.PROVISIONER_MESSAGES.getLogMessage("UNSUPPORTED_FEATURE_VERSION", fd.getFeatureName(),
                                                                                                    fd.getIbmFeatureVersion()),
                                                        InstallException.BAD_FEATURE_DEFINITION);
                        }
                        iaList.add(esaAsset);
                    } catch (Exception e) {
                        throw ExceptionUtils.createByKey(e, "ERROR_INVALID_ESA", esa.getName());
                    }
                } else if (installResource.getType().equals(ResourceType.IFIX)) {
                    try {
                        iaList.add(new FixAsset(installResource.getName(), d, true));
                    } catch (Exception e) {
                        throw ExceptionUtils.createByKey(e, "ERROR_INVALID_IFIX", installResource.getName());
                    }
                }
            }
        }
    }

    public void installFeature(String esaLocation, String toExtension, boolean acceptLicense) throws InstallException {
        fireProgressEvent(InstallProgressEvent.CHECK, 1, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("STATE_CHECKING"));
        ArrayList<InstallAsset> installAssets = new ArrayList<InstallAsset>();
        String feature = getResolveDirector().resolve(esaLocation, toExtension, product.getInstalledFeatures(), installAssets, 10, 40);
        if (installAssets.isEmpty()) {
            throw ExceptionUtils.create(Messages.PROVISIONER_MESSAGES.getLogMessage("tool.feature.exists", feature), InstallException.ALREADY_EXISTS);
        }
        this.installAssets = new ArrayList<List<InstallAsset>>(1);
        this.installAssets.add(installAssets);
    }

    public void installFeature(Collection<String> featureIds, File fromDir, String toExtension, boolean acceptLicense, boolean offlineOnly) throws InstallException {
        //fireProgressEvent(InstallProgressEvent.CHECK, 1, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("STATE_CHECKING"));
        this.installAssets = new ArrayList<List<InstallAsset>>();
        ArrayList<InstallAsset> installAssets = new ArrayList<InstallAsset>();
        ArrayList<String> unresolvedFeatures = new ArrayList<String>();
        Collection<ESAAsset> autoFeatures = getResolveDirector().getAutoFeature(fromDir, toExtension);
        getResolveDirector().resolve(featureIds, fromDir, toExtension, offlineOnly, installAssets, unresolvedFeatures);
        if (!offlineOnly && !unresolvedFeatures.isEmpty()) {
            log(Level.FINEST, "installFeature() determined unresolved features: " + unresolvedFeatures.toString() + " from " + fromDir.getAbsolutePath());
            installFeatures(unresolvedFeatures, toExtension, acceptLicense, null, null, 5);
        }
        if (!installAssets.isEmpty()) {
            getResolveDirector().resolveAutoFeatures(autoFeatures, installAssets);
            this.installAssets.add(installAssets);
        }
        if (this.installAssets.isEmpty()) {
            throw ExceptionUtils.createByKey(InstallException.ALREADY_EXISTS, "ALREADY_INSTALLED", featureIds.toString());
        }
    }

    public ServerPackageAsset resolveServerPackage(File archiveFile) throws InstallException {
        ServerPackageAsset spa = (ServerPackageJarAsset.validType(archiveFile.getAbsolutePath().toLowerCase())) ? new ServerPackageJarAsset(archiveFile, false) : new ServerPackageZipAsset(archiveFile, false);

        this.installAssets = new ArrayList<List<InstallAsset>>();
        this.installAssets.add(Arrays.asList((InstallAsset) spa));

        return spa;
    }

    public Collection<String> getServerFeaturesToInstall(Set<ServerAsset> servers, boolean offlineOnly) throws InstallException, IOException {
        Set<String> features = new TreeSet<String>();
        Set<String> serverNames = new HashSet<String>(servers.size());
        Set<String> allServerNames = new HashSet<String>(servers.size());

        for (ServerAsset sa : servers) {
            Collection<String> requiredFeatures = sa.getRequiredFeatures();

            if (!requiredFeatures.isEmpty()) {
                logger.log(Level.FINEST, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("LOG_DEPLOY_SERVER_FEATURES",
                                                                                        sa.getServerName(),
                                                                                        InstallUtils.getFeatureListOutput(requiredFeatures)));
                features.addAll(requiredFeatures);
                serverNames.add(sa.getServerName());

            }

            allServerNames.add(sa.getServerName());
        }

        Collection<String> featuresToInstall = getFeaturesToInstall(features, offlineOnly);

        if (!featuresToInstall.isEmpty()) {
            logger.log(Level.FINE, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("LOG_DEPLOY_ADDITIONAL_FEATURES_REQUIRED",
                                                                                  serverNames, featuresToInstall));
        } else {
            logger.log(Level.FINE, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("LOG_DEPLOY_NO_ADDITIONAL_FEATURES_REQUIRED",
                                                                                  allServerNames));
        }

        return featuresToInstall;
    }

    /**
     * Set Director to install the specified fixes including the following tasks
     * - determine to install the fix or not
     * - call Resolver to resolve the required fixes
     * - call Massive Client to download the assets
     *
     * @param strings
     */
    public void installFixes(Collection<String> fixes, String userId, String password) throws InstallException {
        fireProgressEvent(InstallProgressEvent.CHECK, 1, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("STATE_CHECKING"));
        List<String> fixesToInstall = new ArrayList<String>(fixes.size());
        for (String fix : fixes) {
            fixesToInstall.add(fix);
        }

        RepositoryConnectionList loginInfo = getResolveDirector().getRepositoryConnectionList(null, userId, password, this.getClass().getCanonicalName() + ".installFixes");
        Resolver resolver = new Resolver(loginInfo);
        List<IfixResource> ifixResources = resolver.resolveFixResources(fixesToInstall);

        if (ifixResources.isEmpty())
            throw ExceptionUtils.createByKey("ERROR_FAILED_TO_RESOLVE_IFIX", InstallUtils.getFeatureListOutput(fixes));

        ArrayList<InstallAsset> installAssets = new ArrayList<InstallAsset>();
        int progress = 10;
        int interval = 40 / ifixResources.size();
        for (IfixResource fix : ifixResources) {
            fireProgressEvent(InstallProgressEvent.DOWNLOAD, progress, "STATE_DOWNLOADING", fix);
            progress += interval;
            File d;
            try {
                d = InstallUtils.download(this.product.getInstallTempDir(), fix);
            } catch (InstallException e) {
                throw e;
            } catch (Exception e) {
                throw ExceptionUtils.createByKey(e, "ERROR_FAILED_TO_DOWNLOAD_IFIX", fix.getName(), this.product.getInstallTempDir().getAbsolutePath());
            }
            try {
                installAssets.add(new FixAsset(fix.getName(), d, true));
            } catch (Exception e) {
                throw ExceptionUtils.createByKey(e, "ERROR_INVALID_IFIX", fix.getName());
            }
        }
        this.installAssets = new ArrayList<List<InstallAsset>>(1);
        this.installAssets.add(installAssets);
    }

    public Set<InstallLicense> getFeatureLicense(Collection<String> featureIds, File fromDir, String toExtension, boolean offlineOnly, Locale locale) throws InstallException {
        Set<InstallLicense> licenses = new HashSet<InstallLicense>();
        ArrayList<InstallAsset> installAssets = new ArrayList<InstallAsset>();
        ArrayList<String> unresolvedFeatures = new ArrayList<String>();
        Collection<ESAAsset> autoFeatures = getResolveDirector().getAutoFeature(fromDir, toExtension);
        getResolveDirector().resolve(featureIds, fromDir, toExtension, offlineOnly, installAssets, unresolvedFeatures);
        if (!offlineOnly && !unresolvedFeatures.isEmpty()) {
            log(Level.FINEST, "getFeatureLicense() determined unresolved features: " + unresolvedFeatures.toString() + " from " + fromDir.getAbsolutePath());
            licenses = getFeatureLicense(unresolvedFeatures, locale, null, null);
        }
        if (installAssets.isEmpty()) {
            return licenses;
        }
        getResolveDirector().resolveAutoFeatures(autoFeatures, installAssets);
        Map<String, InstallLicenseImpl> licenseIds = new HashMap<String, InstallLicenseImpl>();
        for (InstallAsset installAsset : installAssets) {
            if (installAsset.isFeature()) {
                ESAAsset esa = (ESAAsset) installAsset;
                if (esa.isPublic()) {
                    ExeInstallAction.incrementNumOfLocalFeatures();
                }
                LicenseProvider lp = esa.getLicenseProvider(locale);
                String licenseId = esa.getLicenseId();
                if (licenseId != null && !licenseId.isEmpty()) {
                    InstallLicenseImpl ili = licenseIds.get(licenseId);
                    if (ili == null) {
                        ili = new InstallLicenseImpl(licenseId, null, lp);
                        licenseIds.put(licenseId, ili);
                    }
                    ili.addFeature(esa.getProvideFeature());
                }
            }
        }
        licenses.addAll(licenseIds.values());
        return licenses;
    }

    public Set<InstallLicense> getFeatureLicense(String esaLocation, Locale locale) throws InstallException {
        Set<InstallLicense> licenses = new HashSet<InstallLicense>();
        ArrayList<InstallAsset> installAssets = new ArrayList<InstallAsset>();
        getResolveDirector().resolve(esaLocation, "", product.getInstalledFeatures(), installAssets, 10, 40);
        if (installAssets.isEmpty()) {
            return licenses;
        }

        Map<String, InstallLicenseImpl> licenseIds = new HashMap<String, InstallLicenseImpl>();
        for (InstallAsset installAsset : installAssets) {
            if (installAsset.isFeature()) {
                ESAAsset esa = (ESAAsset) installAsset;
                LicenseProvider lp = esa.getLicenseProvider(locale);
                String licenseId = esa.getLicenseId();
                if (licenseId != null && !licenseId.isEmpty()) {
                    InstallLicenseImpl ili = licenseIds.get(licenseId);
                    if (ili == null) {
                        ili = new InstallLicenseImpl(licenseId, null, lp);
                        licenseIds.put(licenseId, ili);
                    }
                    ili.addFeature(esa.getProvideFeature());
                }
            }
        }
        licenses.addAll(licenseIds.values());
        return licenses;
    }

    public Set<InstallLicense> getFeatureLicense(Collection<String> featureNames, Locale locale, String userId, String password) throws InstallException {
        Set<InstallLicense> licenses = new HashSet<InstallLicense>();
        if (featureNames == null || featureNames.isEmpty())
            return licenses;

        Map<String, List<List<RepositoryResource>>> installResources = getResolveDirector().resolveMap(featureNames, DownloadOption.required, userId, password);
        if (isEmpty(installResources)) {
            this.installAssets = new ArrayList<List<InstallAsset>>(0);
            return licenses;
        }

        return getFeatureLicense(locale, installResources);
    }

    private Set<InstallLicense> getFeatureLicense(Locale locale, Map<String, List<List<RepositoryResource>>> installResources) throws InstallException {
        Set<InstallLicense> licenses = new HashSet<InstallLicense>();
        Map<String, InstallLicenseImpl> licenseIds = new HashMap<String, InstallLicenseImpl>();
        getFeatureLicenseFromInstallResources(locale, licenseIds, installResources);
        licenses.addAll(licenseIds.values());
        return licenses;

    }

    private void getFeatureLicenseFromInstallResources(Locale locale, Map<String, InstallLicenseImpl> licenseIds,
                                                       Map<String, List<List<RepositoryResource>>> installResources) throws InstallException {
        if (installResources == null)
            return;
        for (List<List<RepositoryResource>> targetList : installResources.values()) {
            for (List<RepositoryResource> mrList : targetList) {
                for (RepositoryResource installResource : mrList) {
                    if (installResource.getType().equals(ResourceType.FEATURE)) {
                        try {
                            Visibility v = ((EsaResource) installResource).getVisibility();
                            if (v.equals(Visibility.PUBLIC) || v.equals(Visibility.INSTALL)) {
                                ExeInstallAction.incrementNumOfRemoteFeatures();
                            }
                            AttachmentResource la = installResource.getLicenseAgreement(locale);
                            AttachmentResource li = installResource.getLicenseInformation(locale);
                            AttachmentResource enLi = locale.getLanguage().equalsIgnoreCase("en") ? null : installResource.getLicenseInformation(Locale.ENGLISH);
                            String licenseId = installResource.getLicenseId();
                            if (licenseId != null && !licenseId.isEmpty()) {
                                InstallLicenseImpl ili = licenseIds.get(licenseId);
                                if (ili == null) {
                                    ili = new InstallLicenseImpl(licenseId, installResource.getLicenseType(), la, li, enLi);
                                    licenseIds.put(licenseId, ili);
                                }
                                ili.addFeature(((EsaResource) installResource).getProvideFeature());
                            }
                        } catch (RepositoryException e) {
                            throw ExceptionUtils.createByKey(e, "ERROR_FAILED_TO_GET_FEATURE_LICENSE", installResource.getName());
                        }
                    }
                }
            }
        }
    }

    public Set<InstallLicense> getServerPackageFeatureLicense(File archive, boolean offlineOnly, Locale locale) throws InstallException {
        String aName = archive.getAbsolutePath().toLowerCase();
        ServerPackageAsset spa = null;
        if (ServerPackageZipAsset.validType(aName)) {
            spa = new ServerPackageZipAsset(archive, false);
        } else if (ServerPackageJarAsset.validType(aName)) {
            spa = new ServerPackageJarAsset(archive, false);
        } else {
            return new HashSet<InstallLicense>();
        }

        return getFeatureLicense(spa.getRequiredFeatures(), locale, null, null);
    }

    public Set<InstallLicense> getServerFeatureLicense(File serverXML, boolean offlineOnly, Locale locale) throws InstallException, IOException {
        if (null != serverXML) {
            return getFeatureLicense(new ServerAsset(serverXML).getRequiredFeatures(), locale, null, null);
        }

        return new HashSet<InstallLicense>();
    }

    public Set<String> getInstalledLicense() {
        return product.getAcceptedLicenses();
    }

    public List<String> getExtensionNames() {
        return product.getExtensionNames();
    }

    private void checkSetScriptsPermission(List<File> filesInstalled) {
        if (setScriptsPermission)
            return;
        setScriptsPermission = containScript(filesInstalled);
    }

    private void download(int progress, InstallAsset installAsset) throws InstallException {
        RepositoryResource rr = installAsset.getRepositoryResource();
        if (rr != null) {
            installAsset.download(product.getInstallTempDir());
            fireDownloadProgressEvent(progress, rr);
        }
    }

    /**
     * Perform the installation of the determined install assets.
     *
     * @throws IOException
     */
    public void install(ExistsAction existsAction, boolean rollbackAll, boolean downloadDependencies) throws InstallException {
        if (installAssets.isEmpty())
            return;
        int progress = 50;
        int interval1 = installAssets.size() == 0 ? 40 : 40 / installAssets.size();
        List<File> filesInstalled = new ArrayList<File>();
        ChecksumsManager checksumsManager = new ChecksumsManager();
        for (List<InstallAsset> iaList : installAssets) {
            int interval2 = iaList.size() == 0 ? interval1 : interval1 / (iaList.size() * 2);
            if (!rollbackAll)
                filesInstalled = new ArrayList<File>();
            Set<String> executableFiles = new HashSet<String>();
            Map<String, Set<String>> extattrFilesMap = new HashMap<String, Set<String>>();
            for (InstallAsset installAsset : iaList) {
                progress += interval2;
                try {
                    download(progress, installAsset);
                } catch (InstallException e) {
                    InstallUtils.delete(filesInstalled);
                    installAsset.cleanup();
                    throw e;
                }
                try {
                    fireInstallProgressEvent(progress, installAsset);
                } catch (CancelException e) {
                    InstallUtils.delete(filesInstalled);
                    installAsset.cleanup();
                    throw e;
                }
                progress += interval2;
                try {
                    engine.install(installAsset, filesInstalled, getFeaturesToBeInstalled(), existsAction, executableFiles, extattrFilesMap, downloadDependencies,
                                   getResolveDirector().getProxy(),
                                   checksumsManager);
                    if (installAsset.isFeature() || installAsset.isAddon()) {
                        ESAAsset esaa = ((ESAAsset) installAsset);
                        if (esaa.isPublic()) {
                            log(Level.FINE, installAsset.installedLogMsg());
                        }
                    } else {
                        log(Level.FINE, installAsset.installedLogMsg());
                    }
                } catch (InstallException e) {
                    log(Level.SEVERE, e.getMessage(), e);
                    throw e;
                } catch (IOException e) {
                    throw ExceptionUtils.create(e);
                } finally {
                    installAsset.cleanup();
                }
            }

            try {
                InstallPlatformUtils.setExecutePermissionAccordingToUmask(executableFiles.toArray(new String[executableFiles.size()]));
            } catch (Exception e) {
                log(Level.WARNING, e.getMessage());
                if (null != e.getCause()) {
                    log(Level.SEVERE, null, e);
                }

                log(Level.WARNING, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("ERROR_UNABLE_TO_SET_EXECUTE_PERMISSIONS", executableFiles.toString()));
            }

            try {
                InstallPlatformUtils.setExtendedAttributes(extattrFilesMap);
            } catch (Exception e) {
                log(Level.WARNING, e.getMessage());
                if (null != e.getCause()) {
                    log(Level.SEVERE, null, e);
                }

                for (Map.Entry<String, Set<String>> entry : extattrFilesMap.entrySet()) {
                    String attr = entry.getKey();

                    log(Level.WARNING, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("ERROR_UNABLE_TO_SET_EXT_ATTR", attr, entry.getValue().toString()));
                }
            }
            checkSetScriptsPermission(filesInstalled);
        }
        checksumsManager.updateChecksums();
    }

    /**
     * Clean up the downloaded install assets;
     * reset installAssets and uninstallAssets.
     */
    public void cleanUp() {
        fireProgressEvent(InstallProgressEvent.CLEAN_UP, 98, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("STATE_CLEANING"));
        if (installAssets != null) {
            for (List<InstallAsset> iaList : installAssets) {
                for (InstallAsset asset : iaList) {
                    asset.delete();
                }
            }
        }
        boolean del = InstallUtils.deleteDirectory(this.product.getInstallTempDir());
        if (!del)
            this.product.getInstallTempDir().deleteOnExit();
        installAssets = null;
        setScriptsPermission = false;
        if (resolveDirector != null)
            resolveDirector.cleanUp();
        if (uninstallDirector != null)
            uninstallDirector.cleanUp();
        log(Level.FINE, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("MSG_CLEANUP_SUCCESS"));
    }

    public Collection<String> getInstalledFeatureNames() {
        if (installAssets == null)
            return null;
        Collection<String> installed = new ArrayList<String>();
        for (List<InstallAsset> iaList : installAssets) {
            for (InstallAsset asset : iaList) {
                if (asset.isFeature()) {
                    ESAAsset esa = (ESAAsset) asset;
                    if (esa.isPublic()) {
                        String esaName = esa.getShortName();
                        if (esaName == null || esaName.isEmpty())
                            esaName = esa.getFeatureName();
                        installed.add(esaName);
                    }
                }
            }
        }
        return installed;
    }

    public Set<String> getInstalledFeatures(String installedBy) {
        return product.getInstalledFeatures(installedBy);
    }

    public void refresh() {
        this.product.refresh();
    }

    public void enableConsoleLog(Level level, boolean verbose) {
        InstallLogUtils.enableConsoleLogging(level, verbose);
        InstallLogUtils.enableConsoleErrorLogging(verbose);
    }

    public void reapplyFixIfNeeded() throws ReapplyFixException {
        BundleRepositoryRegistry.initializeDefaults(null, false);
        Set<String> fixesToReapply = ValidateCommandTask.getFixesToReapply(product.getManifestFileProcessor(), new InstallUtils.InstallCommandConsole());
        try {
            fireProgressEvent(InstallProgressEvent.CHECK, 90, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("STATE_VALIDATING_FIXES", fixesToReapply.toString()));
            this.enableEvent = false;
            if (fixesToReapply.isEmpty()) {
                log(Level.FINEST, "No fix is required to be reapplied.");
            } else {
                installFixes(fixesToReapply, null, null);
                install(ExistsAction.replace, false, false);
                log(Level.FINEST, "Successfully reapplied the following fixes: " + fixesToReapply.toString());
            }
        } catch (InstallException e) {
            log(Level.FINEST, "Failed to reapply the following fixes: " + fixesToReapply.toString());
            throw new ReapplyFixException(Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("LOG_REINSTALL_FIXES_WARNING",
                                                                                         fixesToReapply.toString()).trim(), e, InstallException.RUNTIME_EXCEPTION);
        } finally {
            this.enableEvent = true;
        }
    }

    public void setScriptsPermission(int event) {
        if (event == InstallProgressEvent.POST_INSTALL ? setScriptsPermission : getUninstallDirector().needToSetScriptsPermission()) {
            fireProgressEvent(event, 95, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("STATE_SET_SCRIPTS_PERMISSION"));
            try {
                SelfExtractUtils.fixScriptPermissions(new SelfExtractor.NullExtractProgress(), product.getInstallDir(), null);
            } catch (Exception e) {

            }
        }
    }

    public void setUserAgent(String kernelUser) {
        getResolveDirector().setUserAgent(kernelUser);
    }

    public List<EsaResource> queryFeatures(String searchStr) throws InstallException {
        List<EsaResource> features = new ArrayList<EsaResource>();
        RepositoryConnectionList loginInfo = getResolveDirector().getRepositoryConnectionList(null, null, null, this.getClass().getCanonicalName() + ".queryFeatures");
        try {
            for (ProductInfo productInfo : ProductInfo.getAllProductInfo().values()) {
                log(Level.FINE, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("MSG_SEARCHING_FEATURES"));
                features.addAll(loginInfo.findMatchingEsas(searchStr, new ProductInfoProductDefinition(productInfo), Visibility.PUBLIC));
                log(Level.FINE, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("MSG_SEARCHING_ADDONS"));
                features.addAll(loginInfo.findMatchingEsas(searchStr, new ProductInfoProductDefinition(productInfo), Visibility.INSTALL));
            }
            log(Level.FINE, " ");
        } catch (ProductInfoParseException pipe) {
            throw ExceptionUtils.create(pipe);
        } catch (DuplicateProductInfoException dpie) {
            throw ExceptionUtils.create(dpie);
        } catch (ProductInfoReplaceException pire) {
            throw ExceptionUtils.create(pire);
        } catch (RepositoryException re) {
            throw ExceptionUtils.create(re, re.getCause(), getResolveDirector().getProxy(), getResolveDirector().defaultRepo());
        }
        return features;
    }

    public Map<ResourceType, List<RepositoryResource>> queryAssets(String searchStr, AssetType type) throws InstallException {
        Map<ResourceType, List<RepositoryResource>> results = new HashMap<ResourceType, List<RepositoryResource>>();
        List<RepositoryResource> addOns = new ArrayList<RepositoryResource>();
        List<RepositoryResource> features = new ArrayList<RepositoryResource>();
        List<RepositoryResource> samples = new ArrayList<RepositoryResource>();
        List<RepositoryResource> openSources = new ArrayList<RepositoryResource>();
        RepositoryConnectionList loginInfo = getResolveDirector().getRepositoryConnectionList(null, null, null, this.getClass().getCanonicalName() + ".queryAssets");
        try {
            Collection<ProductDefinition> pdList = new ArrayList<ProductDefinition>();
            for (ProductInfo pi : ProductInfo.getAllProductInfo().values()) {
                pdList.add(new ProductInfoProductDefinition(pi));
            }

            if (type.equals(AssetType.all) || type.equals(AssetType.addon)) {
                log(Level.FINE, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("MSG_SEARCHING_ADDONS"));
                List<ResourceType> tList = new ArrayList<ResourceType>(1);
                tList.add(ResourceType.FEATURE);
                addOns.addAll(loginInfo.findResources(searchStr, pdList, tList, Visibility.INSTALL));
            }

            if (type.equals(AssetType.all) || type.equals(AssetType.feature)) {
                log(Level.FINE, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("MSG_SEARCHING_FEATURES"));
                List<ResourceType> tList = new ArrayList<ResourceType>(1);
                tList.add(ResourceType.FEATURE);
                features.addAll(loginInfo.findResources(searchStr, pdList, tList, Visibility.PUBLIC));
            }

            if (type.equals(AssetType.all) || type.equals(AssetType.sample)) {
                log(Level.FINE, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("MSG_SEARCHING_SAMPLES"));
                List<ResourceType> tList = new ArrayList<ResourceType>(1);
                tList.add(ResourceType.PRODUCTSAMPLE);
                samples.addAll(loginInfo.findResources(searchStr, pdList, tList, Visibility.PUBLIC));
            }

            if (type.equals(AssetType.all) || type.equals(AssetType.opensource)) {
                log(Level.FINE, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("MSG_SEARCHING_OPENSOURCE"));
                List<ResourceType> tList = new ArrayList<ResourceType>(1);
                tList.add(ResourceType.OPENSOURCE);
                openSources.addAll(loginInfo.findResources(searchStr, pdList, tList, Visibility.PUBLIC));
            }

            log(Level.FINE, "");
        } catch (ProductInfoParseException pipe) {
            throw ExceptionUtils.create(pipe);
        } catch (DuplicateProductInfoException dpie) {
            throw ExceptionUtils.create(dpie);
        } catch (ProductInfoReplaceException pire) {
            throw ExceptionUtils.create(pire);
        } catch (RepositoryException re) {
            throw ExceptionUtils.create(re, re.getCause(), getResolveDirector().getProxy(), getResolveDirector().defaultRepo());
        }
        if (!addOns.isEmpty())
            results.put(ResourceType.ADDON, addOns);
        if (!features.isEmpty())
            results.put(ResourceType.FEATURE, features);
        if (!samples.isEmpty())
            results.put(ResourceType.PRODUCTSAMPLE, samples);
        if (!openSources.isEmpty())
            results.put(ResourceType.OPENSOURCE, openSources);
        return results;
    }

    public Collection<String> downloadFeatureFeatureManager(Set<String> featureNames, File toDir, DownloadOption downloadOption, ExistsAction action, String user,
                                                            String password) throws InstallException {
        fireProgressEvent(InstallProgressEvent.CHECK, 1, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("STATE_CHECKING"));

        if (featureNames == null || featureNames.isEmpty()) {
            throw ExceptionUtils.createByKey("ERROR_FEATURES_LIST_INVALID");
        }

        List<List<RepositoryResource>> installResources = getResolveDirector().resolve(featureNames, downloadOption, user, password);
        if (isEmpty(installResources)) {
            if (downloadOption == DownloadOption.required)
                throw ExceptionUtils.createByKey(InstallException.ALREADY_EXISTS, "ERROR_DOWNLOAD_ALREADY_INSTALLED",
                                                 InstallUtils.getShortNames(product.getFeatureDefinitions(), featureNames).toString());
            else
                throw ExceptionUtils.createByKey("ERROR_FAILED_TO_RESOLVE_FEATURES", InstallUtils.getFeatureListOutput(featureNames));
        }

        Collection<String> downloaded = new ArrayList<String>(installResources.size());
        int progress = 10;
        int interval1 = installResources.size() == 0 ? 90 : 90 / installResources.size();
        for (List<RepositoryResource> mrList : installResources) {
            int interval2 = mrList.size() == 0 ? interval1 : interval1 / mrList.size();
            for (RepositoryResource installResource : mrList) {
                fireDownloadProgressEvent(progress, installResource);
                progress += interval2;
                if (installResource.getType().equals(ResourceType.FEATURE)) {
                    String name = ((EsaResource) installResource).getProvideFeature();
                    File targetFile = new File(toDir, name + ".esa");
                    if (targetFile.exists()) {
                        if (action == ExistsAction.ignore) {
                            log(Level.FINEST, "Existing file " + name + ".esa" + " is found in the directory " + toDir.getAbsolutePath()
                                              + ", the feature is skipped for download.");
                        } else if (action == ExistsAction.replace) {
                            if (targetFile.delete()) {
                                try {
                                    File d = InstallUtils.download(installResource, toDir);
                                    downloaded.add(d.getAbsolutePath());
                                    log(Level.FINEST, "Downloaded " + installResource.getName() + " to " + d.getAbsolutePath());
                                } catch (InstallException e) {
                                    throw e;
                                } catch (Exception e) {
                                    throw ExceptionUtils.createByKey(InstallException.IO_FAILURE, e, "ERROR_FAILED_TO_DOWNLOAD_FEATURE",
                                                                     installResource.getName(), toDir.getAbsolutePath());
                                }
                            } else
                                throw ExceptionUtils.createByKey(InstallException.IO_FAILURE, "ERROR_FAILED_TO_DOWNLOAD_FEATURE",
                                                                 name, targetFile.getAbsolutePath());
                        } else {
                            throw new InstallException(Messages.PROVISIONER_MESSAGES.getLogMessage("tool.install.file.exists", targetFile), InstallException.IO_FAILURE);
                        }
                    } else {
                        try {
                            File d = InstallUtils.download(installResource, toDir);
                            downloaded.add(d.getAbsolutePath());
                            log(Level.FINEST, "Downloaded " + installResource.getName() + " to " + d.getAbsolutePath());
                        } catch (InstallException e) {
                            throw e;
                        } catch (Exception e) {
                            throw ExceptionUtils.createByKey(InstallException.IO_FAILURE, e, "ERROR_FAILED_TO_DOWNLOAD_FEATURE",
                                                             installResource.getName(), toDir.getAbsolutePath());
                        }
                    }

                } else {
                    log(Level.FINEST, installResource.getName() + " is an unsupported type " + installResource.getType() + " to be downloaded.");
                }
            }
        }
        return downloaded;
    }

    public Map<String, Collection<String>> downloadAssetsInstallUtility(Set<String> assetsNames, File toDir, DownloadOption downloadOption, String user, String password,
                                                                        boolean isOverride) throws InstallException {
        fireProgressEvent(InstallProgressEvent.CHECK, 1, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("STATE_CHECKING"));

        createRepoConfig(toDir, assetsNames);
        if (assetsNames == null || assetsNames.isEmpty()) {
            throw ExceptionUtils.createByKey("ERROR_ASSETS_LIST_INVALID");
        }

        Map<String, Collection<String>> downloaded = new HashMap<String, Collection<String>>();
        RepositoryDownloadUtil.writeResourcesToDiskRepo(downloaded, toDir, getResolveDirector().getInstallResources(), this.product.getProductVersion(),
                                                        eventManager, getResolveDirector().defaultRepo());
        return downloaded;
    }

    private void createRepoConfig(File toDir, Set<String> featureNames) throws InstallException {
        try {
            File repoConfigFile = new File(toDir, "repository.config");
            if (repoConfigFile.exists())
                return;

            FileWriter fileWriter = new FileWriter(repoConfigFile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("LayoutPolicy=P1\n");
            bufferedWriter.write("LayoutPolicyVersion=0.0.0.1\n");
            bufferedWriter.write("# repository.type=liberty.directory");
            bufferedWriter.close();
        } catch (Exception e) {
            log(Level.SEVERE, e.getMessage(), e);
            throw ExceptionUtils.createByKey(InstallException.IO_FAILURE, "ERROR_FAILED_TO_DOWNLOAD_FEATURE",
                                             featureNames, toDir.getAbsolutePath());
        }
    }

    public Map<String, InstalledFeature> getInstalledCoreFeatures() {
        try {
            Map<String, InstalledFeature> installedFeatures = new TreeMap<String, InstalledFeature>();
            Map<String, ProvisioningFeatureDefinition> fdMap = product.getCoreFeatureDefinitions();
            for (Entry<String, ProvisioningFeatureDefinition> entry : fdMap.entrySet()) {
                installedFeatures.put(entry.getKey(), new InstalledAssetImpl(entry.getValue()));
            }
            return installedFeatures;
        } catch (FeatureToolException rte) {
            log(Level.FINEST, "Director.getInstalledCoreFeatures() got exception.", rte);
            return null;
        }
    }

    public Map<String, InstalledFeatureCollection> getInstalledFeatureCollections() {
        try {
            Map<String, InstalledFeatureCollection> installedFeatureCollections = new TreeMap<String, InstalledFeatureCollection>();
            Map<String, ProvisioningFeatureDefinition> fdMap = product.getFeatureCollectionDefinitions();
            for (Entry<String, ProvisioningFeatureDefinition> entry : fdMap.entrySet()) {
                installedFeatureCollections.put(entry.getKey(), new InstalledAssetImpl(entry.getValue()));
            }
            return installedFeatureCollections;
        } catch (FeatureToolException rte) {
            log(Level.FINEST, "Director.getInstalledFeatureCollections() got exception.", rte);
            return null;
        }
    }

    public void installAssets(Collection<String> assetIds, File fromDir, RepositoryConnectionList loginInfo) throws InstallException {
        this.installAssets = new ArrayList<List<InstallAsset>>();
        ArrayList<InstallAsset> installAssets = new ArrayList<InstallAsset>();
        ArrayList<String> unresolvedFeatures = new ArrayList<String>();
        Collection<ESAAsset> autoFeatures = getResolveDirector().getAutoFeature(fromDir, InstallConstants.TO_USER);
        getResolveDirector().resolve(assetIds, fromDir, InstallConstants.TO_USER, false, installAssets, unresolvedFeatures);
        if (!unresolvedFeatures.isEmpty()) {
            log(Level.FINEST, "installAssets() determined unresolved features: " + unresolvedFeatures.toString() + " from " + fromDir.getAbsolutePath());
            installAssets(unresolvedFeatures, loginInfo);
        }
        if (!installAssets.isEmpty()) {
            getResolveDirector().resolveAutoFeatures(autoFeatures, installAssets);
            this.installAssets.add(installAssets);
        }
        if (this.installAssets.isEmpty()) {
            throw ExceptionUtils.createByKey(InstallException.ALREADY_EXISTS, "ASSETS_ALREADY_INSTALLED", assetIds.toString());
        }
    }

    public void installAssets(Collection<String> assetIds, RepositoryConnectionList loginInfo) throws InstallException {
        fireProgressEvent(InstallProgressEvent.CHECK, 1, Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("STATE_CHECKING_ASSETS"));

        if (assetIds == null || assetIds.isEmpty()) {
            throw ExceptionUtils.createByKey("ERROR_ASSETS_LIST_INVALID");
        }

        RepositoryConnectionList li = loginInfo == null ? getResolveDirector().getRepositoryConnectionList(null, null, null,
                                                                                                           this.getClass().getCanonicalName() + ".installAssets") : loginInfo;
        Map<String, List<List<RepositoryResource>>> installResources = getResolveDirector().resolveMap(assetIds, li, false);
        if (isEmpty(installResources)) {
            throw ExceptionUtils.createByKey(InstallException.ALREADY_EXISTS, "ASSETS_ALREADY_INSTALLED",
                                             InstallUtils.getShortNames(product.getFeatureDefinitions(), assetIds).toString());
        }
        downloadAssets(installResources, InstallConstants.TO_USER);
    }

    private void downloadAssets(Map<String, List<List<RepositoryResource>>> installResources, String toExtension) throws InstallException {
        if (installResources == null || installResources.isEmpty()) {
            this.installAssets = new ArrayList<List<InstallAsset>>(1);
            return;
        } else {
            this.installAssets = new ArrayList<List<InstallAsset>>(installResources.size());
        }
        int progress = 10;
        int interval1 = installResources.size() == 0 ? 40 : 40 / installResources.size();
        for (Entry<String, List<List<RepositoryResource>>> targetList : installResources.entrySet()) {
            int interval2 = targetList.getValue().size() == 0 ? interval1 : interval1 / targetList.getValue().size();
            for (List<RepositoryResource> mrList : targetList.getValue()) {
                List<InstallAsset> iaList = new ArrayList<InstallAsset>(mrList.size());
                this.installAssets.add(iaList);
                int interval3 = mrList.size() == 0 ? interval2 : interval2 / mrList.size();
                for (RepositoryResource installResource : mrList) {
                    if (InstallUtils.isDirectoryBasedRepository(installResource))
                        fireDownloadProgressEvent(progress, installResource);
                    progress += interval3;
                    File d = InstallUtils.getFileDirectoryBasedRepository(installResource);
                    if (installResource.getType().equals(ResourceType.FEATURE)) {
                        EsaResource esa = (EsaResource) installResource;
                        ESAAsset esaAsset;
                        String target = targetList.getKey();
                        try {
                            if (d != null) {
                                esaAsset = new ESAAsset(esa.getName(), esa.getProvideFeature(), InstallUtils.toExtension(target, toExtension), d, false);
                                if (esaAsset.getSubsystemEntry() == null) {
                                    throw ExceptionUtils.create(Messages.PROVISIONER_MESSAGES.getLogMessage("tool.install.content.no.subsystem.manifest"),
                                                                InstallException.BAD_FEATURE_DEFINITION);
                                }
                                ProvisioningFeatureDefinition fd = esaAsset.getProvisioningFeatureDefinition();
                                if (!fd.isSupportedFeatureVersion()) {
                                    throw ExceptionUtils.create(Messages.PROVISIONER_MESSAGES.getLogMessage("UNSUPPORTED_FEATURE_VERSION", fd.getFeatureName(),
                                                                                                            fd.getIbmFeatureVersion()),
                                                                InstallException.BAD_FEATURE_DEFINITION);
                                }
                            } else {
                                esaAsset = new ESAAsset(esa.getName(), esa.getProvideFeature(), InstallUtils.toExtension(target, toExtension), esa);
                            }
                            iaList.add(esaAsset);
                        } catch (Exception e) {
                            throw ExceptionUtils.createByKey(e, "ERROR_INVALID_ESA", esa.getName());
                        }
                    } else if (installResource.getType().equals(ResourceType.IFIX)) {
                        try {
                            if (d != null)
                                iaList.add(new FixAsset(installResource.getName(), d, false));
                            else
                                iaList.add(new FixAsset(installResource.getName(), (IfixResource) installResource));
                        } catch (Exception e) {
                            throw ExceptionUtils.createByKey(e, "ERROR_INVALID_IFIX", installResource.getName());
                        }
                    } else if (installResource.getType().equals(ResourceType.PRODUCTSAMPLE)) {
                        try {
                            if (d != null)
                                iaList.add(new SampleAsset(installResource.getName(), ((SampleResource) installResource).getShortName(), d, false));
                            else
                                iaList.add(new SampleAsset((SampleResource) installResource));
                        } catch (Exception e) {
                            throw ExceptionUtils.createByKey(e, "ERROR_INVALID_SAMPLE", installResource.getName());
                        }
                    } else if (installResource.getType().equals(ResourceType.OPENSOURCE)) {
                        try {
                            if (d != null)
                                iaList.add(new OpenSourceAsset(installResource.getName(), ((SampleResource) installResource).getShortName(), d, false));
                            else
                                iaList.add(new OpenSourceAsset((SampleResource) installResource));
                        } catch (Exception e) {
                            throw ExceptionUtils.createByKey(e, "ERROR_INVALID_OPENSOURCE", installResource.getName());
                        }
                    }
                }
            }
        }
    }

    public void checkResources() throws InstallException {
        getResolveDirector().checkResources();
        if (installAssets != null) {
            for (List<InstallAsset> iaList : installAssets) {
                for (InstallAsset ia : iaList) {
                    checkResource(ia);
                }
            }
        }

        long required = getResolveDirector().getInstallResourcesMainAttachmentSize();
        String requiredSpace = castToPrintableMessage(required);
        logger.log(Level.FINEST, "Total required space for installation is " + requiredSpace + " including temporary files.");
        File wlpDir = product.getInstallDir();
        long free = wlpDir.getFreeSpace();
        String wlpDirSpace = castToPrintableMessage(wlpDir.getFreeSpace());
        logger.log(Level.FINEST, "Total available space is " + wlpDirSpace + ".");
        if (free < required) {
            try {
                throw ExceptionUtils.createByKey("ERROR_WLP_DIR_NO_SPACE", wlpDir.getCanonicalPath(), wlpDirSpace, requiredSpace);
            } catch (IOException e) {
                throw ExceptionUtils.create(e);
            }
        }
    }

    public String castToPrintableMessage(long number) {
        long i = number / 1048576;
        if (i > 0)
            return String.valueOf(i) + " MB";

        else {
            long l = number / 1024;
            return String.valueOf(l) + " KB";
        }

    }

    private void checkResource(InstallAsset installAsset) throws InstallException {
        if (installAsset.isSample() || installAsset.isOpenSource()) {
            JarAsset ja = (JarAsset) installAsset;
            String serverName = ja.getShortName();
            if (serverName != null) {
                File serverDir = new File(Utils.getUserDir(), "servers/" + serverName);
                if (serverDir.exists()) {
                    String msgId = installAsset.isSample() ? "ERROR_SAMPLE_SERVER_ALREADY_INSTALLED" : "ERROR_OPENSOURCE_SERVER_ALREADY_INSTALLED";
                    throw new InstallException(Messages.INSTALL_KERNEL_MESSAGES.getLogMessage(msgId, ja.toString(), serverName));
                }
            }
        }
    }

    public int getInstallResourcesSize() {
        return getResolveDirector().getInstallResourcesSize();
    }

    public int getLocalInstallAssetsSize() {
        return getResolveDirector().getLocalInstallAssetsSize();
    }

    public int getPublicInstallResourcesSize() {
        return getResolveDirector().getPublicInstallResourcesSize();
    }

    public int getPublicLocalInstallAssetsSize() {
        return getResolveDirector().getPublicLocalInstallAssetsSize();
    }

    public Map<String, Collection<String>> getInstalledAssetNames() {
        if (installAssets == null)
            return null;
        Map<String, Collection<String>> installed = new HashMap<String, Collection<String>>();
        Collection<String> installedAddons = new ArrayList<String>();
        Collection<String> installedFeatures = new ArrayList<String>();
        Collection<String> installedFixes = new ArrayList<String>();
        Collection<String> installedSamples = new ArrayList<String>();
        Collection<String> installedOpenSources = new ArrayList<String>();
        for (List<InstallAsset> iaList : installAssets) {
            for (InstallAsset asset : iaList) {
                if (asset.isFeature()) {
                    ESAAsset esa = (ESAAsset) asset;
                    if (esa.isPublic()) {
                        String esaName = esa.getShortName();
                        if (esaName == null || esaName.isEmpty())
                            esaName = esa.getFeatureName();
                        if (esa.isAddon())
                            installedAddons.add(esaName);
                        else
                            installedFeatures.add(esaName);
                    }
                } else if (asset.isFix()) {
                    installedFixes.add(asset.toString());
                } else if (asset.isSample()) {
                    installedSamples.add(asset.toString());
                } else if (asset.isOpenSource()) {
                    installedOpenSources.add(asset.toString());
                }
            }
        }
        installed.put(InstallConstants.ADDON, installedAddons);
        installed.put(InstallConstants.FEATURE, installedFeatures);
        installed.put(InstallConstants.IFIX, installedFixes);
        installed.put(InstallConstants.SAMPLE, installedSamples);
        installed.put(InstallConstants.OPENSOURCE, installedOpenSources);
        return installed;
    }

    public Set<InstallLicense> getFeatureLicense(Locale locale) throws InstallException {
        Map<String, InstallLicenseImpl> licenseIds = new HashMap<String, InstallLicenseImpl>();
        getFeatureLicenseFromInstallAssets(locale, licenseIds, getResolveDirector().getLocalInstallAssets());
        getFeatureLicenseFromInstallResources(locale, licenseIds, getResolveDirector().getInstallResources());
        Set<InstallLicense> licenses = new HashSet<InstallLicense>();
        licenses.addAll(licenseIds.values());
        return licenses;
    }

    private void getFeatureLicenseFromInstallAssets(Locale locale, Map<String, InstallLicenseImpl> licenseIds, List<InstallAsset> installAssets) {
        if (installAssets == null)
            return;
        for (InstallAsset installAsset : installAssets) {
            if (installAsset.isFeature()) {
                ESAAsset esa = (ESAAsset) installAsset;
                if (esa.isPublic()) {
                    ExeInstallAction.incrementNumOfLocalFeatures();
                }
                LicenseProvider lp = esa.getLicenseProvider(locale);
                String licenseId = esa.getLicenseId();
                if (licenseId != null && !licenseId.isEmpty()) {
                    InstallLicenseImpl ili = licenseIds.get(licenseId);
                    if (ili == null) {
                        ili = new InstallLicenseImpl(licenseId, null, lp);
                        licenseIds.put(licenseId, ili);
                    }
                    ili.addFeature(esa.getProvideFeature());
                }
            }
        }
    }

    public Collection<String> getSampleLicense(Locale locale) throws InstallException {
        Collection<String> licenses = new ArrayList<String>();
        for (List<List<RepositoryResource>> targetList : getResolveDirector().getInstallResources().values()) {
            for (List<RepositoryResource> mrList : targetList) {
                for (RepositoryResource mr : mrList) {
                    ResourceType type = mr.getType();
                    if (type.equals(ResourceType.PRODUCTSAMPLE) ||
                        type.equals(ResourceType.OPENSOURCE)) {
                        try {
                            AttachmentResource lar = mr.getLicenseAgreement(locale);
                            if (lar != null)
                                licenses.add(InstallLicenseImpl.getLicense(lar));
                        } catch (RepositoryException e) {
                            throw ExceptionUtils.createByKey(e, "ERROR_FAILED_TO_GET_ASSET_LICENSE", mr.getName());
                        }
                    }
                }
            }
        }
        return licenses;
    }

    public Collection<String> getSamplesOrOpenSources() {
        Collection<String> samplesOrOpenSources = new ArrayList<String>();
        Map<String, List<List<RepositoryResource>>> installResources = getResolveDirector().getInstallResources();
        if (installResources != null) {
            for (List<List<RepositoryResource>> targetList : installResources.values()) {
                for (List<RepositoryResource> mrList : targetList) {
                    for (RepositoryResource mr : mrList) {
                        ResourceType type = mr.getType();
                        if (type.equals(ResourceType.PRODUCTSAMPLE) ||
                            type.equals(ResourceType.OPENSOURCE)) {
                            samplesOrOpenSources.add(InstallUtils.getResourceId(mr));
                        }
                    }
                }
            }
        }
        return samplesOrOpenSources;
    }

    public void downloadAssets(String toExtension) throws InstallException {
        downloadAssets(getResolveDirector().getInstallResources(), toExtension);
        if (getResolveDirector().getLocalInstallAssetsSize() > 0)
            this.installAssets.add(getResolveDirector().getLocalInstallAssets());
    }

    public void setFirePublicAssetOnly(boolean firePublicAssetOnly) {
        this.firePublicAssetOnly = firePublicAssetOnly;
    }

    public RepositoryConnectionList getRepositoryConnectionList() throws InstallException {
        return getResolveDirector().getRepositoryConnectionList(null, null, null, this.getClass().getCanonicalName());
    }

    public void setProxy(RestRepositoryConnectionProxy proxy) {
        getResolveDirector().setProxy(proxy);
    }

    private ResolveDirector getResolveDirector() {
        if (resolveDirector == null)
            resolveDirector = new ResolveDirector(product, eventManager, logger);
        return resolveDirector;
    }

    public void resolve(Collection<String> assetIds, boolean download) throws InstallException {
        getResolveDirector().resolve(assetIds, download);
    }

    public void resolve(String feature, File esaFile, String toExtension) throws InstallException {
        getResolveDirector().resolve(feature, esaFile, toExtension);
    }

    public boolean resolveExistingAssetsFromDirectoryRepo(Collection<String> featureNames, File repoDir, boolean isOverwrite) throws InstallException {
        return getResolveDirector().resolveExistingAssetsFromDirectoryRepo(featureNames, repoDir, isOverwrite);
    }

    private UninstallDirector getUninstallDirector() {
        if (uninstallDirector == null)
            uninstallDirector = new UninstallDirector(product, engine, eventManager, logger);
        return uninstallDirector;
    }

    public void uninstall(Collection<String> ids, boolean force) throws InstallException {
        getUninstallDirector().uninstall(ids, force);
    }

    public void uninstallFeatures(Collection<String> featureNames, Collection<String> uninstallInstallFeatures) {
        getUninstallDirector().uninstallFeatures(featureNames, uninstallInstallFeatures, false);
    }

    public void uninstallFix(String fixId) throws InstallException {
        getUninstallDirector().uninstallFix(fixId);
    }

    public void uninstallFix(Collection<String> fixNames) throws InstallException {
        getUninstallDirector().uninstallFix(fixNames);
    }

    public void uninstall(boolean checkDependency, String productId, Collection<File> toBeDeleted) throws InstallException {
        getUninstallDirector().uninstall(checkDependency, productId, toBeDeleted);
    }

    public void uninstallFeaturesPrereqChecking(Collection<String> featureNames, boolean allowUninstallAll, boolean force) throws InstallException {
        getUninstallDirector().uninstallFeaturesPrereqChecking(featureNames, allowUninstallAll, force);
    }

    public void uninstallFeaturesByProductId(String productId, boolean exceptPlatfromFeatuers) throws InstallException {
        getUninstallDirector().uninstallFeaturesByProductId(productId, exceptPlatfromFeatuers);
    }

    public void checkAssetsNotInstalled(Collection<String> assetIds) throws InstallException {
        getResolveDirector().checkAssetsNotInstalled(assetIds);
    }
}
