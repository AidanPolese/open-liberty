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
package com.ibm.websphere.collective.repository;

import java.io.IOException;
import java.util.Map;

/**
 * The AdminMetadataManagerMBean provides the administrative metadata MBean operations to add, retrieve and remove metadata and the metadata includes
 * tags, owner, contact and note data. The metadata information is stored in the collective repository.
 */
public interface AdminMetadataManagerMBean {

    /**
     * A String representing the {@link javax.management.ObjectName} that this MBean maps to.
     */
    String OBJECT_NAME = "WebSphere:feature=collectiveController,type=AdminMetadataManager,name=AdminMetadataManager";

    /**
     * Create administrative metadata for the given resource type, identity and metadata map.
     * <p>
     * Any metadata that is not identified with an expected map key as defined below, is ignored with no exception.
     * <p>
     * A map entry that has an expected key, but the data for that key is not as expected will result in an
     * IllegalArgumentException, but only after all other properly formed keys and their data have been processed. In the
     * case that there are multiple instances of expected keys with improper data, only the first such occurrence is
     * thrown in the IllegalArgumentException.
     * 
     * @param resourceType resource type includes server, application, cluster, host and runtime.
     * @param identity specifies the unique resource for this action. This might be:
     *            <ul>
     *            <li>server identity - {hostName},{userDir},{serverName}</li>
     *            <li>cluster identity - {clusterName}</li>
     *            <li>application identity - {hostName},{userDir},{serverName},{applicationName}</li>
     *            <li>clustered application identity - {clusterName},{applicationName}</li>
     *            <li>host identity - {hostName}</li>
     *            <li>runtime identity - {hostName},{installDir}</li>
     *            </ul>
     * @param metadata a map of admin metadata. This is a map with the possible keys: tags, owner, contacts and note. The value for each key
     *            is as follows:
     *            <ul>
     *            <li>tags - an array of tag Strings (String[])</li>
     *            <li>owner - a simple String field (String)</li>
     *            <li>contacts - an array of contact Strings (String[])</li>
     *            <li>note - a simple String field (String)</li>
     *            </ul>
     * 
     * @throws IOException If there was a problem accessing the repository for the request
     * @throws IllegalArgumentException
     */
    void addAdminMetadata(String resourceType, String identity, Map metadata) throws IOException;

    /**
     * Deploy administrative metadata for the given resource type, identity and metadata map. It replaces all metadata
     * previously created by this method.
     * <p>
     * Any metadata that is not identified with an expected map key as defined below, is ignored with no exception.
     * <p>
     * A map entry that has an expected key, but the data for that key is not as expected will result in an
     * IllegalArgumentException, but only after all other properly formed keys and their data have been processed. In the
     * case that there are multiple instances of expected keys with improper data, only the first such occurrence is
     * thrown in the IllegalArgumentException.
     * 
     * @param resourceType resource type includes server, application, cluster, host and runtime.
     * @param identity specifies the unique resource for this action. This might be:
     *            <ul>
     *            <li>server identity - {hostName},{userDir},{serverName}</li>
     *            <li>cluster identity - {clusterName}</li>
     *            <li>application identity - {hostName},{userDir},{serverName},{applicationName}</li>
     *            <li>clustered application identity - {clusterName},{applicationName}</li>
     *            <li>host identity - {hostName}</li>
     *            <li>runtime identity - {hostName},{installDir}</li>
     *            </ul>
     * @param metadata a map of admin metadata. This is a map with the possible keys: tags, owner, contacts and note. The value for each key
     *            is as follows:
     *            <ul>
     *            <li>tags - an array of tag Strings for the identity (String[])</li>
     *            <li>owner - a simple String indicating the owner of the identity (String)</li>
     *            <li>contacts - an array of Strings that are contact information for the identity (String[])</li>
     *            <li>note - a simple String that is free-form note text for the identity (String)</li>
     *            </ul>
     * 
     * @throws IOException If there was a problem accessing the repository for the request
     * @throws IllegalArgumentException
     */
    void deployAdminMetadata(String resourceType, String identity, Map metadata) throws IllegalArgumentException, IOException;

