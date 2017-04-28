/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015, 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.install.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.ws.install.InstallConstants;
import com.ibm.ws.install.InstallException;
import com.ibm.ws.install.internal.InstallLogUtils.Messages;
import com.ibm.ws.install.repository.download.RepositoryDownloadUtil;
import com.ibm.ws.kernel.productinfo.DuplicateProductInfoException;
import com.ibm.ws.kernel.productinfo.ProductInfo;
import com.ibm.ws.kernel.productinfo.ProductInfoParseException;
import com.ibm.ws.kernel.productinfo.ProductInfoReplaceException;
import com.ibm.ws.repository.common.enums.ResourceType;
import com.ibm.ws.repository.connections.RepositoryConnection;
import com.ibm.ws.repository.connections.RestRepositoryConnectionProxy;
import com.ibm.ws.repository.exceptions.RepositoryBackendException;
import com.ibm.ws.repository.exceptions.RepositoryBackendIOException;
import com.ibm.ws.repository.exceptions.RepositoryException;
import com.ibm.ws.repository.exceptions.RepositoryResourceException;
import com.ibm.ws.repository.resolver.RepositoryResolutionException;
import com.ibm.ws.repository.resolver.RepositoryResolutionException.MissingRequirement;
import com.ibm.ws.repository.resources.RepositoryResource;

import wlp.lib.extract.SelfExtractor;

public class ExceptionUtils {

    public static boolean isCertPathBuilderException(Throwable cause) {
        if (cause == null)
            return false;
        if (cause instanceof java.security.cert.CertPathBuilderException)
            return true;
        return isCertPathBuilderException(cause.getCause());
    }

    public static InstallException createByKey(Exception e, String msgKey, Object... args) {
        return create(Messages.INSTALL_KERNEL_MESSAGES.getLogMessage(msgKey, args), e);
    }

    static InstallException createByKey(String msgKey, Object... args) {
        return create(Messages.INSTALL_KERNEL_MESSAGES.getLogMessage(msgKey, args));
    }

    static InstallException create(String msg) {
        return create(msg, InstallException.RUNTIME_EXCEPTION);
    }

    static InstallException createByKey(int rc, String msgKey, Object... args) {
        return create(Messages.INSTALL_KERNEL_MESSAGES.getLogMessage(msgKey, args), rc);
    }

    static InstallException create(String msg, int rc) {
        return new InstallException(msg, rc);
    }

    static InstallException create(String msg, Exception e) {
        return create(msg, e, InstallException.RUNTIME_EXCEPTION);
    }

    static InstallException createByKey(int rc, Exception e, String msgKey, Object... args) {
        return create(Messages.INSTALL_KERNEL_MESSAGES.getLogMessage(msgKey, args), e, rc);
    }

    public static InstallException createFailedToDownload(RepositoryResource installResource, Exception e, File toDir) {
        String target = toDir == null ? System.getProperty("java.io.tmpdir") : toDir.getAbsolutePath();
        if (e instanceof IOException)
            return ExceptionUtils.createByKey(InstallException.IO_FAILURE, e, "ERROR_FAILED_DOWNLOAD_ASSETS_TO_TEMP",
                                              InstallUtils.getResourceId(installResource), InstallUtils.getResourceName(installResource), target);
        if (e instanceof RepositoryBackendException) {
            RepositoryConnection failingConnection = ((RepositoryBackendException) e).getFailingConnection();
            String repositoryLocation = failingConnection.getRepositoryLocation();
            boolean isDefaultRepo = repositoryLocation.startsWith(InstallConstants.REPOSITORY_LIBERTY_URL);
            if (ExceptionUtils.isCertPathBuilderException(e.getCause()))
                return ExceptionUtils.createByKey(e, isDefaultRepo ? "ERROR_FAILED_TO_CONNECT_CAUSED_BY_CERT" : "ERROR_FAILED_TO_CONNECT_REPO_CAUSED_BY_CERT", repositoryLocation);
            return ExceptionUtils.createByKey(e, isDefaultRepo ? "ERROR_FAILED_TO_DOWNLOAD_ASSETS_FROM_DEFAULT_REPO" : "ERROR_FAILED_TO_DOWNLOAD_ASSETS_FROM_REPO",
                                              InstallUtils.getResourceId(installResource), InstallUtils.getResourceName(installResource), repositoryLocation);
        }
        if (e instanceof RepositoryResourceException)
            return ExceptionUtils.createByKey(e, "ERROR_FAILED_TO_DOWNLOAD_ASSETS", InstallUtils.getResourceId(installResource),
                                              InstallUtils.getResourceName(installResource));
        if (installResource.getType().equals(ResourceType.FEATURE))
            return createByKey(InstallException.IO_FAILURE, e, "ERROR_FAILED_TO_DOWNLOAD_FEATURE", installResource.getName(), target);
        if (installResource.getType().equals(ResourceType.IFIX))
            return createByKey(InstallException.IO_FAILURE, e, "ERROR_FAILED_TO_DOWNLOAD_IFIX", installResource.getName(), target);
        if (installResource.getType().equals(ResourceType.PRODUCTSAMPLE))
            return createByKey(InstallException.IO_FAILURE, e, "ERROR_FAILED_TO_DOWNLOAD_SAMPLE", installResource.getName(), target);
        if (installResource.getType().equals(ResourceType.OPENSOURCE))
            return createByKey(InstallException.IO_FAILURE, e, "ERROR_FAILED_TO_DOWNLOAD_OPENSOURCE", installResource.getName(), target);
        return createByKey(e, "ERROR_UNSUPPORTED");
    }

