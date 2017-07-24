/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/

package com.ibm.ws.security.wim;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.security.wim.ras.WIMMessageHelper;
import com.ibm.websphere.security.wim.ras.WIMMessageKey;
import com.ibm.ws.security.wim.util.StringUtil;
import com.ibm.wsspi.security.wim.exception.WIMException;

/**
 * An abstract implementation of a {@link RepositoryWrapper}.
 */
abstract class AbstractRepositoryWrapper implements RepositoryWrapper {

    private static final TraceComponent tc = Tr.register(AbstractRepositoryWrapper.class);

    private final String repositoryId;

    public AbstractRepositoryWrapper(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    @Override
    public Repository getRepository() throws WIMException {
        return null;
    }

    abstract protected RepositoryConfig getRepositoryConfig();

    @Override
    public void clear() {}

    @Override
    public Map<String, String> getRepositoryBaseEntries() {
        return getRepositoryConfig().getRepositoryBaseEntries();
    }

    @Override
    public Set<String> getRepositoryGroups() {
        String[] repositoriesForGroups = getRepositoryConfig().getRepositoriesForGroups();
        if (repositoriesForGroups != null && repositoriesForGroups.length > 0) {
            return new HashSet<String>(Arrays.asList(repositoriesForGroups));
        }
        return Collections.emptySet();
    }

    @Override
    public int isUniqueNameForRepository(String uniqueName, boolean isDn) throws WIMException {
        int repo = -1;
        if (isDn) {
            Collection<String> baseEntryList = getRepositoryBaseEntries().keySet();
            if (baseEntryList.size() == 0) {
                throw new WIMException(WIMMessageKey.MISSING_BASE_ENTRY, Tr.formatMessage(
                                                                                          tc,
                                                                                          WIMMessageKey.MISSING_BASE_ENTRY,
                                                                                          WIMMessageHelper.generateMsgParms(repositoryId)));
            }
            int uLength = uniqueName.length();
            for (String baseEntry : baseEntryList) {
                int baseEntryLength = baseEntry.length();
                if (baseEntryLength == 0 && repo == -1) {
                    //Previously we matched the root to everything, but now we are looking for a better match
                    repo = 0;
                } else if ((uLength == baseEntryLength) && uniqueName.equalsIgnoreCase(baseEntry)) {
                    //We found an exact match so return with highest priority
                    return Integer.MAX_VALUE;
                } else if ((uLength > baseEntryLength) && (StringUtil.endsWithIgnoreCase(uniqueName, "," + baseEntry))) {
                    //We found a match, but we need to check if it is better than our currently best match
                    if (repo < baseEntryLength) {
                        repo = baseEntryLength;
                    }
                }
            }
        }
        return repo;
    }
}
