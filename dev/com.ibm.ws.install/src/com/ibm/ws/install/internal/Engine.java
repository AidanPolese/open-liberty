/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013, 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.install.internal;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.ibm.ws.install.InstallConstants.ExistsAction;
import com.ibm.ws.install.InstallException;
import com.ibm.ws.install.internal.adaptor.ESAAdaptor;
import com.ibm.ws.install.internal.adaptor.FixAdaptor;
import com.ibm.ws.install.internal.adaptor.ServerPackageJarAdaptor;
import com.ibm.ws.install.internal.adaptor.ServicePackageAdaptor;
import com.ibm.ws.install.internal.asset.ESAAsset;
import com.ibm.ws.install.internal.asset.FixAsset;
import com.ibm.ws.install.internal.asset.InstallAsset;
import com.ibm.ws.install.internal.asset.JarAsset;
import com.ibm.ws.install.internal.asset.ServerPackageAsset;
import com.ibm.ws.install.internal.asset.UninstallAsset;
import com.ibm.ws.install.internal.asset.UninstallAsset.UninstallAssetType;
import com.ibm.ws.kernel.feature.provisioning.ProvisioningFeatureDefinition;
import com.ibm.ws.kernel.provisioning.ExtensionConstants;
import com.ibm.ws.repository.connections.RestRepositoryConnectionProxy;

public class Engine {

    private final Product product;

    public Engine(Product product) {
        this.product = product;
    }

    public void install(InstallAsset installAsset, List<File> filesInstalled, Collection<String> featuresToBeInstalled, ExistsAction existsAction,
                        Set<String> executableFiles, Map<String, Set<String>> extattrFiles, boolean downloadDependencies,
                        RestRepositoryConnectionProxy proxy, ChecksumsManager checksumsManager) throws IOException, InstallException {
        if (installAsset.isFeature())
            ESAAdaptor.install(product, (ESAAsset) installAsset, filesInstalled, featuresToBeInstalled, existsAction, executableFiles, extattrFiles, checksumsManager);
        else if (installAsset.isFix())
            FixAdaptor.install(product, (FixAsset) installAsset);
        else if (installAsset.isServerPackage())
            if (installAsset instanceof JarAsset) {
                ServerPackageJarAdaptor.install(product, (JarAsset) installAsset, filesInstalled, downloadDependencies, proxy);
            } else {
                ServicePackageAdaptor.install(product, (ServerPackageAsset) installAsset, filesInstalled, existsAction);
            }
        else if (installAsset.isSample())
            ServerPackageJarAdaptor.install(product, (JarAsset) installAsset, filesInstalled, downloadDependencies, proxy);
        else if (installAsset.isOpenSource())
            ServerPackageJarAdaptor.install(product, (JarAsset) installAsset, filesInstalled, downloadDependencies, proxy);
    }

    public void uninstall(UninstallAsset uninstallAsset, boolean checkDependency, List<File> filesRestored) throws IOException, ParserConfigurationException, SAXException, InstallException {
        if (uninstallAsset.getType().equals(UninstallAssetType.feature)) {
            // Remove the feature contents and metadata            
            ESAAdaptor.uninstallFeature(uninstallAsset.getProvisioningFeatureDefinition(), product.getFeatureDefinitions(),
                                        getBaseDir(uninstallAsset.getProvisioningFeatureDefinition()), checkDependency, filesRestored);
        } else if (uninstallAsset.getType().equals(UninstallAssetType.fix)) {
            FixAdaptor.uninstallFix(uninstallAsset.getIFixInfo(), product.getInstallDir(), filesRestored);
        }
        InstallUtils.updateFingerprint(product.getInstallDir());
    }

    private File getBaseDir(ProvisioningFeatureDefinition pd) throws InstallException {
        if (pd.getBundleRepositoryType().equals(ExtensionConstants.USER_EXTENSION)) {
            return product.getUserExtensionDir();
        } else if (pd.getBundleRepositoryType().equals(ExtensionConstants.CORE_EXTENSION)) {
            return product.getInstallDir();
        } else {
            String repoType = pd.getBundleRepositoryType();
            return product.getUserDirExternal(repoType);
        }
    }

    public void preCheck(UninstallAsset uninstallAsset, boolean checkDependency) throws InstallException {
        if (uninstallAsset.getType().equals(UninstallAssetType.feature)) {
            ESAAdaptor.preCheck(uninstallAsset.getProvisioningFeatureDefinition(), product.getFeatureDefinitions(),
                                getBaseDir(uninstallAsset.getProvisioningFeatureDefinition()), checkDependency);
        } else if (uninstallAsset.getType().equals(UninstallAssetType.fix)) {
            FixAdaptor.preCheck(uninstallAsset.getIFixInfo(), product.getInstallDir());
        }
    }

}
