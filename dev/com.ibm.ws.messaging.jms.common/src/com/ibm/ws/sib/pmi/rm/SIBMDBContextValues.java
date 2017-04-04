/*
* 
* 
* ============================================================================
* IBM Confidential OCO Source Materials
* 
* Copyright IBM Corp. 2012
* 
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
* ============================================================================
* 
*
* Change activity:
*
* Reason           Date   Origin   Description
* ---------------  ------ -------- -------------------------------------------
* SIB0025.pmir.3   260805 ajw      Refactor and Registration support
* SIB0025.pmir.4   140905 ajw      Add guards to context and transaction info
* 317787.1         031105 ajw      New provider context and isFilterPassed call
* 395641           271106 ajw      Changed context names
*/
package com.ibm.ws.sib.pmi.rm;

import com.ibm.websphere.sib.SIDestinationAddress;

/**
 * Class to be used by instrumentation points in the SIB layer to create
 * the context values needed on methods on SIBPmiRm class. If the component is
 * not SIB and not a MDB this class should not be used.
 */
public class SIBMDBContextValues
{
  private String busName;
  private String destinationName;
  private String messageSelector;
  private String mdbDiscriminiator;
  private final String provider = "SIB";

  /**
   * Constructor to create the context values. This should only be invoked
   * when the <code>isComponentEnabled(int)</code> returns true
   * 
   * @param SIDestinationAddress
   * @param MessageSelector
   * @param MdbDiscriminator
   */
  public SIBMDBContextValues(SIDestinationAddress destinationAddress,
      String messageSelector, String mdbDiscriminator)
  {
    if (destinationAddress == null)
    {
      this.busName = "";
      this.destinationName = "";
    }
    else
    {
      this.busName = destinationAddress.getBusName();
      this.destinationName = destinationAddress.getDestinationName();
    }
    
    if (mdbDiscriminator == null)
    {
      this.mdbDiscriminiator = "";
    }
    else
    {
      this.mdbDiscriminiator = mdbDiscriminator;
    }
    
    if (messageSelector == null)
    {
      this.messageSelector = "";
    }
    else
    {
      this.messageSelector = messageSelector;
    }
  }

  /**
   * @return String[] the values set in the constructor
   */
  public String[] getContextValues()
  {
    return new String[] { busName, destinationName, 
                          messageSelector, mdbDiscriminiator, provider };
  }
  
  
  /**
   * Returns the MDB context names that will be used for registration
   * of the SIB component
   * @return
   */
  public static String[] getContextNames()
  {
    String[] contextNames = {"BusName", "DestinationName", 
                             "MessageSelector", "MdbDiscriminator", "Provider" };
    return contextNames;
  }
}
