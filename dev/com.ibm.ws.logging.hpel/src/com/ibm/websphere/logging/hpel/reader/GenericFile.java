//%Z% %I% %W% %G% %U% [%H% %T%]
/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2011
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *
 * Change History:
 *
 * Reason           Version        Date       User id     Description
 * ----------------------------------------------------------------------------
 * F017049-43800      8.1       06/29/2011     belyi       Initial code
 */
package com.ibm.websphere.logging.hpel.reader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Interface representing extension of {@link java.io.File} requiring special methods for
 * finding child instances and to return {@link java.io.InputStream} providing file's content.
 * 
 * @ibm-api
 */
public interface GenericFile  {

	/**
	 * Returns child instance with the provided name.
	 * 
	 * @param name name of the child instance
	 * @return instance of the same class representing the child instance.
	 */
	File getChild(String name);
	
	/**
	 * Returns input stream of this file content.
	 * 
	 * @return input stream instance providing access to this file content
	 * @throws IOException if problem happens to open input stream
	 */
	InputStream getInputStream() throws IOException;

}
