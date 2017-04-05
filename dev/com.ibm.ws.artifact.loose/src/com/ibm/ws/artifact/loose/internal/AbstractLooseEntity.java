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

import com.ibm.ws.artifact.loose.internal.LooseArchive.EntryInfo;
import com.ibm.wsspi.artifact.ArtifactContainer;
import com.ibm.wsspi.artifact.ArtifactEntry;
import com.ibm.wsspi.artifact.EnclosedEntity;

public abstract class AbstractLooseEntity implements EnclosedEntity {
    private final LooseArchive root;
    private final String path;
    private String name;
    private final EntryInfo entry;

    public AbstractLooseEntity(LooseArchive looseArchive, EntryInfo ei, String pathAndName) {
        path = pathAndName;
        root = looseArchive;
        entry = ei;
    }

    @Override
    public ArtifactContainer getEnclosingContainer() {
        final String parentPath = PathUtil.getParent(path);
        if ("/".equals(parentPath)) {
            return root;
        } else {
            return root.getEntry(parentPath).convertToContainer();
        }
    }

    public ArtifactEntry getEntryInEnclosingContainer() {
        if ("/".equals(path)) {
            return root.getEntryInEnclosingContainer();
        } else {
            return root.getEntry(path);
        }
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getName() {
        if (name == null) {
            name = PathUtil.getName(path);
        }

        return name;
    }

    protected LooseArchive getParent() {
        return root;
    }

    protected EntryInfo getEntryInfo() {
        return entry;
    }
}