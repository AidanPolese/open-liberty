/*
 * @start_prolog@
 * Version: @(#) 1.18 SIB/ws/code/sib.processor.impl/src/com/ibm/ws/sib/processor/matching/MPPrincipal.java, SIB.processor, WASX.SIB, ff1246.02 09/05/28 05:46:29 [11/16/12 22:51:53]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  Copyright IBM Corp. 2004, 2009 
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
 * 184185.1.6       270404 nyoung   Enable delivery time discriminator access checks.
 * 202405           170504 gatfora  Missing FFDC's
 * 184185.1.10      250504 nyoung   Implement FRP Security.
 * 207008.2         010704 nyoung   Fold user and group names to lower case.
 * 213918           020704 nyoung   Fix NPE due to groups list not being initialised.
 * 238960.2         151004 nyoung   Trace flooded by Java 2 security warnings.
 * 246746           081204 gatfora  Removal of unused code.
 * SIB0165.mp.1     260907 nottinga Stopped using AuthUtils.getGroupsForUser.
 * 471043           011007 sibcopyr Automatic update of trace guards
 * 516346           010508 djvines  equals to check class is the same
 * 585899           210409 jhumber  Call getUserUsingUniqueName in constructors
 * 585899.1         280509 gatfora  Security checks should be case insensitive.
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.matching;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.security.auth.Subject;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.security.auth.AuthUtils;
//import com.ibm.ws.sib.security.users.UserRepository;
//import com.ibm.ws.sib.security.users.UserRepositoryException;
//import com.ibm.ws.sib.security.users.UserRepositoryFactory;
//import com.ibm.ws.sib.security.users.UserRepository.Group;
//import com.ibm.ws.sib.security.users.UserRepositoryFactory.BehaviouralModifiers;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 */
public class MPPrincipal implements Principal
{
  private static final TraceComponent tc =
    SibTr.register(MPPrincipal.class, SIMPConstants.MP_TRACE_GROUP, SIMPConstants.RESOURCE_BUNDLE);

  /* Output source info */
  static {
    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
      SibTr.debug(tc, "Source info: @(#)SIB/ws/code/sib.processor.impl/src/com/ibm/ws/sib/processor/matching/MPPrincipal.java, SIB.processor, WASX.SIB, ff1246.02 1.18");
  }

  /** The user name */
  private String name;
  /** The groupst he user is in */
  private List<String> groups;

  public MPPrincipal(String nm)
  {
    name = nm.toLowerCase();
  }

  public MPPrincipal(String busName, Subject subject, AuthUtils utils)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
    {
      String report = "<null>";
      if (subject != null)
      {
        report = "subject(" + utils.getUserName(subject) + ")";
      }
      SibTr.entry(tc, "MPPrincipal",  new Object[] { report, utils});
    }

    if (subject != null)
    {
      name = utils.getUserName(subject).toLowerCase();
//      try
//      {
//        UserRepository rep = UserRepositoryFactory.getUserRepository(busName, BehaviouralModifiers.LAZILY_RETRIEVE_ENTITY_DATA, BehaviouralModifiers.CACHED_GROUP_DATA_ALLOWED);
//
//        Set<Group> setOfGroups = rep.getUserUsingUniqueName(name).getGroups();
//        groups = new ArrayList<String>();
//
//        for (Group group : setOfGroups)
//        {
//          groups.add(group.getSecurityName().toLowerCase());
//        }
//      }
//      catch (UserRepositoryException e)
//      {
//        // No FFDC code needed
//      }
    }

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "MPPrincipal", this);
  }

  public MPPrincipal(String busName, String userName)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "MPPrincipal", new Object[] { busName, userName });

    name = userName.toLowerCase();

//    try
//    {
//      UserRepository rep = UserRepositoryFactory.getUserRepository(busName, BehaviouralModifiers.LAZILY_RETRIEVE_ENTITY_DATA, BehaviouralModifiers.CACHED_GROUP_DATA_ALLOWED);
//
//      Set<Group> setOfGroups = rep.getUserUsingUniqueName(name).getGroups();
//      groups = new ArrayList<String>();
//
//      for (Group group : setOfGroups)
//      {
//        groups.add(group.getSecurityName().toLowerCase());
//      }
//    }
//    catch (UserRepositoryException e)
//    {
//      // No FFDC code needed
//    }

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "MPPrincipal", this);
  }

  //------------------------------------------------------------------------------
  // Method: MPUser.equals
  //------------------------------------------------------------------------------
  /**
   *
   * Created: 99-01-19
   */
  //---------------------------------------------------------------------------
  public boolean equals(Object o){
    if (o == null) return false;
    if (o == this) return true;

    if (o.getClass() != this.getClass()) return false; // Must be the same subclass to be equal

    return ((MPPrincipal) o).name.equals(name);
  } //equals


  //------------------------------------------------------------------------------
  // Method: MPUser.toString
  //------------------------------------------------------------------------------
  /**
   *
   * Created: 99-01-19
   */
  //---------------------------------------------------------------------------
  public String toString(){
    return "Principal(" + name + ")";
  } //toString


  //------------------------------------------------------------------------------
  // Method: MPUser.hashCode
  //------------------------------------------------------------------------------
  /**
   *
   * Created: 99-01-19
   */
  //---------------------------------------------------------------------------
  public int hashCode(){
    return name.hashCode();
  } //hashCode

  //------------------------------------------------------------------------------
  // Method: MPUser.getName
  //------------------------------------------------------------------------------
  /**
   *
   * Created: 99-01-19
   */
  //---------------------------------------------------------------------------
  public String getName(){
    return name;
  } //getName

  /**
   * @return
   */
  public List getGroups()
  {
	  return groups;
  }
}
