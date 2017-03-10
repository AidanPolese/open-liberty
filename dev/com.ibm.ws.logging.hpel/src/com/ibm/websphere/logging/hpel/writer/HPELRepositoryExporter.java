//%Z% %I% %W% %G% %U% [%H% %T%]
/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009,2011
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
 * 633565             8.0        01/05/2010   mcasile     Handle slight ManagerImpl constructor change
 * 635684             8.0        01/22/2010   spaungam    update to refelect F017049-18055.1 changes
 * F017049-16916      8.0        08/18/2010   belyi       Clean up class's javadoc for end users
 * 723132             8.0        12/07/2011   belyi       Add ability to export sub process repositories
 */
package com.ibm.websphere.logging.hpel.writer;

import java.io.File;

import com.ibm.ws.logging.hpel.LogRepositorySubProcessCommunication;
import com.ibm.ws.logging.hpel.LogRepositoryWriter;
import com.ibm.ws.logging.hpel.impl.AbstractHPELRepositoryExporter;
import com.ibm.ws.logging.hpel.impl.LogRepositoryManagerImpl;
import com.ibm.ws.logging.hpel.impl.LogRepositorySubManagerImpl;
import com.ibm.ws.logging.hpel.impl.LogRepositoryWriterImpl;

/**
 * Implementation of the {@link RepositoryExporter} interface exporting log records in
 * a directory in HPEL formatted files.  The <code>storeHeader</code> method of the parent class must be called before
 * any records can be stored.  Each record is stored with the <code>storeRecord</code> function.  Failure to
 * follow the order will result in runtime exceptions.
 *
 * @ibm-api
 */
public class HPELRepositoryExporter extends AbstractHPELRepositoryExporter {
	private final File repositoryDir;

	/**
	 * Constructs an exporter which stores log records in HPEL format.
	 *
	 * @param repositoryDir export directory where repository log files will be created.
	 */
	public HPELRepositoryExporter(File repositoryDir) {
		this.repositoryDir = repositoryDir;
	}

	protected LogRepositoryWriter createWriter(String pid, String label) {
        //provide the repository directory, pid/labels to use as the subdirectory name, and true
        //to indicate that subdirectories should be created
		LogRepositoryManagerImpl logManager = new LogRepositoryManagerImpl(repositoryDir, pid, label, true);
		return new LogRepositoryWriterImpl(logManager);
	}
	
	private final static LogRepositorySubProcessCommunication DUMMY_COMMAGENT = new LogRepositorySubProcessCommunication() {
		@Override
		public boolean removeFiles(String destinationType) {
			return false;
		}
		@Override
		public String notifyOfFileCreation(String destinationType,
				long spTimeStamp, String spPid, String spLabel) {
			return null;
		}
		@Override
		public void inactivateSubProcess(String spPid) {
		}
	};

	@Override
	protected LogRepositoryWriter createSubWriter(String pid, String label,
			String superPid) {
		LogRepositorySubManagerImpl logManager = new LogRepositorySubManagerImpl(repositoryDir, pid, label, superPid);
		logManager.setSubProcessCommunicationAgent(DUMMY_COMMAGENT);
		return new LogRepositoryWriterImpl(logManager);
	}
}
