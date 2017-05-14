/************** Begin Copyright - Do not add comments here **************
 *
 * IBM Confidential OCO Source Material
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2012, 2013
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U. S. Copyright Office.
 *
 */

package com.ibm.ws.security.wim;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.security.wim.ras.WIMMessageHelper;
import com.ibm.websphere.security.wim.ras.WIMMessageKey;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.security.registry.CustomRegistryException;
import com.ibm.ws.security.registry.EntryNotFoundException;
import com.ibm.ws.security.registry.RegistryException;
import com.ibm.ws.security.registry.SearchResult;
import com.ibm.ws.security.registry.UserRegistry;
import com.ibm.ws.security.wim.adapter.urbridge.URBridge;
import com.ibm.ws.security.wim.util.StringUtil;
import com.ibm.ws.security.wim.util.UniqueNameHelper;
import com.ibm.wsspi.security.wim.CustomRepository;
import com.ibm.wsspi.security.wim.SchemaConstants;
import com.ibm.wsspi.security.wim.exception.InitializationException;
import com.ibm.wsspi.security.wim.exception.InvalidUniqueNameException;
import com.ibm.wsspi.security.wim.exception.WIMException;
import com.ibm.wsspi.security.wim.model.Entity;
import com.ibm.wsspi.security.wim.model.IdentifierType;
import com.ibm.wsspi.security.wim.model.Root;

/**
 * Single point of contact for core to interact with different repositories
 *
 */
public class RepositoryManager {
    public final static String CLASSNAME = RepositoryManager.class.getName();
    public static final String ACTION_READ = "READ";
    public static final String ACTION_CREATE = "CREATE";
    public static final String ACTION_UPDATE = "UPDATE";
    public static final String ACTION_DELETE = "DELETE";

    private static final TraceComponent tc = Tr.register(RepositoryManager.class);

    private static final String KEY_REGISTRY = "userRegistry";

    private static final String BASE_ENTRY = "registryBaseEntry";

    private final VMMService vmmService;

//    private final HashMap<String, Repository> cachedRepository = new HashMap<String, Repository>();

    interface RepositoryHolder {

        Repository getRepository() throws WIMException;

        void clear();

        Map<String, String> getRepositoryBaseEntries();

        Set<String> getRepositoryGroups();

        boolean isUniqueNameForRepository(String uniqueName, boolean isDn) throws WIMException;
    }

    static class URBridgeHolder implements RepositoryHolder {

        private final String baseEntry;
        private final UserRegistry ur;
        private URBridge urBridge;
        private final Map<String, String> baseEntries;

        public URBridgeHolder(UserRegistry ur, ConfigManager configManager) throws InitializationException {
            String realm = ur.getRealm();
            this.baseEntry = "o=" + realm;
            this.ur = ur;
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(KEY_REGISTRY, ur);
            properties.put(VMMService.KEY_ID, realm);
            properties.put(BASE_ENTRY, baseEntry);
            baseEntries = Collections.singletonMap(baseEntry, realm);

            urBridge = new URBridge(properties, ur, configManager);

        }

        @Override
        public Repository getRepository() {
            return urBridge;
        }

        @Override
        public void clear() {
            if (urBridge != null) {
                urBridge.stopCacheThreads();
            }
            urBridge = null; ///???????
        }

        @Override
        public Map<String, String> getRepositoryBaseEntries() {
            //TODO not clear what value should be????
            return baseEntries;
        }

        @Override
        public Set<String> getRepositoryGroups() {
            return Collections.singleton(urBridge.getRealm());
        }

        @Override
        public boolean isUniqueNameForRepository(String uniqueName, boolean isDn) {
            return baseEntry.equals(uniqueName) || isUserInRealm(uniqueName);
        }

        @FFDCIgnore(Exception.class)
        private boolean isUserInRealm(String uniqueName) {
            try {
                SearchResult result = ur.getUsers(uniqueName, 1);
                if (result != null && result.getList().size() > 0)
                    return true;
            } catch (Exception e) {
            }

            try {
                SearchResult result = ur.getGroups(uniqueName, 1);
                if (result != null && result.getList().size() > 0)
                    return true;
            } catch (Exception e) {
            }
            return false;

        }

    }

