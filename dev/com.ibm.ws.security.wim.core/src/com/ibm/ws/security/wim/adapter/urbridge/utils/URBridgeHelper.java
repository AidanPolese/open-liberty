/************** Begin Copyright - Do not add comments here **************
 *
 *
 * IBM Confidential OCO Source Material
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2012, 2013, 2014
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U. S. Copyright Office.
 *
 */
package com.ibm.ws.security.wim.adapter.urbridge.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ibm.websphere.security.wim.Service;

public class URBridgeHelper {

    //Map to store object per domain
    private static List<String> personAccountTypeList = Collections.synchronizedList(new ArrayList<String>(1));

    //Map to store object per domain
    private static List<String> groupAccountTypeList = Collections.synchronizedList(new ArrayList<String>(1));

    public URBridgeHelper() {
        super();
    }

    public static void mapSupportedEntityTypeList(List<String> entityTypes) {
        for (int i = 0; i < entityTypes.size(); i++) {
            String name = entityTypes.get(i);
            if (Service.DO_PERSON_ACCOUNT.equalsIgnoreCase(name)) {
                personAccountTypeList.add(name);
            }
            if (Service.DO_GROUP.equalsIgnoreCase(name)) {
                groupAccountTypeList.add(name);
            }
        }
    }

    /**
     * Gets The Person Account List for current domain
     *
     * @return
     */
    public static String getPersonAccountType() {
        return personAccountTypeList.get(0);
    }

    /**
     * Gets the group account List for current domain
     *
     * @return
     */
    public static String getGroupAccountType(){
        return (String)groupAccountTypeList.get(0);
    }
}
