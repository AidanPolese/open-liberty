package com.ibm.aries.buildtasks.semantic.versioning.model;

import org.osgi.framework.wiring.BundleWiring;

public class PkgInfo extends VersionedEntity {
    final BundleWiring owner; //null if loaded from xml =).
    final String fromBundle;
    final String fromBundleVersion;

    public PkgInfo(String name, String version, BundleWiring owner) {
        this(name, version, null, null, owner);
    }

    public PkgInfo(String name, String version, String fromBundle, String fromBundleVersion, BundleWiring owner) {
        super(name, version);
        this.owner = owner;
        if (name.indexOf('@') != -1 || (version != null && version.indexOf('@') != -1)) {
            throw new RuntimeException("Error.. where is the @ coming from??");
        }
        this.fromBundle = fromBundle;
        this.fromBundleVersion = fromBundleVersion;
    }

    public String getFromBundle() {
        return fromBundle;
    }

    public String getFromBundleVersion() {
        return fromBundleVersion;
    }

    public BundleWiring getOwner() {
        return owner;
    }

}