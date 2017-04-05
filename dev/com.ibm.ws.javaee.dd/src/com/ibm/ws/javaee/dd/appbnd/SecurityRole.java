// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// %I% %W% %G% %U%
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2011
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// F46946    WAS85     20110712 bkail    : New
// --------- --------- -------- --------- -----------------------------------------

package com.ibm.ws.javaee.dd.appbnd;

import java.util.List;

import com.ibm.ws.javaee.ddmetadata.annotation.DDAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttributeType;
import com.ibm.ws.javaee.ddmetadata.annotation.DDElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDIdAttribute;

/**
 * Represents &lt;security-role>.
 */
@DDIdAttribute
public interface SecurityRole {

    /**
     * @return name="..." attribute value
     */
    @DDAttribute(name = "name", type = DDAttributeType.String)
    String getName();

    /**
     * @return &lt;user> as a read-only list
     */
    @DDElement(name = "user")
    List<User> getUsers();

    /**
     * @return &lt;group> as a read-only list
     */
    @DDElement(name = "group")
    List<Group> getGroups();

    /**
     * @return &lt;special-subject> as a read-only list
     */
    @DDElement(name = "special-subject")
    List<SpecialSubject> getSpecialSubjects();

    /**
     * @return &lt;run-as>, or null if unspecified
     */
    @DDElement(name = "run-as")
    RunAs getRunAs();

}
