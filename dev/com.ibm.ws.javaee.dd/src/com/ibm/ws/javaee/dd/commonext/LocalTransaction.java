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
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyNotInUse;

/**
 * Represents &lt;local-transaction>.
 */
@DDIdAttribute
public interface LocalTransaction {
    enum BoundaryEnum {
        @LibertyNotInUse
        @DDXMIEnumConstant(name = "ActivitySession")
        ACTIVITY_SESSION,
        @DDXMIEnumConstant(name = "BeanMethod")
        BEAN_METHOD
    }

    enum ResolverEnum {
        @DDXMIEnumConstant(name = "Application")
        APPLICATION,
        @DDXMIEnumConstant(name = "ContainerAtBoundary")
        CONTAINER_AT_BOUNDARY
    }

    enum UnresolvedActionEnum {
        @DDXMIEnumConstant(name = "Rollback")
        ROLLBACK,
        @DDXMIEnumConstant(name = "Commit")
        COMMIT
    }

    boolean isSetBoundary();

    @LibertyNotInUse
    @DDAttribute(name = "boundary", type = DDAttributeType.Enum)
    @DDXMIAttribute(name = "boundary")
    BoundaryEnum getBoundary();

    boolean isSetResolver();

    @DDAttribute(name = "resolver", type = DDAttributeType.Enum)
    @DDXMIAttribute(name = "resolver")
    ResolverEnum getResolver();

    boolean isSetUnresolvedAction();

    @DDAttribute(name = "unresolved-action", type = DDAttributeType.Enum)
    @DDXMIAttribute(name = "unresolvedAction")
    UnresolvedActionEnum getUnresolvedAction();

    boolean isSetShareable();

    @DDAttribute(name = "shareable", type = DDAttributeType.Boolean)
    @DDXMIAttribute(name = "shareable")
    boolean isShareable();
}
