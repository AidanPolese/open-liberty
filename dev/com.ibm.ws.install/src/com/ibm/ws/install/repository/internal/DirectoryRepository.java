/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014, 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.install.repository.internal;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.zip.ZipException;

import com.ibm.ws.install.internal.InstallLogUtils.Messages;
import com.ibm.ws.install.internal.asset.ESAAsset;
import com.ibm.ws.install.repository.RepositoryException;
import com.ibm.ws.kernel.feature.Visibility;
import com.ibm.ws.kernel.feature.provisioning.ProvisioningFeatureDefinition;
import com.ibm.ws.kernel.productinfo.ProductInfo;

/**
 *
 */
public class DirectoryRepository extends BaseRepository {

    private File rootPath = null;

    /**
     * @param rootPath
     * @throws RepositoryException
     */
    public DirectoryRepository(File rootPath) throws RepositoryException {
        super();
        this.classname = "DirectoryRepository";
        if (!rootPath.exists()) {
            throw createException(RepositoryUtils.getMessage("ERROR_DIRECTORY_NOT_EXISTS", rootPath.getAbsolutePath()));
        }
        if (rootPath.isFile()) {
            throw createException(RepositoryUtils.getMessage("ERROR_PATH_IS_FILE", rootPath.getAbsolutePath()));
        }
        this.rootPath = rootPath;
    }

    @Override
    protected Collection<?> getEsaAsset(String productId, String productVersion, String productInstallType, String productLicenseType, String productEdition,
                                        Visibility visibility) {
        String method = "getEsaAsset()";
        Collection<ESAAsset> esaAssets = new ArrayList<ESAAsset>();
        Properties props = new Properties();
        props.put(ProductInfo.COM_IBM_WEBSPHERE_PRODUCTID_KEY, productId);
        props.put(ProductInfo.COM_IBM_WEBSPHERE_PRODUCTVERSION_KEY, productVersion);
        props.put(RepositoryUtils.COM_IBM_WEBSPHERE_PRODUCTINSTALLTYPE, productInstallType);
        props.put(ProductInfo.COM_IBM_WEBSPHERE_PRODUCTEDITION_KEY, productEdition);
        if (productLicenseType != null)
            props.put(RepositoryUtils.COM_IBM_WEBSPHERE_PRODUCTLICENSETYPE, productLicenseType);
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase().endsWith(".esa");
            }
        };
        File[] esas = this.rootPath.listFiles(filter);
        for (File esa : esas) {
            try {
                ESAAsset esaAsset = new ESAAsset(esa, "", false);
                if (esaAsset.getSubsystemEntry() == null) {
                    log(method, Messages.PROVISIONER_MESSAGES.getLogMessage("tool.install.content.no.subsystem.manifest"));
                } else {
                    ProvisioningFeatureDefinition fd = esaAsset.getProvisioningFeatureDefinition();
                    if (fd.getVisibility() == visibility) {
                        if (fd.isSupportedFeatureVersion()) {
                            String appliesTo = fd.getHeader("IBM-AppliesTo");
                            if (RepositoryUtils.matchAppliesTo("Feature", esaAsset.getFeatureName(), esa.getName(), appliesTo, productId, productVersion, productInstallType,
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
                log(method, Messages.PROVISIONER_MESSAGES.getLogMessage("tool.install.bad.zip", esa.getAbsolutePath()), e);
            } catch (IOException e) {
                log(method, Messages.PROVISIONER_MESSAGES.getLogMessage("tool.install.bad.zip", esa.getAbsolutePath()), e);
            }
        }

        return esaAssets;
    }

}
