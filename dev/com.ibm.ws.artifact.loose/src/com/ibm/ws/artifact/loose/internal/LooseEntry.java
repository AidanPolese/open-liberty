/*
 * @start_prolog@
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 * 
 * Change activity:
 * 
 * Issue       Date        Name        Description
 * ----------- ----------- --------    ------------------------------------
 */
package com.ibm.ws.artifact.loose.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import com.ibm.ws.artifact.loose.internal.LooseArchive.EntryInfo;
import com.ibm.wsspi.artifact.ArtifactContainer;
import com.ibm.wsspi.artifact.ArtifactEntry;

public class LooseEntry extends AbstractLooseEntity implements ArtifactEntry {

    public LooseEntry(LooseArchive looseArchive, EntryInfo ei, String pathAndName) {
        super(looseArchive, ei, pathAndName);
    }

    @Override
    public ArtifactContainer convertToContainer() {
        return convertToContainer(false);
    }

    @Override
    public ArtifactContainer convertToContainer(boolean localOnly) {
        if (getEntryInfo() == null) {
            return new LooseContainer(getParent(), null, getPath());
        } else {
            return getEntryInfo().createContainer(localOnly, getParent(), getPath());
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (getEntryInfo() == null) {
            return null;
        } else {
            return getEntryInfo().getInputStream(getPath());
        }
    }

    @Override
    public long getSize() {
        if (getEntryInfo() == null) {
            return 0L;
        } else {
            return getEntryInfo().getSize(getPath());
        }
    }

    @Override
    public long getLastModified() {
        if (getEntryInfo() == null) {
            return 0L;
        } else {
            return getEntryInfo().getLastModified(getPath());
        }
    }

    @Override
    public ArtifactContainer getRoot() {
        return getParent();
    }

    /**
     * {@inheritDoc}
     * 
     * @throws MalformedURLException
     */
    @Override
    public URL getResource() {
        EntryInfo entryInfo = getEntryInfo();
        if (entryInfo == null) {
            return null;
        }
        Collection<URL> urls = entryInfo.getURLs(getPath());
        Iterator<URL> urlIterator = urls.iterator();

        URL url = urlIterator.hasNext() ? urlIterator.next() : null;
        return url;
    }

    @Override
    public String getPhysicalPath() {
        /*
         * Use the entry info to get the physical path for this entry. If it's null then it means that this is a purely virtual entry (i.e. isBeneath returned true and matches
         * returned false for a dir entry beneath this one) in which case there is by definition no physical path to return
         */
        EntryInfo entryInfo = getEntryInfo();
        if (entryInfo != null) {
            return entryInfo.getFirstPhysicalPath(getPath());
        } else {
            return null;
        }
    }
}