    static InstallException create(Exception e) {
        if (e instanceof ProductInfoParseException) {
            ProductInfoParseException pipe = (ProductInfoParseException) e;
            String missingKey = pipe.getMissingKey();
            if (missingKey != null) {
                return create(Messages.UTILITY_MESSAGES.getLogMessage("version.missing.key", missingKey, pipe.getFile().getAbsoluteFile()));
            }
            return create(Messages.UTILITY_MESSAGES.getLogMessage("ERROR_UNABLE_READ_FILE", pipe.getFile().getAbsoluteFile(),
                                                                  pipe.getCause().getMessage()));
        }
        if (e instanceof DuplicateProductInfoException) {
            DuplicateProductInfoException dpie = (DuplicateProductInfoException) e;
            return create(Messages.UTILITY_MESSAGES.getLogMessage("version.duplicated.productId", ProductInfo.COM_IBM_WEBSPHERE_PRODUCTID_KEY,
                                                                  dpie.getProductInfo1().getFile().getAbsoluteFile(),
                                                                  dpie.getProductInfo2().getFile().getAbsoluteFile()));
        }
        if (e instanceof ProductInfoReplaceException) {
            ProductInfoReplaceException pire = (ProductInfoReplaceException) e;
            ProductInfo productInfo = pire.getProductInfo();
            String replacesId = productInfo.getReplacesId();
            if (replacesId.equals(productInfo.getId())) {
                return create(Messages.UTILITY_MESSAGES.getLogMessage("version.replaced.product.can.not.itself", productInfo.getFile().getAbsoluteFile()));
            }
            return create(Messages.UTILITY_MESSAGES.getLogMessage("version.replaced.product.not.exist", replacesId,
                                                                  productInfo.getFile().getAbsoluteFile()));
        }
        if (e instanceof RepositoryBackendIOException) {
            return create((RepositoryException) e, new Throwable(e), (RestRepositoryConnectionProxy) null, true);
        }
        return create(e.getMessage(), e, InstallException.RUNTIME_EXCEPTION);
    }

    static InstallException create(RepositoryException e, Throwable cause, RestRepositoryConnectionProxy proxy, boolean defaultRepo) {

        if (proxy != null && cause instanceof UnknownHostException)
            return create(Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("ERROR_TOOL_UNKNOWN_PROXY_HOST", proxy.getProxyURL().getHost()), e);
        else if (cause instanceof ConnectException && cause.getMessage() != null && cause.getMessage().contains("Connection refused") && proxy != null)
            return create(Messages.INSTALL_KERNEL_MESSAGES.getLogMessage("ERROR_TOOL_INCORRECT_PROXY_PORT", String.valueOf(proxy.getProxyURL().getPort())),
                          e);
        else if (isCertPathBuilderException(cause))
            return create(Messages.INSTALL_KERNEL_MESSAGES.getLogMessage(defaultRepo ? "ERROR_FAILED_TO_CONNECT_CAUSED_BY_CERT" : "ERROR_FAILED_TO_CONNECT_REPOS_CAUSED_BY_CERT"),
                          e, InstallException.RUNTIME_EXCEPTION);
        else
            return create(Messages.INSTALL_KERNEL_MESSAGES.getLogMessage(defaultRepo ? "ERROR_FAILED_TO_CONNECT" : "ERROR_FAILED_TO_CONNECT_REPOS"),
                          e, InstallException.RUNTIME_EXCEPTION);
    }