    abstract static class AbstractRepositoryHolder implements RepositoryHolder {

        private final String repositoryId;

        public AbstractRepositoryHolder(String repositoryId) {
            this.repositoryId = repositoryId;
        }

        @Override
        public Repository getRepository() throws WIMException {
            // TODO Auto-generated method stub
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
        public boolean isUniqueNameForRepository(String uniqueName, boolean isDn) throws WIMException {
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
                    int nodeLength = baseEntry.length();
                    if (nodeLength == 0) {
                        return true;
                    } else if ((uLength == nodeLength) && uniqueName.equalsIgnoreCase(baseEntry)) {
                        return true;
                    } else if ((uLength > nodeLength) && (StringUtil.endsWithIgnoreCase(uniqueName, "," + baseEntry))) {
                        return true;
                    }
                }
            }
            return false;
        }

    }

    static class ConfiguredRepositoryHolder extends AbstractRepositoryHolder {

        private final ConfiguredRepository configuredRepository;

        /**
         * @param repositoryId TODO
         * @param repositoryConfiguration
         * @param repositoryFactory
         */
        public ConfiguredRepositoryHolder(String repositoryId, ConfiguredRepository configuredRepository) {
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

    static class CustomRepositoryHolder extends AbstractRepositoryHolder {

        private static class CustomRepositoryAdapter implements Repository, RepositoryConfig {
            private final String repositoryId;
            private final CustomRepository customRepository;

            /**
             * @param customRepository
             */
            public CustomRepositoryAdapter(String repositoryId, CustomRepository customRepository) {
                this.repositoryId = repositoryId;
                this.customRepository = customRepository;
            }

            /*
             * (non-Javadoc)
             *
             * @see com.ibm.ws.security.wim.RepositoryConfig#isReadOnly()
             */
            @Override
            public boolean isReadOnly() {
                return false;
//                TODO:
//                return customRepository.isReadOnly();
            }

            /*
             * (non-Javadoc)
             *
             * @see com.ibm.ws.security.wim.RepositoryConfig#resetConfig()
             */
            @Override
            public void resetConfig() {
                //TODO:
//                customRepository.resetConfig();
            }

            /*
             * (non-Javadoc)
             *
             * @see com.ibm.ws.security.wim.RepositoryConfig#getReposId()
             */
            @Override
            public String getReposId() {
                return repositoryId;
            }

            /*
             * (non-Javadoc)
             *
             * @see com.ibm.ws.security.wim.RepositoryConfig#getRepositoryBaseEntries()
             */
            @Override
            public Map<String, String> getRepositoryBaseEntries() {
                return customRepository.getRepositoryBaseEntries();
            }

            /*
             * (non-Javadoc)
             *
             * @see com.ibm.ws.security.wim.RepositoryConfig#getRepositoriesForGroups()
             */
            //TODO WHAT IS THIS SUPPOSED TO MEAN?
            @Override
            public String[] getRepositoriesForGroups() {
                String[] repos = customRepository.getRepositoriesForGroups();
                if (repos == null) {
                    repos = new String[] { repositoryId };
                }
                return repos;
            }

            /*
             * (non-Javadoc)
             *
             * @see com.ibm.ws.security.wim.Repository#get(com.ibm.wsspi.security.wim.model.Root)
             */
            @Override
            public Root get(Root root) throws WIMException {
                return setRepositoryId(customRepository.get(root));
            }

            /*
             * (non-Javadoc)
             *
             * @see com.ibm.ws.security.wim.Repository#search(com.ibm.wsspi.security.wim.model.Root)
             */
            @Override
            public Root search(Root root) throws WIMException {
                return setRepositoryId(customRepository.search(root));
            }

            /*
             * (non-Javadoc)
             *
             * @see com.ibm.ws.security.wim.Repository#login(com.ibm.wsspi.security.wim.model.Root)
             */
            @Override
            public Root login(Root root) throws WIMException {
                return setRepositoryId(customRepository.login(root));
            }

            /*
             * (non-Javadoc)
             *
             * @see com.ibm.ws.security.wim.Repository#getRealm()
             */
            @Override
            public String getRealm() {
                return customRepository.getRealm();
            }

            /*
             * (non-Javadoc)
             *
             * @see com.ibm.ws.security.wim.Repository#delete(com.ibm.wsspi.security.wim.model.Root)
             */
            @Override
            public Root delete(Root root) throws WIMException {
                return setRepositoryId(customRepository.delete(root));
            }

            /*
             * (non-Javadoc)
             *
             * @see com.ibm.ws.security.wim.Repository#create(com.ibm.wsspi.security.wim.model.Root)
             */
            @Override
            public Root create(Root root) throws WIMException {
                return setRepositoryId(customRepository.create(root));
            }

            /*
             * (non-Javadoc)
             *
             * @see com.ibm.ws.security.wim.Repository#update(com.ibm.wsspi.security.wim.model.Root)
             */
            @Override
            public Root update(Root root) throws WIMException {
                return setRepositoryId(customRepository.update(root));
            }

            /**
             * @param root
             * @return
             */
            private Root setRepositoryId(Root root) {
                for (Entity entity : root.getEntities()) {
                    IdentifierType identifier = entity.getIdentifier();
                    if (identifier != null) {
                        identifier.setRepositoryId(repositoryId);
                    }
                }
                return root;
            }

        }

        private final CustomRepositoryAdapter repository;

        /**
         * @param repositoryId TODO
         * @param repositoryConfiguration
         * @param repositoryFactory
         */
        public CustomRepositoryHolder(String repositoryId, CustomRepository customRepository) {
            super(repositoryId);
            this.repository = new CustomRepositoryAdapter(repositoryId, customRepository);
        }

        @Override
        public Repository getRepository() {
            return repository;
        }

        /*
         * (non-Javadoc)
         *
         * @see com.ibm.ws.security.wim.RepositoryManager.AbstractRepositoryHolder#getRepositoryConfig()
         */
        @Override
        protected RepositoryConfig getRepositoryConfig() {
            return repository;
        }

    }

    private final Map<String, RepositoryHolder> repositories = new ConcurrentHashMap<String, RepositoryHolder>();

    public RepositoryManager(VMMService service) {
        vmmService = service;
    }

    void addConfiguredRepository(String repositoryId, ConfiguredRepository configuredRepository) {
        RepositoryHolder repositoryHolder = new ConfiguredRepositoryHolder(repositoryId, configuredRepository);
        repositories.put(repositoryId, repositoryHolder);
    }

    void addCustomRepository(String repositoryId, CustomRepository customRepository) {
        RepositoryHolder repositoryHolder = new CustomRepositoryHolder(repositoryId, customRepository);
        repositories.put(repositoryId, repositoryHolder);
    }

    void addUserRegistry(UserRegistry userRegistry) {
        try {
            URBridgeHolder repositoryHolder = new URBridgeHolder(userRegistry, vmmService.getConfigManager());
            repositories.put(userRegistry.getRealm(), repositoryHolder);
        } catch (InitializationException e) {
            //TODO will occur on lookup when this is made lazy.
        }
    }

    void removeRepositoryHolder(String id) {
        RepositoryHolder repositoryHolder = repositories.remove(id);
        if (repositoryHolder != null) {
            repositoryHolder.clear();
        }
    }

    /**
     * {@inheritDoc} This lookup is done for all registered RepositoryFactory
     * instances. Please note that this method does not use the configuration
     * data for the RepositoryService for this lookup.
     */
    public Repository getRepository(String instanceId) throws WIMException {
        RepositoryHolder repositoryHolder = repositories.get(instanceId);
        if (repositoryHolder != null) {
            return repositoryHolder.getRepository();
        }
        return null;
//        ConcurrentServiceReferenceMap<String, RepositoryConfiguration> configs = vmmService.getRepositoryConfigurations();
//
//        if (tc.isDebugEnabled())
//            Tr.debug(tc, "Config = " + configs);
//        RepositoryConfig config = configs.getService(instanceId);
//
//        if (config != null) {
//            RepositoryFactory factory = getRepositoryFactory(config.getType()); //LATEST ISSUE HERE
//
//            // If the RepositoryConfiguration is a URRepositoryConfiguration, set the config manager
//            // if (config instanceof URRepositoryConfiguration)
//            //     ((URRepositoryConfiguration) config).setConfigManager(vmmService.getConfigManager());
//            Repository repos = config.getRepository(factory);
//            return repos;
//        } else {
//            Set<Object> userRegistries = vmmService.getUserRegistries();
//
//            Iterator<Object> URIterator = userRegistries.iterator();
//            while (URIterator.hasNext()) {
//                Object ur = URIterator.next();
//                String realm = getRealm(ur);
//
//                if (realm != null && realm.equals(instanceId)) {
//                    // Check if we have a cached instance.
//                    if (cachedRepository.containsKey(instanceId)) {
//                        return cachedRepository.get(instanceId);
//                    } else {
//                        Map<String, Object> properties = new HashMap<String, Object>();
//                        properties.put(KEY_REGISTRY, ur);
////                        properties.put(KEY_CONFIG_MANAGER, vmmService.getConfigManager());
//                        properties.put(VMMService.KEY_ID, realm);
//                        properties.put(BASE_ENTRY, "o=" + realm);
//
//                        Repository repos = new URBridge(properties, (UserRegistry) ur, vmmService.getConfigManager());
//                        cachedRepository.put(instanceId, repos);
//                        return repos;
//                    }
//                }
//            }
//        }
//        return null;
    }

    /**
     * @param ur
     * @return
     * @throws RemoteException
     * @throws CustomRegistryException
     */
    private String getRealm(Object ur) {
        if (ur instanceof com.ibm.ws.security.registry.UserRegistry)
            return ((com.ibm.ws.security.registry.UserRegistry) ur).getRealm();
        else
            return null;
    }

    public Repository getTargetRepository(String uniqueName) throws WIMException {
        String reposId = getRepositoryIdByUniqueName(uniqueName);
        Repository repos = getRepository(reposId);
        return repos;
    }

    public String getRepositoryId(String uniqueName) throws WIMException {
        String reposId = getRepositoryIdByUniqueName(uniqueName);
        return reposId;
    }

    /**
     * Returns the id of the repository to which the uniqueName belongs to.
     *
     * @throws InvalidUniqueNameException
     */
    protected String getRepositoryIdByUniqueName(String uniqueName) throws WIMException {
        boolean isDn = UniqueNameHelper.isDN(uniqueName) != null;
        if (isDn)
            uniqueName = UniqueNameHelper.getValidUniqueName(uniqueName).trim();

        for (Map.Entry<String, RepositoryHolder> entry : repositories.entrySet()) {
            if (entry.getValue().isUniqueNameForRepository(uniqueName, isDn)) {
                return entry.getKey();
            }
        }

        throw new InvalidUniqueNameException(WIMMessageKey.ENTITY_NOT_IN_REALM_SCOPE, Tr.formatMessage(
                                                                                                       tc,
                                                                                                       WIMMessageKey.ENTITY_NOT_IN_REALM_SCOPE,
                                                                                                       WIMMessageHelper.generateMsgParms(uniqueName, "defined")));
    }

    public Map<String, List<String>> getRepositoriesBaseEntries() {
        Map<String, List<String>> reposNodesMap = new HashMap<String, List<String>>();

        for (Map.Entry<String, RepositoryHolder> entry : repositories.entrySet()) {
            reposNodesMap.put(entry.getKey(), new ArrayList<String>(entry.getValue().getRepositoryBaseEntries().keySet()));
        }
        return reposNodesMap;

//        ConcurrentServiceReferenceMap<String, RepositoryConfiguration> configs = vmmService.getRepositoryConfigurations();
//        Set<Object> userRegistries = vmmService.getUserRegistries();
//        if ((configs == null || configs.isEmpty()) && (userRegistries == null || userRegistries.isEmpty())) {
//            return reposNodesMap;
//        }
//
//        if (configs != null && !configs.isEmpty()) {
//            Iterator<RepositoryConfiguration> itr = configs.getServices();
//            while (itr.hasNext()) {
//                RepositoryConfig config = itr.next();
//                List<String> baseEntries = new ArrayList<String>();
//                baseEntries.addAll(config.getRepositoryBaseEntries().keySet());
//                reposNodesMap.put(config.getReposId(), baseEntries); //TODO handle null baseEntries
//            }
//        }
//
//        if (userRegistries != null && !userRegistries.isEmpty()) {
//            Iterator<Object> URIterator = userRegistries.iterator();
//            while (URIterator.hasNext()) {
//                Object ur = URIterator.next();
//                String realm = getRealm(ur);
//
//                if (realm != null) {
//                    String baseEntry = "o=" + realm;
//                    List<String> baseEntries = new ArrayList<String>();
//                    baseEntries.add(baseEntry);
//                    String reposId = realm;
//                    reposNodesMap.put(reposId, baseEntries);
//                }
//            }
//        }
//
//        return reposNodesMap;
    }

    public Map<String, String> getRepositoryBaseEntries(String reposId) throws WIMException {
        RepositoryHolder repositoryHolder = repositories.get(reposId);
        if (repositoryHolder != null) {
            return repositoryHolder.getRepositoryBaseEntries();
        }
        return Collections.emptyMap();
//        List<String> baseEntries = new ArrayList<String>();
//
//        RepositoryConfig config = vmmService.getRepositoryConfigurations().getService(reposId);
//        if (config != null)
//            baseEntries.addAll(config.getRepositoryBaseEntries().keySet());
//        else {
//            Set<Object> userRegistries = vmmService.getUserRegistries();
//
//            if (userRegistries != null && !userRegistries.isEmpty()) {
//                Iterator<Object> URIterator = userRegistries.iterator();
//                urSearch: while (URIterator.hasNext()) {
//                    Object ur = URIterator.next();
//                    String realm = getRealm(ur);
//
//                    if (realm != null && realm.equals(reposId)) {
//                        String baseEntry = "o=" + realm;
//                        baseEntries.add(baseEntry);
//                        break urSearch;
//                    }
//                }
//            }
//        }
//
//        return baseEntries;
    }

    public List<String> getRepoIds() throws WIMException {
        return new ArrayList<String>(repositories.keySet());
//        List<String> repoIds = new ArrayList<String>();
//        ConcurrentServiceReferenceMap<String, RepositoryConfiguration> configs = vmmService.getRepositoryConfigurations();
//
//        Set<Object> userRegistries = vmmService.getUserRegistries();
//
//        if ((configs == null || configs.isEmpty()) && (userRegistries == null || userRegistries.isEmpty())) {
//            return repoIds;
//        }
//
//        Iterator<RepositoryConfiguration> itr = configs.getServices();
//        while (itr.hasNext()) {
//            RepositoryConfig config = itr.next();
//            // If repository Id is not already added then add it to the list
//            if (!repoIds.contains(config.getReposId())) {
//                repoIds.add(config.getReposId());
//            }
//        }
//
//        Iterator<Object> URIterator = userRegistries.iterator();
//        while (URIterator.hasNext()) {
//            Object ur = URIterator.next();
//            // Default the repository Id to its configured realm
//            String repoId = getRealm(ur);
//
//            // If repository Id is not already added then add it to the list
//            if (!repoIds.contains(repoId)) {
//                repoIds.add(repoId);
//            }
//        }
//
//        return repoIds;
    }

    public int getNumberOfRepositories() throws WIMException {
        return getRepoIds().size();
    }

    /**
     * Returns true if the baseEntries list contains the baseEntry. The
     * comparison is done using equalsIgnoreCase() to make sure that Turkish
     * locale I's are handled properly.
     **/
    public static boolean matchBaseEntryIgnoreCase(List<String> baseEntries, String baseEntry) {
        boolean result = false;
        if (baseEntries != null && baseEntry != null) {
            for (int i = 0; i < baseEntries.size(); i++) {
                if (baseEntry.equalsIgnoreCase(baseEntries.get(i))) {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

    public boolean isPropertyJoin() {
        return false;
    }

    public boolean isEntryJoin() {
        return false;
    }

    /**
     * sorts a list of base entries into the repositories that provide them.
     *
     * @param realmBaseEntries
     * @return map of repository id to base entries that that repository provides.
     * @throws WIMException
     */
    //TODO doing it everytime instead of prepopulated might cause perf issue. look at alternatives where it can be done
    public Map<String, List<String>> getBaseEntriesForRepos(String[] realmBaseEntries) throws WIMException {
        Map<String, List<String>> reposBaseEntries = new HashMap<String, List<String>>();
        for (String baseEntryName : realmBaseEntries) {
            String reposId = getRepositoryIdByUniqueName(baseEntryName);
            List<String> baseEntryList = reposBaseEntries.get(reposId);
            if (baseEntryList == null)
                baseEntryList = new ArrayList<String>();
            baseEntryList.add(baseEntryName);
            reposBaseEntries.put(reposId, baseEntryList);
        }
        return reposBaseEntries;
    }

    //TODO not tested.
    public boolean isReadOnly(String reposId) throws WIMException {
        // return readOnlyMap.get(reposId).booleanValue();
        // As property is not defined in the metatype, always return false.
        return false;
    }

    //TODO not tested.
    public boolean isSortingSupported(String reposId) {
        // return sortSupportMap.get(reposId).booleanValue();
        // As property is not defined in the metatype, always return false.
        return false;
    }

    private Map<String, Set<String>> getRepositoriesForGroup() {
        //TODO this appears to be backwards, but also seems to match the original (untested) code.
        //Perhaps the map needs to be inverted?
        //On the other hand getRepositoriesForGroupMembers seems to return the inverted map.
        Map<String, Set<String>> repositoriesForGroup = new HashMap<String, Set<String>>();
        for (Map.Entry<String, RepositoryHolder> entry : repositories.entrySet()) {
            repositoriesForGroup.put(entry.getKey(), entry.getValue().getRepositoryGroups());
        }
        return repositoriesForGroup;

//        ConcurrentServiceReferenceMap<String, RepositoryConfiguration> configs = vmmService.getRepositoryConfigurations();
//        Set<Object> userRegistries = vmmService.getUserRegistries();
//
//        if ((configs == null || configs.isEmpty()) && (userRegistries == null || userRegistries.isEmpty())) {
//            return repositoriesForGroup;
//        }
//
//        Iterator<RepositoryConfiguration> itr = configs.getServices();
//        while (itr.hasNext()) {
//            RepositoryConfig config = itr.next();
//            // TODO not tested
//            String[] reposForGroups = config.getRepositoriesForGroups();
//            repositoriesForGroup.put(config.getReposId(), new HashSet<String>());
//
//            if (reposForGroups != null && reposForGroups.length > 0) {
//                for (int k = 0; k < reposForGroups.length; k++) {
//                    String grpReposId = reposForGroups[k].trim();
//                    Set<String> grpReposIdSet = repositoriesForGroup.get(config.getReposId());
//
//                    grpReposIdSet.add(grpReposId);
//                    repositoriesForGroup.put(config.getReposId(), grpReposIdSet);
//                }
//            }
//        }
//
//        Iterator<Object> URIterator = userRegistries.iterator();
//        while (URIterator.hasNext()) {
//            Object ur = URIterator.next();
//            // Default the repository Id to its configured realm
//            String repoId = getRealm(ur);
//
//            HashSet<String> repositoryIdForGroup = new HashSet<String>();
//            repositoryIdForGroup.add(repoId);
//            repositoriesForGroup.put(repoId, repositoryIdForGroup);
//        }
//
//        return repositoriesForGroup;
    }

    //TODO not tested.
    public boolean isCrossRepositoryGroupMembership(String reposID) throws WIMException {

        Map<String, Set<String>> repositoriesForGroup = getRepositoriesForGroup();

        int numOfReposForGrp = repositoriesForGroup.get(reposID).size();
        if (numOfReposForGrp > 1) {
            return true;
        }
        if (numOfReposForGrp == 1) {
            String grpReposUUID = repositoriesForGroup.get(reposID).iterator().next();
            if (!reposID.equals(grpReposUUID)) {
                return true;
            }
        }

        return false;
    }

    //TODO not tested.
    public Set<String> getRepositoriesForGroupMembership(String repositoryId) throws WIMException {
        RepositoryHolder repositoryHolder = repositories.get(repositoryId);
        if (repositoryHolder != null) {
            return repositoryHolder.getRepositoryGroups();
        }
        return null;
//        return getRepositoriesForGroup().get(reposID);
    }

    private Map<String, Set<String>> getRepositoriesForGroupMembers() {
        Map<String, Set<String>> groupToRepositoryId = new HashMap<String, Set<String>>();

        for (Map.Entry<String, RepositoryHolder> entry : repositories.entrySet()) {
            String repositoryid = entry.getKey();
            Set<String> groups = entry.getValue().getRepositoryGroups();
            for (String group : groups) {
                Set<String> repositoryIds = groupToRepositoryId.get(group);
                if (repositoryIds == null) {
                    repositoryIds = new HashSet<String>();
                    groupToRepositoryId.put(group, repositoryIds);
                }
                repositoryIds.add(repositoryid);
            }
        }
        return groupToRepositoryId;
//        ConcurrentServiceReferenceMap<String, RepositoryConfiguration> configs = vmmService.getRepositoryConfigurations();
//        if (configs == null || configs.isEmpty()) {
//            return repositoriesForGroupMembers;
//        }
//
//        Iterator<RepositoryConfiguration> itr = configs.getServices();
//        while (itr.hasNext()) {
//            RepositoryConfig config = itr.next();
//            // TODO not tested
//            String[] reposForGroups = config.getRepositoriesForGroups();
//
//            if (reposForGroups != null && reposForGroups.length > 0) {
//                for (int k = 0; k < reposForGroups.length; k++) {
//                    String grpReposId = reposForGroups[k].trim();
//
//                    Set<String> mbrReposIdSet = repositoriesForGroupMembers.get(grpReposId);
//                    if (mbrReposIdSet == null) {
//                        mbrReposIdSet = new HashSet<String>();
//                    }
//                    mbrReposIdSet.add(config.getReposId());
//                    repositoriesForGroupMembers.put(grpReposId, mbrReposIdSet);
//                }
//            }
//        }
//        return repositoriesForGroupMembers;
    }

    //TODO not tested.
    public boolean canGroupAcceptMember(String grpReposId, String mbrReposId) {
        Map<String, Set<String>> repositoriesForGroupMembers = getRepositoriesForGroupMembers();

        if (repositoriesForGroupMembers != null) {
            Set<String> mbrReposIdSet = repositoriesForGroupMembers.get(grpReposId);
            if (mbrReposIdSet != null) {
                return mbrReposIdSet.contains(mbrReposId);
            }
        }
        return false;
    }

    /**
     *
     */
    public void clearAllCachedURRepositories() {
        for (RepositoryHolder repositoryHolder : repositories.values()) {
            repositoryHolder.clear();
        }
    }

    /**
     * @param uniqueName
     * @return
     */
    @FFDCIgnore(Exception.class)
    public List<String> getFederationUREntityType(String data) {
        for (RepositoryHolder rh : repositories.values()) {
            if (rh instanceof URBridgeHolder) {
                UserRegistry ur = ((URBridgeHolder) rh).ur;

                try {
                    SearchResult result = ur.getUsers(data, 1);
                    if (result != null && result.getList().size() > 0) {
                        ArrayList<String> returnValue = new ArrayList<String>();
                        returnValue.add(SchemaConstants.DO_PERSON_ACCOUNT);
                        returnValue.add(data);
                        return returnValue;
                    }
                } catch (Exception e) {
                }

                try {
                    SearchResult result = ur.getGroups(data, 1);
                    if (result != null && result.getList().size() > 0) {
                        ArrayList<String> returnValue = new ArrayList<String>();
                        returnValue.add(SchemaConstants.DO_GROUP);
                        returnValue.add(data);
                        return returnValue;
                    }
                } catch (Exception e) {
                }

                try {
                    String result = ur.getUserSecurityName(data);
                    if (result != null) {
                        ArrayList<String> returnValue = new ArrayList<String>();
                        returnValue.add(SchemaConstants.DO_PERSON_ACCOUNT);
                        returnValue.add(result);
                        return returnValue;
                    }
                } catch (Exception e) {
                }

                try {
                    String result = ur.getGroupSecurityName(data);
                    if (result != null) {
                        ArrayList<String> returnValue = new ArrayList<String>();
                        returnValue.add(SchemaConstants.DO_GROUP);
                        returnValue.add(result);
                        return returnValue;
                    }
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

}