    /**
     * Retrieve all administrative metadata for the given resource type and identity.
     * 
     * @param resourceType resource type includes server, application, cluster, host and runtime.
     * @param identity specifies the unique resource for this action. This might be:
     *            <ul>
     *            <li>server identity - {hostName},{userDir},{serverName}</li>
     *            <li>cluster identity - {clusterName}</li>
     *            <li>application identity - {hostName},{userDir},{serverName},{applicationName}</li>
     *            <li>clustered application identity - {clusterName},{applicationName}</li>
     *            <li>host identity - {hostName}</li>
     *            <li>runtime identity - {hostName},{installDir}</li>
     *            </ul>
     * @return a map of admin metadata. This is a map with the possible keys: tags, owner, contacts and note. The value for each key
     *         is as follows:
     *         <ul>
     *         <li>tags - an array of tag Strings for the identity (String[])</li>
     *         <li>owner - a simple String indicating the owner of the identity (String)</li>
     *         <li>contacts - an array of Strings that are contact information for the identity (String[])</li>
     *         <li>note - a simple String that is free-form note text for the identity (String)</li>
     *         </ul>
     * 
     * @throws IOException If there was a problem accessing the repository for the request
     * @throws IllegalArgumentException
     * 
     */
    Map<String, Object> getAdminMetadata(String resourceType, String identity) throws IllegalArgumentException, IOException;

    /**
     * Retrieve requested keys of administrative metadata for the given resource type and identity.
     * 
     * @param resourceType resource type includes server, application, cluster, host and runtime.
     * @param identity specifies the unique resource for this action. This might be:
     *            <ul>
     *            <li>server identity - {hostName},{userDir},{serverName}</li>
     *            <li>cluster identity - {clusterName}</li>
     *            <li>application identity - {hostName},{userDir},{serverName},{applicationName}</li>
     *            <li>clustered application identity - {clusterName},{applicationName}</li>
     *            <li>host identity - {hostName}</li>
     *            <li>runtime identity - {hostName},{installDir}</li>
     *            </ul>
     * @param mapKeys the map keys to be returned for this request. The possible map keys includes tags, owner, contacts and note
     * @return a map of admin metadata for the requested map keys. This is a map with the possible keys: tags, owner, contacts and note. If a key
     *         has no entries for this resource type and identity, the key will not be included in the returned map. The value for each key
     *         is as follows:
     *         <ul>
     *         <li>tags - an array of tag Strings for the identity (String[])</li>
     *         <li>owner - a simple String indicating the owner of the identity (String)</li>
     *         <li>contacts - an array of Strings that are contact information for the identity (String[])</li>
     *         <li>note - a simple String that is free-form note text for the identity (String)</li>
     *         </ul>
     * 
     * @throws IOException If there was a problem accessing the repository for the request
     * @throws IllegalArgumentException
     * 
     */
    Map<String, Object> getAdminMetadata(String resourceType, String identity, String[] mapKeys) throws IllegalArgumentException, IOException;

    /**
     * Set (replace) all administrative metadata for the given resource type and identity with the supplied metadata map.
     * This will delete all the existing metadata for the resource type and identity (for all metadata keys whether passed in
     * the new metdata map or not), before then adding the new metadata.
     * 
     * @param resourceType resource type includes server, application, cluster, host and runtime.
     * @param identity specifies the unique resource for this action. This might be:
     *            <ul>
     *            <li>server identity - {hostName},{userDir},{serverName}</li>
     *            <li>cluster identity - {clusterName}</li>
     *            <li>application identity - {hostName},{userDir},{serverName},{applicationName}</li>
     *            <li>clustered application identity - {clusterName},{applicationName}</li>
     *            <li>host identity - {hostName}</li>
     *            <li>runtime identity - {hostName},{installDir}</li>
     *            </ul>
     * @param metadata a map of admin metadata. This is a map with the possible keys: tags, owner, contacts and note. The value for each key
     *            is as follows:
     *            <ul>
     *            <li>tags - an array of tag Strings for the identity (String[])</li>
     *            <li>owner - a simple String indicating the owner of the identity (String)</li>
     *            <li>contacts - an array of Strings that are contact information for the identity (String[])</li>
     *            <li>note - a simple String that is free-form note text for the identity (String)</li>
     *            </ul>
     * 
     * @throws IOException If there was a problem accessing the repository for the request
     * @throws IllegalArgumentException
     * 
     */
    public void setAdminMetadata(String resourceType, String identity, Map<String, Object> metadata) throws IllegalArgumentException, IOException;