    static InstallException create(RepositoryException e, Collection<String> featureNames, boolean installingAsset, RestRepositoryConnectionProxy proxy, boolean defaultRepo) {
        Throwable cause = e;
        Throwable rootCause = e;

        // Check the list of causes of the exception for connection issues
        while ((rootCause = cause.getCause()) != null && cause != rootCause) {
            if (rootCause instanceof UnknownHostException || rootCause instanceof FileNotFoundException || rootCause instanceof ConnectException) {
                return create(e, rootCause, proxy, defaultRepo);
            }
            cause = rootCause;
        }

        if (featureNames != null) {
            if (isCertPathBuilderException(cause))
                return createByKey(InstallException.CONNECTION_FAILED, e, defaultRepo ? "ERROR_FAILED_TO_CONNECT_CAUSED_BY_CERT" : "ERROR_FAILED_TO_CONNECT_REPOS_CAUSED_BY_CERT");

            String featuresListStr = InstallUtils.getFeatureListOutput(featureNames);
            InstallException ie = create(Messages.INSTALL_KERNEL_MESSAGES.getLogMessage(installingAsset ? "ERROR_FAILED_TO_RESOLVE_ASSETS" : "ERROR_FAILED_TO_RESOLVE_FEATURES",
                                                                                        featuresListStr),
                                         e);
            ie.setData(featuresListStr);

            return ie;

        } else
            return create(e);
    }

