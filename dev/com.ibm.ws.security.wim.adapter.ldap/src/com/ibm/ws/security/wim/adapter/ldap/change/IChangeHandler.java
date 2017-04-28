/************** Begin Copyright - Do not add comments here **************
 *
 *
 * IBM Confidential OCO Source Material
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2012, 2013
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U. S. Copyright Office.
 *
 */
package com.ibm.ws.security.wim.adapter.ldap.change;

import java.util.List;

import com.ibm.ws.security.wim.adapter.ldap.LdapEntry;
import com.ibm.wsspi.security.wim.exception.WIMException;

public interface IChangeHandler {

    /**
     * Method to return the current checkpoint for the repository. This method
     * must be invoked when the VMM data and its application's copy are in sync.
     * The checkpoint returned by this method must be used subsequently when
     * querying for changes since the state when the data copies were in sync.
     *
     * @return String representation of repository checkpoint. For e.g. in case
     *         of TDS, it returns the changelog number and in case of Active Directory
     *         it returns the highestCommittedUSN.
     *
     * @throws WIMException
     */
    String getCurrentCheckPoint() throws WIMException;

    /**
     * Method that must be implemented to retrieve changes since the given
     * checkpoint for the repository.
     * 
     * @param checkPoint String checkpoint, changes beyond which need to be returned.
     * @param changeTypes List of change types that must be returned. It is possible
     *            to return only certain types of changes (e.g. only add and delete changes) or
     *            all types of changes (*).
     *
     * @param base LDAP search base
     * @param filter LDAP search filter
     * @param scope LDAP search scope
     * @param inEntityTypes Used to identify the properties to return
     * @param propNames Used to identify the properties to return
     * @param countLimit Search result set size limit
     * @param timeLimit Search time limit
     *
     * @return A List of LdapEntry objects representing changed entries in LDAP
     *
     * @throws WIMException
     */
    List<LdapEntry> searchChangedEntities(String checkPoint, List<String> changeTypes, String base, String filter,
                                          int scope, List<String> inEntityTypes, List<String> propNames, int countLimit, int timeLimit) throws WIMException;
}