    /**
     * Remove administrative metadata from the given resource type and identity, that matches the metadata map.
     * 
     * @param resourceType resource type includes server, application, cluster, host and runtime.
     * @param identity specifies the unique resource for this action. This might be:
     *            <ul>
     *            <li>server identity - {hostName},{userDir},{serverName}</li>
     *            <li>cluster identity - {clusterName}</li>
     *            <li>application identity - {hostName},{userDir},{serverName},{applicationName}</li>
     *            <li>clustered application identity - {clusterName},{applicationName}</li>
     *            <li>host identity - {hostName}</li>
     *            <li>runtime identity - {hostName},{installDir}</li>
     *            </ul>
     * @param metadata a map of admin metadata. This is a map with the possible keys: tags, owner, contacts and note. This map should include the
     *            specific values to delete for the desired keys. The value for each key is as follows:
     *            <ul>
     *            <li>tags - an array of tag Strings to delete for the identity (String[])</li>
     *            <li>owner - a simple String indicating the owner to delete from the identity (String)</li>
     *            <li>contacts - an array of Strings that are contact information to delete for the identity (String[])</li>
     *            <li>note - a simple String that is free-form note text to delete for the identity (String)</li>
     *            </ul>
     * 
     * @throws IOException If there was a problem accessing the repository for the request
     */
    void removeAdminMetadata(String resourceType, String identity, Map metadata) throws IOException;

    /**
     * Remove all administrative metadata from the given resource type and identity.
     * 
     * @param resourceType resource type including server, application, cluster, host and runtime.
     * @param identity specifies the unique resource for this action. This might be:
     *            <ul>
     *            <li>server identity - {hostName},{userDir},{serverName}</li>
     *            <li>cluster identity - {clusterName}</li>
     *            <li>application identity - {hostName},{userDir},{serverName},{applicationName}</li>
     *            <li>clustered application identity - {clusterName},{applicationName}</li>
     *            <li>host identity - {hostName}</li>
     *            <li>runtime identity - {hostName},{installDir}</li>
     *            </ul>
     * @throws IOException If there was a problem accessing the repository for the request
     * @throws IllegalArgumentException
     */
    void removeAllAdminMetadata(String resourceType, String identity) throws IllegalArgumentException, IOException;

    /**
     * Remove the administrative metadata for all resource types for the given identity.
     * 
     * @param identity specifies the unique resource for this action. This might be:
     *            <ul>
     *            <li>server identity - {hostName},{userDir},{serverName}</li>
     *            <li>cluster identity - {clusterName}</li>
     *            <li>application identity - {hostName},{userDir},{serverName},{applicationName}</li>
     *            <li>clustered application identity - {clusterName},{applicationName}</li>
     *            <li>host identity - {hostName}</li>
     *            <li>runtime identity - {hostName},{installDir}</li>
     *            </ul>
     * @throws IOException If there was a problem accessing the repository for the request
     */
    void removeAllMetadata(String identity) throws IOException;

    /**
     * Create a tag for the given resource type and identity.
     * 
     * @param resourceType resource type including server, application, cluster, host and runtime.
     * @param identity specifies the unique resource for this action. This might be:
     *            <ul>
     *            <li>server identity - {hostName},{userDir},{serverName}</li>
     *            <li>cluster identity - {clusterName}</li>
     *            <li>application identity - {hostName},{userDir},{serverName},{applicationName}</li>
     *            <li>clustered application identity - {clusterName},{applicationName}</li>
     *            <li>host identity - {hostName}</li>
     *            <li>runtime identity - {hostName},{installDir}</li>
     *            </ul>
     * @param tag to be added
     * @throws IOException If there was a problem accessing the repository for the request
     * @throws IllegalArgumentException
     * 
     */
    void addAdminTag(String resourceType, String identity, String tag) throws IllegalArgumentException, IOException;

