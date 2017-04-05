/*
 * @start_prolog@
 * Version: @(#) 1.11 SIB/ws/code/sib.processor.impl/src/com/ibm/ws/sib/processor/matching/MPGroup.java, SIB.processor, WASX.SIB, ff1246.02 08/05/12 21:44:20 [11/16/12 22:51:53]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  Copyright IBM Corp. 2004, 2008
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 *
 * Change activity:
 *
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- -------------------------------------------------
 * 184185.1.4       210404 nyoung   Introduce discriminator access checking.
 * 207008.2         010704 nyoung   Fold user and group names to lower case.
 * 228819           070904 nyoung   Support setting of default security policy.
 * 238960.2         151004 nyoung   Trace flooded by Java 2 security warnings.
 * 246746           081204 gatfora  Removal of unused code.
 * 516346           010508 djvines  Don't override equals
 * 520288           130508 sibcopyr Automatic update of trace guards 
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.matching;

import java.security.Principal;
import java.security.acl.Group;
import java.util.List;
import java.util.Enumeration;

import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * @author Neil Young
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MPGroup extends MPPrincipal implements Group
{
  private static final TraceComponent tc =
    SibTr.register(MPGroup.class, SIMPConstants.MP_TRACE_GROUP, SIMPConstants.RESOURCE_BUNDLE);

  /* Output source info */
  static {
    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
      SibTr.debug(tc, "Source info: @(#)SIB/ws/code/sib.processor.impl/src/com/ibm/ws/sib/processor/matching/MPGroup.java, SIB.processor, WASX.SIB, ff1246.02 1.11");
  }

    public MPGroup(String nm)
    {
      super(nm.toLowerCase());
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      {
        SibTr.entry(tc, "MPGroup", nm);
        SibTr.exit(tc, "MPGroup", this);
      }
    }

    /**
     * Adds the specified member to the group.
     *
     * @param user the principal to add to this group.
     *
     * @return true if the member was successfully added,
     * false if the principal was already a member.
     */
    public boolean addMember(Principal user)
    {
      return true;
    }

    /**
     * Removes the specified member from the group.
     *
     * @param user the principal to remove from this group.
     *
     * @return true if the principal was removed, or
     * false if the principal was not a member.
     */
    public boolean removeMember(Principal user)
    {
       return true;
    }

    /**
     * Returns true if the passed principal is a member of the group.
     * This method does a recursive search, so if a principal belongs to a
     * group which is a member of this group, true is returned.
     *
     * @param member the principal whose membership is to be checked.
     *
     * @return true if the principal is a member of this group,
     * false otherwise.
     */
    public boolean isMember(Principal member)
    {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
        SibTr.entry(tc, "isMember", "is: " + member + ", a member of: " + this);

      boolean result = false;

      if(member instanceof MPPrincipal)
      {
        List theGroups = ((MPPrincipal)member).getGroups();
        if(theGroups != null)
          result = theGroups.contains(getName());
      }

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
        SibTr.exit(tc, "isMember", new Boolean(result));

      return result;
    }


    /**
     * Returns an enumeration of the members in the group.
     * The returned objects can be instances of either Principal
     * or Group (which is a subclass of Principal).
     *
     * @return an enumeration of the group members.
     */
    public Enumeration members()
    {
        return null;
    }
}
