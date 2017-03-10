//%Z% %I% %W% %G% %U% [%H% %T%]
/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *
 * Change History:
 *
 * Reason           Version        Date       User id     Description
 * ----------------------------------------------------------------------------
 * F001340-15950.1    8.0        09/04/2009   belyi       Initial HPEL code
 */
package com.ibm.websphere.logging.hpel.reader;

import java.io.Serializable;

/**
 * A pointer to a location in an HPEL repository.
 * <p>
 * The RepositoryPointer points to an exact point in the repository and can be
 * used in queries to RepositoryReaders.  Implementations of the 
 * RepositoryPointer interface will vary based on the characteristics of the
 * repository.  For example, a file based repository could use a file name and
 * file offset in the repository pointer, whereas a database based repository
 * could use a primary key value in the repository pointer.  As such, there are
 * no methods defined on the RepositoryPointer interface. 
 * <p> 
 * Instances of this interface are obtained from 
 * {@link RepositoryLogRecordHeader} and can be used in 
 * {@link RepositoryReader} requests.
 * 
 * @ibm-api
 */
public interface RepositoryPointer extends Serializable {
}