    /**
     * Create tags for the given resource type and identity.
     * 
     * @param resourceType resource types including server, application, cluster, host and runtime.
     * @param identity specifies the unique resource for this action. This might be:
     *            <ul>
     *            <li>server identity - {hostName},{userDir},{serverName}</li>
     *            <li>cluster identity - {clusterName}</li>
     *            <li>application identity - {hostName},{userDir},{serverName},{applicationName}</li>
     *            <li>clustered application identity - {clusterName},{applicationName}</li>
     *            <li>host identity - {hostName}</li>
     *            <li>runtime identity - {hostName},{installDir}</li>
     *            </ul>
     * @param tags array of tags to be added to the identity
     * 
     * @throws IOException If there was a problem accessing the repository for the request
     * @throws IllegalArgumentException
     * 
     */
    void addAdminTags(String resourceType, String identity, String[] tags) throws IllegalArgumentException, IOException;

    /**
     * Retrieve all admin and deploy tags for the given resourceType and identity.
     * 
     * @param resourceType resource types including server, application, cluster, host and runtime.
     * @param identity specifies the unique resource for this action. This might be:
     *            <ul>
     *            <li>server identity - {hostName},{userDir},{serverName}</li>
     *            <li>cluster identity - {clusterName}</li>
     *            <li>application identity - {hostName},{userDir},{serverName},{applicationName}</li>
     *            <li>clustered application identity - {clusterName},{applicationName}</li>
     *            <li>host identity - {hostName}</li>
     *            <li>runtime identity - {hostName},{installDir}</li>
     *            </ul>
     * 
     * @return an array of tags
     * @throws IOException If there was a problem accessing the repository for the request
     * @throws IllegalArgumentException
     */
    String[] getAdminTags(String resourceType, String identity) throws IllegalArgumentException, IOException;

    /**
     * Retrieve all unique tags across all resource types.
     * 
     * @return an array of tags
     * @throws IOException If there was a problem accessing the repository for the request
     * @throws IllegalArgumentException
     */
    String[] getTags() throws IllegalArgumentException, IOException;

    /**
     * Get the identities for a resource type.
     * 
     * @param resourceType resource types including server, application, cluster, host and runtime.
     * 
     * @return an array of identities. An identity specifies a unique resource and can be in the following forms:
     *         <ul>
     *         <li>server identity - {hostName},{userDir},{serverName}</li>
     *         <li>cluster identity - {clusterName}</li>
     *         <li>application identity - {hostName},{userDir},{serverName},{applicationName}</li>
     *         <li>clustered application identity - {clusterName},{applicationName}</li>
     *         <li>host identity - {hostName}</li>
     *         <li>runtime identity - {hostName},{installDir}</li>
     *         </ul>
     * @throws IOException If there was a problem accessing the repository for the request
     * @throws IllegalArgumentException
     */
    public String[] getResourceIdentities(String resourceType) throws IllegalArgumentException, IOException;

    /**
     * Set (replace) all tags for the given resource type and identity.
     * 
     * @param resourceType resource types including server, application, cluster, host and runtime.
     * @param identity specifies the unique resource for this action. This might be:
     *            <ul>
     *            <li>server identity - {hostName},{userDir},{serverName}</li>
     *            <li>cluster identity - {clusterName}</li>
     *            <li>application identity - {hostName},{userDir},{serverName},{applicationName}</li>
     *            <li>clustered application identity - {clusterName},{applicationName}</li>
     *            <li>host identity - {hostName}</li>
     *            <li>runtime identity - {hostName},{installDir}</li>
     *            </ul>
     * @param tags array of tags
     * @throws IOException If there was a problem accessing the repository for the request
     * @throws IllegalArgumentException
     * 
     */
    void setAdminTags(String resourceType, String identity, String[] tags) throws IllegalArgumentException, IOException;

    /**
     * Remove a specific tag for the given resource type and identity.
     * 
     * @param resourceType resource types including server, application, cluster, host and runtime.
     * @param identity specifies the unique resource for this action. This might be:
     *            <ul>
     *            <li>server identity - {hostName},{userDir},{serverName}</li>
     *            <li>cluster identity - {clusterName}</li>
     *            <li>application identity - {hostName},{userDir},{serverName},{applicationName}</li>
     *            <li>clustered application identity - {clusterName},{applicationName}</li>
     *            <li>host identity - {hostName}</li>
     *            <li>runtime identity - {hostName},{installDir}</li>
     *            </ul>
     * @param tag to be removed
     * @throws IOException If there was a problem accessing the repository for the request
     * @throws IllegalArgumentException
     */
    void removeAdminTag(String resourceType, String identity, String tag) throws IllegalArgumentException, IOException;

