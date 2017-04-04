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

/**
 * Class to be used by instrumentation points in the JMS layer to create
 * the context values needed on methods on SIBPmiRm class. If the component is
 * not JMS this class should not be used.
 */
public class JMSContextValues
{
  private String className;
  private String methodName;
 
  /**
   * Constructor to create the context values. This should only be invoked
   * when the <code>isComponentEnabled(int)</code> returns true
   * 
   * @param className
   * @param methodName
   */
  public JMSContextValues(String className, String methodName)
  {
    if (className == null)
      className = "";
    
    if (methodName == null)
      methodName = "";
    
    this.className = className;
    this.methodName = methodName;
  }

  /**
   * @return String[] the values set in the constructor
   */
  public String[] getContextValues()
  {
    return new String[] { className, methodName };
  }
  
  /**
   * Returns the context names that will be used for registration
   * of the JMS component
   * @return
   */
  public static String[] getContextNames()
  {
    String[] contextNames = {"ClassName", "MethodName"};
    return contextNames;
  }
}
