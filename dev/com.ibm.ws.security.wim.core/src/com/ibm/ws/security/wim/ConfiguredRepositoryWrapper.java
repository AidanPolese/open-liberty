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

class ConfiguredRepositoryWrapper extends AbstractRepositoryWrapper {

    private final ConfiguredRepository configuredRepository;

    /**
     * @param repositoryId TODO
     * @param repositoryConfiguration
     * @param repositoryFactory
     */
    public ConfiguredRepositoryWrapper(String repositoryId, ConfiguredRepository configuredRepository) {
        super(repositoryId);
        this.configuredRepository = configuredRepository;
    }

    @Override
    public Repository getRepository() {
        return configuredRepository;
    }

    @Override
    protected RepositoryConfig getRepositoryConfig() {
        return configuredRepository;
    }

}