    //need to differentiate download and install here
    static InstallException create(RepositoryResolutionException e, Collection<String> assetNames, File installDir, boolean installingAsset) {
        Collection<MissingRequirement> allRequirementsNotFound = e.getAllRequirementsResourcesNotFound();
        Collection<MissingRequirement> dependants = new ArrayList<MissingRequirement>(allRequirementsNotFound.size());
        for (MissingRequirement f : allRequirementsNotFound) {
            /**
             * make sure it's not invalid asset names entered
             */
            if (!assetNames.contains(f.getRequirementName())) {
                dependants.add(f);
            }
        }

        /**
         * not valid for current product case or dependency not applicable
         */

        MissingRequirement missingRequirementWithMaxVersion = null;
        String newestVersion = null;
        for (MissingRequirement mr : allRequirementsNotFound) {
            if (missingRequirementWithMaxVersion == null)
                missingRequirementWithMaxVersion = mr;
            String r = mr.getRequirementName();
            if (r.contains("productInstallType") ||
                r.contains("productEdition") ||
                r.contains("productVersion")) {

                Pattern validNumericVersionOrRange = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");
                Matcher matcher = validNumericVersionOrRange.matcher(r);
                String version = null;
                while (matcher.find()) {
                    version = matcher.group();
                    break;
                }
                List productMatchers = SelfExtractor.parseAppliesTo(mr.getRequirementName());
                wlp.lib.extract.ReturnCode validInstallRC = SelfExtractor.validateProductMatches(installDir, productMatchers);
                String currentEdition = "";
                if (validInstallRC.getMessageKey().equals("invalidVersion") || validInstallRC.getMessageKey().equals("invalidEdition")) {
                    int productEdition = 2;
                    if (validInstallRC.getMessageKey().equals("invalidEdition")) {
                        productEdition = 0;
                    }
                    currentEdition = (String) validInstallRC.getParameters()[productEdition];
                }
                if (!!!currentEdition.equals("Liberty Early Access")) {
                    if (isNewerVersion(version, newestVersion, false)) {
                        missingRequirementWithMaxVersion = mr;
                        newestVersion = version;
                    }
                } else {
                    if (isNewerVersion(version, newestVersion, true)) {
                        missingRequirementWithMaxVersion = mr;
                        newestVersion = version;
                    }
                }

            }
        }

        /**
         * missing dependent case, keep the current message
         */
        if (dependants.size() > 0) {
            String missingRequirement = missingRequirementWithMaxVersion.getRequirementName();
            if (!missingRequirement.contains(";")
                && !missingRequirement.contains("productInstallType")
                && !missingRequirement.contains("productEdition")
                && !missingRequirement.contains("productVersion")) {
                String assetsStr = assetNames.size() == 1 ? assetNames.iterator().next() : assetNames.toString();
                String feature = missingRequirement;

                InstallException ie = create(Messages.INSTALL_KERNEL_MESSAGES.getLogMessage(installingAsset ? "ERROR_ASSET_MISSING_DEPENDENT" : "ERROR_MISSING_DEPENDENT",
                                                                                            assetsStr,
                                                                                            feature),
                                             e);
                ie.setData(assetsStr, feature);

                return ie;

            }
        }

        if (missingRequirementWithMaxVersion.getRequirementName().contains("productInstallType") ||
            missingRequirementWithMaxVersion.getRequirementName().contains("productEdition") ||
            missingRequirementWithMaxVersion.getRequirementName().contains("productVersion")) {
            @SuppressWarnings("rawtypes")
            List productMatchers = SelfExtractor.parseAppliesTo(missingRequirementWithMaxVersion.getRequirementName());
            String feature = RepositoryDownloadUtil.getAssetNameFromMassiveResource(missingRequirementWithMaxVersion.getOwningResource());
            String errMsg = "";
            if (InstallUtils.containsIgnoreCase(assetNames, feature)) {
                errMsg = validateProductMatches(feature, productMatchers, installDir, installingAsset);

            } else {
                String assetsStr = assetNames.size() == 1 ? assetNames.iterator().next() : InstallUtils.getFeatureListOutput(assetNames);
                errMsg = validateProductMatches(assetsStr, feature, productMatchers, installDir, installingAsset);
            }
            if (!errMsg.isEmpty()) {
                return create(errMsg, e, InstallException.NOT_VALID_FOR_CURRENT_PRODUCT);
            }
        }

        InstallException ie = create(Messages.INSTALL_KERNEL_MESSAGES.getLogMessage(installingAsset ? "ERROR_FAILED_TO_RESOLVE_ASSETS" : "ERROR_FAILED_TO_RESOLVE_FEATURES",
                                                                                    InstallUtils.getFeatureListOutput(assetNames)),
                                     e);
        ie.setData(assetNames);

        return ie;
    }

    static boolean isNewerVersion(String version, String newestVersion, boolean isEarlyAccess) {

        if (version == null)
            return false;
        String[] versionAry = version.split("\\.");

        if (!!!isEarlyAccess && versionAry[0].length() == 4)
            return false;

        if (isEarlyAccess && versionAry[0].length() != 4)
            return false;

        if (newestVersion == null)
            return true;

        String[] newestVersionAry = newestVersion.split("\\.");
        for (int i = 0; i < versionAry.length; i++) {
            if (Integer.parseInt(versionAry[i]) > Integer.parseInt(newestVersionAry[i]))
                return true;
            else if (Integer.parseInt(versionAry[i]) < Integer.parseInt(newestVersionAry[i]))
                return false;
        }
        return false;

    }

    @SuppressWarnings("rawtypes")
    static String validateProductMatches(String feature, List productMatchers, File installDir, boolean installingAsset) {
        return validateProductMatches(feature, null, productMatchers, installDir, installingAsset);
    }

