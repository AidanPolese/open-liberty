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
package com.ibm.ws.install.repository.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.ibm.ws.install.internal.InstallLogUtils.Messages;
import com.ibm.ws.install.internal.asset.ESAVirtualAsset;
import com.ibm.ws.install.repository.RepositoryException;
import com.ibm.ws.kernel.feature.Visibility;
import com.ibm.ws.kernel.feature.provisioning.ProvisioningFeatureDefinition;
import com.ibm.ws.kernel.productinfo.ProductInfo;

/**
 *
 */
public class ZipRepository extends BaseRepository {

    private ZipFile zipFile = null;

    /**
     * @param rootPath
     * @throws RepositoryException
     */
    public ZipRepository(ZipFile zipFile) throws RepositoryException {
        super();
        this.classname = "ZipRepository";
        this.zipFile = zipFile;
        if (zipFile.getEntry("feature.properties") == null)
            throw createException(RepositoryUtils.getMessage("ERROR_INVALID_INDEX_FILE", this.zipFile.getName()));
    }

    @Override
    protected Collection<?> getEsaAsset(String productId, String productVersion, String productInstallType, String productLicenseType, String productEdition,
                                        Visibility visibility) {
        String method = "getEsaAsset()";
        Collection<ESAVirtualAsset> esaAssets = new ArrayList<ESAVirtualAsset>();
        Properties props = new Properties();
        props.put(ProductInfo.COM_IBM_WEBSPHERE_PRODUCTID_KEY, productId);
        props.put(ProductInfo.COM_IBM_WEBSPHERE_PRODUCTVERSION_KEY, productVersion);
        props.put(RepositoryUtils.COM_IBM_WEBSPHERE_PRODUCTINSTALLTYPE, productInstallType);
        props.put(ProductInfo.COM_IBM_WEBSPHERE_PRODUCTEDITION_KEY, productEdition);
        if (productLicenseType != null)
            props.put(RepositoryUtils.COM_IBM_WEBSPHERE_PRODUCTLICENSETYPE, productLicenseType);

        Enumeration<? extends ZipEntry> entries = this.zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.isDirectory() && entry.getName().indexOf('/') == entry.getName().lastIndexOf('/')) {
                try {
                    ESAVirtualAsset esaAsset = new ESAVirtualAsset(this.zipFile, entry.getName(), "", false);
                    if (esaAsset.getSubsystemEntry() == null) {
                        log(method, Messages.PROVISIONER_MESSAGES.getLogMessage("tool.install.content.no.subsystem.manifest"));
                    } else {
                        ProvisioningFeatureDefinition fd = esaAsset.getProvisioningFeatureDefinition();
                        if (fd.getVisibility() == visibility) {
                            if (fd.isSupportedFeatureVersion()) {
                                String appliesTo = fd.getHeader("IBM-AppliesTo");
                                if (RepositoryUtils.matchAppliesTo("Feature", esaAsset.getFeatureName(), this.zipFile.getName(), appliesTo, productId, productVersion,
                                                                   productInstallType,
                                                                   productLicenseType,
                                                                   productEdition)) {
                                    esaAssets.add(esaAsset);
                                }
                            } else {
                                log(method, Messages.PROVISIONER_MESSAGES.getLogMessage("UNSUPPORTED_FEATURE_VERSION", fd.getFeatureName(), fd.getIbmFeatureVersion()));
                            }
                        } else {
                            log(method, "The feature " + fd.getFeatureName() + " is not " + visibility);
                        }
                    }
                } catch (ZipException e) {
                    log(method, Messages.PROVISIONER_MESSAGES.getLogMessage("tool.install.bad.zip", this.zipFile.getName()), e);
                } catch (IOException e) {
                    log(method, Messages.PROVISIONER_MESSAGES.getLogMessage("tool.install.bad.zip", this.zipFile.getName()), e);
                }
            }

        }
        return esaAssets;
    }

}
