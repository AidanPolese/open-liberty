/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.product.utility.extension.ifix.xml;

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "libertyFixMetadata")
public class LibertyProfileMetadataFile implements MetadataOutput {

    @XmlElement(name = "bundles")
    private Bundles bundles;

    @XmlElement(name = "newFeatureManifests")
    private FeatureManifests manifests;

    public LibertyProfileMetadataFile() {
        //no-op
        //default constructor required by jaxb
    }

    public LibertyProfileMetadataFile(List<BundleFile> bundleFiles, Set<FeatureManifestFile> manifestFiles) {
        this.bundles = new Bundles(bundleFiles);
        this.manifests = new FeatureManifests(manifestFiles);
    }

    public Bundles getBundles() {
        return this.bundles;
    }

    public FeatureManifests getManifests() {
        return this.manifests;
    }
}
