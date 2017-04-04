//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//      SECPYX          01/30/06        mmolden             security changes/collaborator refactoring  

package com.ibm.wsspi.webcontainer.collaborator;

/**
 * @author asisin
 *
 */
public class TxCollaboratorConfig 
{
	Object suspendTx = null;
	Object dispatchContext = null;
	private boolean beginner;
    	
	/**
	 * @return
	 */
	public Object getDispatchContext()
	{
		return dispatchContext;
	}

	/**
	 * @return
	 */
	public Object getSuspendTx()
	{
		return suspendTx;
	}

	/**
	 * @param object
	 */
	public void setDispatchContext(Object object)
	{
		dispatchContext = object;
	}

	/**
	 * @param object
	 */
	public void setSuspendTx(Object object)
	{
		suspendTx = object;
	}
	
	/**
	 * @param object
	 */
	public void setBeginner(boolean beginner)
	{
		this.beginner = beginner;
	}
	
    /**
     * @return
     */
    public boolean getBeginner()
    {
        return beginner;
    }
	

}
	
