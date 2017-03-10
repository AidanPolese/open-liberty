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
package com.ibm.ws.kernel.provisioning;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import com.ibm.ws.kernel.boot.cmdline.Utils;
import com.ibm.ws.kernel.boot.internal.BootstrapConstants;

/**
 * Product Extension.
 */
public class ProductExtension {

    public static final String PRODUCT_EXTENSION_DIR = "etc/extensions";

    public static final String PRODUCT_EXTENSIONS_FILE_EXTENSION = ".properties";

    public static final String PRODUCT_EXTENSIONS_INSTALL = "com.ibm.websphere.productInstall";

    public static final String PRODUCT_EXTENSIONS_ID = "com.ibm.websphere.productId";

    private static FileFilter PROPERTIESFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return file.getName().endsWith(PRODUCT_EXTENSIONS_FILE_EXTENSION);
        }
    };

    private static File getExtensionDir(File installDir) {
        return new File(installDir, PRODUCT_EXTENSION_DIR);
    }

    public static List<ProductExtensionInfo> getProductExtensions() {
        return getProductExtensions(Utils.getInstallDir());
    }

    /**
     * Get a list of configured product extensions.
     * 
     * @return
     */
    public static List<ProductExtensionInfo> getProductExtensions(File installDir) {
        ArrayList<ProductExtensionInfo> productList = new ArrayList<ProductExtensionInfo>();

        HashMap<String, Properties> extraProductExtensions = getExtraProductExtensions();
        if (extraProductExtensions != null) {
            ProductExtensionInfo prodInfo;
            for (Entry<String, Properties> entry : extraProductExtensions.entrySet()) {
                String name = entry.getKey();
                Properties featureProperties = entry.getValue();
                String installLocation = featureProperties.getProperty(ProductExtension.PRODUCT_EXTENSIONS_INSTALL);
                String productId = featureProperties.getProperty(ProductExtension.PRODUCT_EXTENSIONS_ID);
                prodInfo = new ProductExtensionInfoImpl(name, productId, installLocation);
                productList.add(prodInfo);
            }
        }

        File productExtensionsDir = getExtensionDir(installDir);
        if (productExtensionsDir.exists()) {
            File[] productPropertiesFiles = productExtensionsDir.listFiles(PROPERTIESFilter);

            // Iterate over all the *.properties files in the product extensions dir.
            for (File file : productPropertiesFiles) {
                String fileName = file.getName();

                // Get the product name.
                String productName = fileName.substring(0, fileName.indexOf(PRODUCT_EXTENSIONS_FILE_EXTENSION));

                // skip a file called just .properties
                if (0 != productName.length()) {
                    if (ExtensionConstants.USER_EXTENSION.equalsIgnoreCase(productName) == false) {
                        // extensions added by the embedder SPI will override extensions of the same name that exist in the install root.
                        if ((extraProductExtensions == null) || (extraProductExtensions.containsKey(productName) == false)) {
                            // Read data in .properties file.
                            ProductExtensionInfo prodInfo;
                            try {
                                prodInfo = loadExtensionInfo(productName, file);
                                if (prodInfo != null)
                                    productList.add(prodInfo);
                            } catch (IOException e) {
                            }
                        }
                    }
                }
            }
        }

        return productList;
    }

    private static HashMap<String, Properties> getExtraProductExtensions() {
        HashMap<String, Properties> extraProductExtensions = null;
        String embededData = System.getProperty(BootstrapConstants.ENV_PRODUCT_EXTENSIONS_ADDED_BY_EMBEDDER);
        if (embededData != null) {
            String[] extensions = embededData.split("\n");
            for (int i = 0; (i < extensions.length) && ((i + 3) <= extensions.length); i = i + 3) {
                Properties props = new Properties();
                props.setProperty("com.ibm.websphere.productId", extensions[i + 1]);
                props.setProperty("com.ibm.websphere.productInstall", extensions[i + 2]);
                if (extraProductExtensions == null) {
                    extraProductExtensions = new HashMap<String, Properties>();
                }
                extraProductExtensions.put(extensions[i], props);
            }
        }
        return extraProductExtensions;
    }

    /**
     * Get a list of configured product extensions.
     * 
     * @return
     */
    public static ProductExtensionInfo getProductExtension(String extensionName) throws IOException {

        ProductExtensionInfo productExtensionInfo = null;
        HashMap<String, Properties> extraProductExtensions = getExtraProductExtensions();
        if (extraProductExtensions != null) {
            Properties featureProperties = extraProductExtensions.get(extensionName);
            if (featureProperties != null) {
                String installLocation = featureProperties.getProperty(PRODUCT_EXTENSIONS_INSTALL);
                String productId = featureProperties.getProperty(PRODUCT_EXTENSIONS_ID);
                productExtensionInfo = new ProductExtensionInfoImpl(extensionName, productId, installLocation);
            }
        }

        if (productExtensionInfo == null) {
            File extensionFile = new File(getExtensionDir(Utils.getInstallDir()), extensionName + ".properties");
            productExtensionInfo = loadExtensionInfo(extensionName, extensionFile);
        }

        return productExtensionInfo;
    }

    private static ProductExtensionInfo loadExtensionInfo(String productName, File extensionFile) throws IOException {
        if (extensionFile.isFile()) {
            Properties featureProperties = new Properties();
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(extensionFile);
                featureProperties.load(fileInputStream);
                String installLocation = featureProperties.getProperty(PRODUCT_EXTENSIONS_INSTALL);
                String productId = featureProperties.getProperty(PRODUCT_EXTENSIONS_ID);

                ServiceFingerprint.put(extensionFile);

                return new ProductExtensionInfoImpl(productName, productId, installLocation);
            } finally {
                Utils.tryToClose(fileInputStream);
            }
        }
        return null;
    }

    /**
     * Product extension information.
     */
    public static class ProductExtensionInfoImpl implements ProductExtensionInfo {
        final String productName;

        final String location;

        final String productId;

        /**
         * Constructor.
         * 
         * @param productName
         * @param location
         * @param productId
         */
        public ProductExtensionInfoImpl(String productName, String productId, String location) {
            this.productName = productName;
            this.productId = productId;
            this.location = location;
        }

        @Override
        public String getName() {
            return productName;
        }

        @Override
        public String getLocation() {
            return location;
        }

        @Override
        public String getProductID() {
            return productId;
        }
    }
}
