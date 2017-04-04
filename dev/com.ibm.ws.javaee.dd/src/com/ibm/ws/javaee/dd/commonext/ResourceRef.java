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

package com.ibm.ws.javaee.dd.commonext;

import com.ibm.ws.javaee.ddmetadata.annotation.DDAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttributeType;
import com.ibm.ws.javaee.ddmetadata.annotation.DDIdAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIEnumConstant;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIRefElement;
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyNotInUse;

/**
 * Represents &lt;resource-ref>.
 */
@DDIdAttribute
public interface ResourceRef {
    enum IsolationLevelEnum {
        TRANSACTION_NONE,
        TRANSACTION_READ_UNCOMMITTED,
        TRANSACTION_READ_COMMITTED,
        TRANSACTION_REPEATABLE_READ,
        TRANSACTION_SERIALIZABLE
    }

    enum ConnectionManagementPolicyEnum {
        @DDXMIEnumConstant(name = "Default")
        DEFAULT,
        @DDXMIEnumConstant(name = "Aggressive")
        AGGRESSIVE,
        @DDXMIEnumConstant(name = "Normal")
        NORMAL
    }

    enum BranchCouplingEnum {
        @DDXMIEnumConstant(name = "Loose")
        LOOSE,
        @DDXMIEnumConstant(name = "Tight")
        TIGHT
    }

    @DDAttribute(name = "name", type = DDAttributeType.String)
    @DDXMIRefElement(name = "resourceRef", referentType = com.ibm.ws.javaee.dd.common.ResourceRef.class, getter = "getName")
    String getName();

    boolean isSetIsolationLevel();

    @DDAttribute(name = "isolation-level", type = DDAttributeType.Enum)
    @DDXMIAttribute(name = "isolationLevel")
    IsolationLevelEnum getIsolationLevel();

    boolean isSetConnectionManagementPolicy();

    @LibertyNotInUse
    @DDAttribute(name = "connection-management-policy", type = DDAttributeType.Enum)
    @DDXMIAttribute(name = "connectionManagementPolicy")
    ConnectionManagementPolicyEnum getConnectionManagementPolicy();

    boolean isSetCommitPriority();

    @DDAttribute(name = "commit-priority", type = DDAttributeType.Int)
    @DDXMIAttribute(name = "commitPriority")
    int getCommitPriority();

    boolean isSetBranchCoupling();

    @DDAttribute(name = "branch-coupling", type = DDAttributeType.Enum)
    @DDXMIAttribute(name = "branchCoupling")
    BranchCouplingEnum getBranchCoupling();

}
