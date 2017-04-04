/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.classloading.internal;

import java.io.File;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import com.ibm.ws.artifact.ExtractableArtifactEntry;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.Entry;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.adaptable.module.adapters.EntryAdapter;
import com.ibm.wsspi.artifact.ArtifactEntry;
import com.ibm.wsspi.artifact.overlay.OverlayContainer;

@Component(service = EntryAdapter.class,
           configurationPolicy = ConfigurationPolicy.OPTIONAL,
           immediate = true,
           property = { "service.vendor=IBM", "toType=com.ibm.ws.classloading.internal.NativeLibrary" })
public class NativeLibraryAdapter implements EntryAdapter<NativeLibrary> {
    @Override
    public NativeLibrary adapt(Container root, OverlayContainer rootOverlay, ArtifactEntry artifactEntry, Entry entryToAdapt) throws UnableToAdaptException {
        try {
            final File lib = getFileForLibraryEntry(artifactEntry);
            if (lib != null) {
                return new NativeLibrary() {
                    @Override
                    public File getLibraryFile() {
                        return lib;
                    }
                };
            }
        } catch (IOException e) {
            throw new UnableToAdaptException(e);
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public static File getFileForLibraryEntry(ArtifactEntry artifactEntry) throws IOException {
        File lib = null;
        if (artifactEntry.getPhysicalPath() != null) {
            lib = new File(artifactEntry.getPhysicalPath());
        } else if (artifactEntry instanceof ExtractableArtifactEntry) {
            ExtractableArtifactEntry eae = (ExtractableArtifactEntry) artifactEntry;
            lib = eae.extract();
            if (lib != null) {
                // Some operating systems require native libraries to be executable.
                if (System.getSecurityManager() == null)
                    lib.setExecutable(true);
                else {
                    final File finalLib = lib;
                    AccessController.doPrivileged(new PrivilegedAction<Void>() {
                        @Override
                        public Void run() {
                            finalLib.setExecutable(true);
                            return null;
                        }
                    });
                }
            }
        }
        return lib;
    }
}
