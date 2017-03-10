// %Z% %I% %W% %G% %U% [%H% %T%]
/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2012
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *
 * Change History:
 *
 * Reason           Version        Date       User id     Description
 * ----------------------------------------------------------------------------
 * 739832            8.5.5       09/28/2012   belyi       Initial code check-in
 */
package com.ibm.ws.logging.hpel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Interface to have a customized way to create output stream into a file. For
 * example, if need to use other than default encoding. File instances which
 * need such capability need to implement that interface with methods matching
 * FileOutputStream constructors accepting File instances.
 */
public interface GenericOutputFile {
    /**
     * @see FileOutputStream#FileOutputStream(File)
     */
    FileOutputStream createOutputStream() throws IOException;
    
    /**
     * @see FileOutputStream#FileOutputStream(File, boolean)
     */
    FileOutputStream createOutputStream(boolean append) throws IOException;
}