    /**
     * Remove all tags for a given resource type and identity.
     * 
     * @param resourceType resource types including server, application, cluster, host and runtime.
     * @param identity specifies the unique resource for this action. This might be:
     *            <ul>
     *            <li>server identity - {hostName},{userDir},{serverName}</li>
     *            <li>cluster identity - {clusterName}</li>
     *            <li>application identity - {hostName},{userDir},{serverName},{applicationName}</li>
     *            <li>clustered application identity - {clusterName},{applicationName}</li>
     *            <li>host identity - {hostName}</li>
     *            <li>runtime identity - {hostName},{installDir}</li>
     *            </ul>
     * @throws IOException If there was a problem accessing the repository for the request
     * @throws IllegalArgumentException
     */
    void removeAdminTags(String resourceType, String identity) throws IllegalArgumentException, IOException;

    /**
     * For a specified resource type, return all the identities that match any of the data in the supplied metadata map.
     * 
     * @param resourceType resource types including server, application, cluster, host and runtime.
     * @param metadata a map of admin metadata. This is a map with the possible keys: tags, owner, contacts and note. If a key has a value of null, this
     *            means return any identity that has that type of metadata (for example, an owner), no matter the value. The value for each key
     *            is as follows:
     *            <ul>
     *            <li>tags - an array of tag Strings for the identity (String[])</li>
     *            <li>owner - a simple String indicating the owner of the identity (String)</li>
     *            <li>contacts - an array of Strings that are contact information for the identity (String[])</li>
     *            <li>note - a simple String that is free-form note text for the identity (String)</li>
     *            </ul>
     * @return an array of identities. An identity specifies a unique resource and can be in the following forms:
     *         <ul>
     *         <li>server identity - {hostName},{userDir},{serverName}</li>
     *         <li>cluster identity - {clusterName}</li>
     *         <li>application identity - {hostName},{userDir},{serverName},{applicationName}</li>
     *         <li>clustered application identity - {clusterName},{applicationName}</li>
     *         <li>host identity - {hostName}</li>
     *         <li>runtime identity - {hostName},{installDir}</li>
     *         </ul>
     * @throws IOException If there was a problem accessing the repository for the request
     * @throws IllegalArgumentException
     */
    String[] searchResources(String resourceType, Map<String, Object> metadata) throws IllegalArgumentException, IOException;

    /**
     * Return all the identities that match any of the data in the supplied metadata map.
     * 
     * @param metadata a map of admin metadata. This is a map with the possible keys: tags, owner, contacts and note. If a key has a value of null, this
     *            means return any identity that has that type of metadata (for example, an owner), no matter the value. The value for each key
     *            is as follows:
     *            <ul>
     *            <li>tags - an array of tag Strings for the identity (String[])</li>
     *            <li>owner - a simple String indicating the owner of the identity (String)</li>
     *            <li>contacts - an array of Strings that are contact information for the identity (String[])</li>
     *            <li>note - a simple String that is free-form note text for the identity (String)</li>
     *            </ul>
     * @return a map of identities across all resources, where the result map contains following keys:
     *         <ul>
     *         <li>server</li>
     *         <li>cluster</li>
     *         <li>application</li>
     *         <li>host</li>
     *         <li>runtime</li>
     *         </ul>
     *         <p>
     *         Each key will have a value that is an array of identities (String[]). An identity specifies a unique resource and can be in the following forms:
     *         <ul>
     *         <li>server identity - {hostName},{userDir},{serverName}</li>
     *         <li>cluster identity - {clusterName}</li>
     *         <li>application identity - {hostName},{userDir},{serverName},{applicationName}</li>
     *         <li>clustered application identity - {clusterName},{applicationName}</li>
     *         <li>host identity - {hostName}</li>
     *         <li>runtime identity - {hostName},{installDir}</li>
     *         </ul>
     * @throws IOException If there was a problem accessing the repository for the request
     * @throws IllegalArgumentException
     */
    Map<String, String[]> searchResources(Map<String, Object> metadata) throws IllegalArgumentException, IOException;

}
