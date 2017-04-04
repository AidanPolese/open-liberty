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
* 395641           271106 ajw      Changed context names
*/
package com.ibm.ws.sib.pmi.rm;

import com.ibm.websphere.sib.SIDestinationAddress;

/**
 * Class to be used by instrumentation points in the SIB layer to create
 * the context values needed on methods on SIBPmiRm class. If the component is
 * not SIB and not send/receive/publish calls this class should not be
 * used.
 */
public class SIBContextValues
{
  private String className;
  private String methodName;
  private String busName;
  private String destinationName;

  /**
   * Constructor to create the context values. This should only be invoked
   * when the <code>isComponentEnabled(int)</code> returns true
   * 
   * @param SIDestinationAddress
   * @param ClassName
   * @param methodName
   */
  public SIBContextValues(SIDestinationAddress destinationAddress,
      String className, String methodName)
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
    
    if (className == null)
    {
      this.className = "";
    }
    else
    {
      this.className = className;
    }
    
    if (methodName == null)
    {
      methodName = "";
    }
    else
    {
      this.methodName = methodName;
    }
  }

  /**
   * @return String[] the values set in the constructor
   */
  public String[] getContextValues()
  {
    return new String[] { className, methodName, busName, destinationName};
  }
  
  /**
   * Returns the context names that will be used for registration
   * of the SIB component
   * @return
   */
  public static String[] getContextNames()
  {
    String[] contextNames = {"ClassName", "MethodName", "BusName", 
                             "DestinationName" };
    return contextNames;
  }
}
