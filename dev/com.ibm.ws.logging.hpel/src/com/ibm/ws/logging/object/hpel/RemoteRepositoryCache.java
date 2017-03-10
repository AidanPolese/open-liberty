//%Z% %I% %W% %G% %U% [%H% %T%]
/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009,2010
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *
 * Change History:
 *
 * Reason           Version        Date       User id     Description
 * ----------------------------------------------------------------------------
 * F017049-22352      8.0        08/04/2010   belyi       Initial class code
 * 668669             8.0        09/02/2010   belyi       Add code to initialize transient size after de-serialization
 */
package com.ibm.ws.logging.object.hpel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Result cache instance to be sent with remote calls.
 */
public class RemoteRepositoryCache implements Serializable {
	private static final long serialVersionUID = -8618080841198449905L;
	
	private final int total;
	private final byte[][] start;
	private final byte[][] end;
	transient int size = -1;
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
	     in.defaultReadObject();
	     size = -1;
	}
	/**
	 * constructs new remote repository cache object with specified values
	 * 
	 * @param total number of files in the repository result
	 * @param start caching info for a set of files at the start of the repository
	 * @param end caching info for a set of files at the end of the repository
	 */
	public RemoteRepositoryCache(int total, byte[][] start, byte[][] end) {
		if (start == null || end == null) {
			throw new IllegalArgumentException("Neither start nor end value can be 'null'");
		}
		if (total < start.length + end.length) {
			throw new IllegalArgumentException("Value of total should be more or equal to sum of start and end array sizes.");
		}
		this.total = total;
		this.start = start;
		this.end = end;
	}
	
	/**
	 * gets number of files in the repository result
	 * @return number of files as recorded in this instance
	 */
	public int getTotal() {
		return total;
	}
	
	/**
	 * gets array of bytes representing statistics for each file at the start of the repository
	 * @return array of binary representations of file cache
	 */
	public byte[][] getStart() {
		return start;
	}
	
	/**
	 * gets array of bytes representing statistics for each file at the end of the repository
	 * @return array of binary representations of file cache
	 */
	public byte[][] getEnd() {
		return end;
	}
	
	/**
	 * gets indicator that this instance contain statistics for all files in the result
	 * @return <code>true</code> if <code>start</code> and <code>end</code> arrays together contain
	 * 		statistics for all <code>total</code> files in the result.
	 */
	public boolean isComplete() {
		return total == start.length + end.length;
	}
	
	/**
	 * gets number of records in the result based on the information available in this cache.
	 * @return sum of sizes recorded in start and end arrays
	 */
	public int getSize() {
		if (size < 0) {
			size = 0;
			for (byte[] cache: start) {
				size += new RemoteOneFileCache(cache).getSize();
			}
			for (byte[] cache: end) {
				size += new RemoteOneFileCache(cache).getSize();
			}
		}
		return size;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(end);
		result = prime * result + Arrays.hashCode(start);
		result = prime * result + total;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RemoteRepositoryCache other = (RemoteRepositoryCache) obj;
		if (!Arrays.deepEquals(end, other.end))
			return false;
		if (!Arrays.deepEquals(start, other.start))
			return false;
		if (total != other.total)
			return false;
		return true;
	}
	
	/**
	 * Helper class for converting file statistics between individual values and array of
	 * bytes used in remote communication
	 */
	public static class RemoteOneFileCache {
		private final long timestamp;
		private final int size;
		private final long firstId;
		private final long lastId;
		
		/**
		 * creates new instance out of individual statistics values
		 * 
		 * @param timestamp time stamp of the file statistics is collected for
		 * @param size the number of records in the file
		 * @param firstId internal sequence ID of the first record in the file
		 * @param lastId internal sequence ID of the last record in the file
		 */
		public RemoteOneFileCache(long timestamp, int size, long firstId, long lastId) {
			this.timestamp = timestamp;
			this.size = size;
			this.firstId = firstId;
			this.lastId = lastId;
		}
		
		/**
		 * creates new instance out of byte array received in a remote call
		 * 
		 * @param cache byte array received in a remote call
		 */
		public RemoteOneFileCache(byte[] cache) {
			DataInput input = new DataInputStream(new ByteArrayInputStream(cache));
			try {
				timestamp = input.readLong();
				size = input.readInt();
				firstId = input.readLong();
				lastId = input.readLong();
			} catch (IOException e) {
				throw new IllegalArgumentException("Input bytes do not contain all necessary values");
			}
		}
		
		/**
		 * returns byte representation of this instance
		 * 
		 * @return byte array which can later be used in a constructor to retrieve individual values
		 */
		public byte[] toByteArray() {
			// Create output buffer to hold 3 long and 1 integer values.
			ByteArrayOutputStream buffer = new ByteArrayOutputStream(28);
			try {
				DataOutputStream output = new DataOutputStream(buffer);
				output.writeLong(timestamp);
				output.writeInt(size);
				output.writeLong(firstId);
				output.writeLong(lastId);
				output.close();
			} catch (IOException e) {
				throw new RuntimeException("Internal Error: failed to write into in-memory buffer");
			}
			return buffer.toByteArray();
		}
		
		/**
		 * @return time stamp of the file statistics is collected for
		 */
		public long getTimestamp() {
			return timestamp;
		}
		
		/**
		 * @return the number of records in the file
		 */
		public int getSize() {
			return size;
		}
		
		/**
		 * @return internal sequence ID of the first record in the file
		 */
		public long getFirstId() {
			return firstId;
		}
		
		/**
		 * @return internal sequence ID of the last record in the file
		 */
		public long getLastId() {
			return lastId;
		}
	}

}
