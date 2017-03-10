/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.metatype;

import java.util.Locale;
import java.util.Set;

import org.osgi.framework.Bundle;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 *
 */
@Trivial
public class SchemaGeneratorOptions {

    private Bundle[] bundles;
    private Locale locale;
    private String encoding;
    private Set<String> ignoredPids;
    private boolean isRuntime = false;

    private String schemaVersion;
    private String outputVersion;

    public Bundle[] getBundles() {
        return bundles;
    }

    public void setBundles(Bundle[] bundles) {
        this.bundles = bundles;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public Set<String> getIgnoredPids() {
        return ignoredPids;
    }

    public void setIgnoredPids(Set<String> ignoredPids) {
        this.ignoredPids = ignoredPids;
    }

    public boolean isRuntime() {
        return this.isRuntime;
    }

    public void setIsRuntime(boolean value) {
        this.isRuntime = value;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public String getOutputVersion() {
        return outputVersion;
    }

    public void setSchemaVersion(String v) {
        schemaVersion = v;
    }

    public void setOutputVersion(String v) {
        outputVersion = v;
    }

}
