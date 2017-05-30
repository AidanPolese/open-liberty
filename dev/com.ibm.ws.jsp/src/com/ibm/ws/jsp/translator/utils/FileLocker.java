package com.ibm.ws.jsp.translator.utils;


/**
 * The FileLocker Class should be extended by anyone wanting to lock Files.
 * A ZosFileLockerImpl is contained in JSP in SERV1.  JSPComponent
 * Update the JSPClassFactory with .updateMap() in order to add and then getInstanceOf()
 *    in order to load your implementation in the place of this default class.
 *    
 * @author dmeisenb, kennas
 *
 */
  public class FileLocker {
	
    public boolean obtainFileLock(String filename)
	{
    
    	return true;
	}

	public boolean releaseFileLock(String filename)
	{

		return true;
	}
	
	
}