    @SuppressWarnings("rawtypes")
    static String validateProductMatches(String feature, String dependency, List productMatchers, File installDir, boolean installingAsset) {
        wlp.lib.extract.ReturnCode validInstallRC = SelfExtractor.validateProductMatches(installDir, productMatchers);
        String errMsg = "";
        if (validInstallRC != wlp.lib.extract.ReturnCode.OK) {
            String productName = InstallConstants.PRODUCTNAME;
            Object[] params = validInstallRC.getParameters();
            if (validInstallRC.getMessageKey().equals("invalidVersion") || validInstallRC.getMessageKey().equals("invalidEdition")) {
                int productVersion = 0;
                int matchVersion = 1;
                int productEdition = 2;
                int matchEdition = 3;

                if (validInstallRC.getMessageKey().equals("invalidEdition")) {
                    productVersion = 2;
                    matchVersion = 3;
                    productEdition = 0;
                    matchEdition = 1;
                }
                String version = (String) params[productVersion];
                String appliesToVersion = (String) params[matchVersion];
                if (appliesToVersion == null || version.equals(appliesToVersion))
                    appliesToVersion = "";

                @SuppressWarnings("unchecked")
                List<String> editions = (List<String>) params[matchEdition];
                String edition = InstallUtils.getEditionName((String) params[productEdition]);
                StringBuilder applicableProducts = new StringBuilder();
                applicableProducts.append(InstallUtils.NEWLINE);

                //no editions requirement
                if (editions == null || editions.size() == 0) {
                    editions = InstallUtils.ALL_EDITIONS;
                }

                if (((String) params[productEdition]).equalsIgnoreCase("Liberty Early Access")) {
                    editions = new ArrayList<String>();
                    editions.add("Early Access");
                }
                Collections.sort(editions);
                Map<String, String> productMap = new HashMap<String, String>();
                applicableProducts.append(InstallUtils.NEWLINE);
                for (String e : editions) {
                    String editionName = "";
                    editionName = InstallUtils.getEditionName(e);
                    if (!productMap.containsKey(editionName)) {
                        String product = "- " + productName + (editionName.isEmpty() ? "" : " ") + editionName + " " + appliesToVersion;
                        productMap.put(editionName, product);
                        applicableProducts.append(product);
                        applicableProducts.append(InstallUtils.NEWLINE);
                    }
                }
                applicableProducts.append(InstallUtils.NEWLINE);
                //installing asset has invalid product version and/or edition
                if (dependency == null || dependency.isEmpty()) {
                    if (appliesToVersion.equals("")) //installing asset has invalid product edition only
                        errMsg = Messages.INSTALL_KERNEL_MESSAGES.getLogMessage(installingAsset ? "ERROR_ASSET_INVALID_PRODUCT_EDITION" : "ERROR_INVALID_PRODUCT_EDITION",
                                                                                new Object[] { feature, productName, edition, applicableProducts.toString(), productName,
                                                                                               edition });
                    else
                        errMsg = Messages.INSTALL_KERNEL_MESSAGES.getLogMessage(installingAsset ? "ERROR_ASSET_INVALID_PRODUCT_EDITION_VERSION" : "ERROR_INVALID_PRODUCT_EDITION_VERSION",
                                                                                new Object[] { feature, productName, edition, version, applicableProducts.toString() });
                } else
                    errMsg = Messages.INSTALL_KERNEL_MESSAGES.getLogMessage(installingAsset ? "ERROR_ASSET_DEPENDENT_INVALID_VERSION_EDITION" : "ERROR_DEPENDENT_INVALID_VERSION_EDITION",
                                                                            new Object[] { feature, dependency, productName, edition, version,
                                                                                           applicableProducts.toString() });

            } else if (validInstallRC.getMessageKey().equals("invalidInstallType")) {
                errMsg = Messages.INSTALL_KERNEL_MESSAGES.getLogMessage(installingAsset ? "ERROR_ASSET_INVALID_PRODUCT_INSTALLTYPE" : "ERROR_INVALID_PRODUCT_INSTALLTYPE",
                                                                        new Object[] { feature, params[0], params[1] });
            }

            if (errMsg.isEmpty()) {
                errMsg = Messages.INSTALL_KERNEL_MESSAGES.getLogMessage(installingAsset ? "ERROR_ASSET_NOT_APPLICABLE" : "ERROR_FEATURE_NOT_APPLICABLE", feature,
                                                                        installDir.getAbsolutePath(),
                                                                        validInstallRC.getErrorMessage());
            }
        }
        return errMsg;
    }

    private static InstallException create(String msg, Exception e, int rc) {
        InstallException ie = new InstallException(msg, e, rc);
        return ie;
    }
}
