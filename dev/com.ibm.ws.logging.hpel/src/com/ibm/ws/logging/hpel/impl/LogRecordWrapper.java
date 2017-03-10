//%Z% %I% %W% %G% %U% [%H% %T%]
/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2010, 2012
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *
 * Change History:
 *
 * Reason           Version        Date       User id     Description
 * ----------------------------------------------------------------------------
 * 663081             8.0        09/09/2010    belyi      Initial code check-in
 * 694943             8.0        03/05/2011    belyi      Copy wsLogRecord extensions into this extensions
 * PM44407            7.0        09/15/2011   spaungam    advanced format is missing threadname
 * F1344-49496        8.0        10/26/2011    belyi      Copy MDC into this extensions
 * 729954             8.5        03/16/2012    belyi      Use LogRecordStack for thread specific information
 * 725321             8.5        03/21/2012    belyi      Use thread id from the stack if available
 */
package com.ibm.ws.logging.hpel.impl;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.ibm.ejs.ras.hpel.HpelHelper;
import com.ibm.ejs.ras.hpel.Messages;
import com.ibm.websphere.logging.hpel.reader.RepositoryLogRecord;
import com.ibm.websphere.logging.hpel.reader.RepositoryPointer;
import com.ibm.ws.logging.object.hpel.HpelLogRecordFactory;
import com.ibm.ws.logging.object.hpel.LogRecordStack;
import com.ibm.ws.logging.internal.WsLogRecord;

/**
 * Wrapper implementation of the RepositoryLogRecord interface for LogRecord and WsLogRecord
 * instances. Note, that since instances of this class did not come from a repository their 
 * {@link #getRepositoryPointer()} method throws {@link UnsupportedOperationException}
 */
public class LogRecordWrapper implements RepositoryLogRecord {
	private static final long serialVersionUID = 1770501124954999137L;
	
	private final String messageID;
	private final String rawMessage;
	private final long millis;
	private final Level level;
	private final int threadID;
	private final long sequence;
	
	private final String localizedMessage;	
	private final String messageLocale;
	private final String loggerName;
	private final String resourceBundleName;         
	private final String sourceClassName;
	private final String sourceMethodName;
	private final Object[] parameters;
	
	// WSLogRecord fields
	private final int localizable;
	private final byte[] rawData;
	private final String stackTrace; 
	private final Map<String, String> extensions;   
	
	/**
	 * Creates instance based on the values found in the specified record
	 * 
	 * @param record log record to use as a base for all values of this instance
	 */
	public LogRecordWrapper(LogRecord record) {
		if (record == null) {
			throw new IllegalArgumentException("Wrapper's base record cannot be null");
		}
		millis = record.getMillis();
		level = record.getLevel();
		// Get thread specific information from the stack
		extensions = LogRecordStack.getExtensions();
		threadID = HpelHelper.getActiveThreadId(record);
		sequence = record.getSequenceNumber();
		rawMessage = record.getMessage();
		loggerName = record.getLoggerName();
		resourceBundleName = record.getResourceBundleName();
		sourceClassName = record.getSourceClassName();
		sourceMethodName = record.getSourceMethodName();
		if (record.getThrown() != null) {
			stackTrace = HpelHelper.throwableToString(record.getThrown());
		} else {
			stackTrace = null;
		}
		parameters = record.getParameters();
		final Locale locale;
		WsLogRecord wsLogRecord = HpelLogRecordFactory.getWsLogRecordIfConvertible(record);
		if (wsLogRecord != null) {
			localizable = wsLogRecord.getLocalizable();
			if (wsLogRecord.getMessageLocale() != null) {
				messageLocale = wsLogRecord.getMessageLocale();
				String[] items = messageLocale.split("_");
				if (items.length == 0) {
					locale = Locale.getDefault();
				} else if (items.length == 1) {
					locale = new Locale(items[0]);
				} else if (items.length == 2) {
					locale = new Locale(items[0], items[1]);
				} else {
					locale = new Locale(items[0], items[1], items[2]);
				}
			} else {
				locale = Locale.getDefault();
				messageLocale = locale.toString();
			}
			if (localizable == WsLogRecord.REQUIRES_NO_LOCALIZATION) {
				String formatted = wsLogRecord.getFormattedMessage();
				if (rawMessage == null || !rawMessage.equals(formatted)) {
					localizedMessage = formatted;
				} else {
					localizedMessage = null;
				}
			} else {
				localizedMessage = getLocalized(record, locale);
			}
			rawData = wsLogRecord.getRawData();			
						
			extensions.putAll(wsLogRecord.getExtensions());
			
			// Extension fields
			if (wsLogRecord.getReporterOrSourceThreadName() != null) {
				extensions.put(RepositoryLogRecord.PTHREADID, wsLogRecord.getReporterOrSourceThreadName());
			}
		} else {
			localizable = WsLogRecord.DEFAULT_LOCALIZATION;
			locale = Locale.getDefault();
			messageLocale = locale.toString();
			localizedMessage = getLocalized(record, locale);
			rawData = null;
		}
		messageID = HpelMessageConverter.getMessageId(localizedMessage==null ? rawMessage : localizedMessage);
	}

	private static String getLocalized(LogRecord r, Locale locale)
	{
		String localizedMessage = null;
		ResourceBundle resourceBundle = r.getResourceBundle();
		String resourceBundleName = r.getResourceBundleName();
		if (resourceBundleName != null) {
			String defaultMessage = r.getMessage(); 
			if (defaultMessage != null) {
				String messageKey = defaultMessage.replace(' ','.');
				localizedMessage = Messages.getStringFromBundle(resourceBundle, resourceBundleName, messageKey, locale, defaultMessage);
				if (defaultMessage.equals(localizedMessage)) {
					localizedMessage = null;
				}
			}
		}
		return localizedMessage;
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.websphere.logging.hpel.reader.RepositoryLogRecord#getExtension(java.lang.String)
	 */
	@Override
	public String getExtension(String name) {
		return extensions.get(name);
	}

	/* (non-Javadoc)
	 * @see com.ibm.websphere.logging.hpel.reader.RepositoryLogRecord#getExtensions()
	 */
	@Override
	public Map<String, String> getExtensions() {
		return extensions;
	}

	/* (non-Javadoc)
	 * @see com.ibm.websphere.logging.hpel.reader.RepositoryLogRecord#getFormattedMessage()
	 */
	@Override
	public String getFormattedMessage() {
		String result = localizedMessage==null ? rawMessage : localizedMessage;
		if (parameters != null) {
			try {
				return MessageFormat.format(result, parameters);
			} catch (IllegalArgumentException iae) {
				// In case of problem with positional parameters fall through and
				// return unformatted string.
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see com.ibm.websphere.logging.hpel.reader.RepositoryLogRecord#getLocalizable()
	 */
	@Override
	public int getLocalizable() {
		return localizable;
	}

	/* (non-Javadoc)
	 * @see com.ibm.websphere.logging.hpel.reader.RepositoryLogRecord#getLocalizedMessage()
	 */
	@Override
	public String getLocalizedMessage() {
		return localizedMessage;
	}

	/* (non-Javadoc)
	 * @see com.ibm.websphere.logging.hpel.reader.RepositoryLogRecord#getLoggerName()
	 */
	@Override
	public String getLoggerName() {
		return loggerName;
	}

	/* (non-Javadoc)
	 * @see com.ibm.websphere.logging.hpel.reader.RepositoryLogRecord#getMessageID()
	 */
	@Override
	public String getMessageID() {
		return messageID;
	}

	/* (non-Javadoc)
	 * @see com.ibm.websphere.logging.hpel.reader.RepositoryLogRecord#getMessageLocale()
	 */
	@Override
	public String getMessageLocale() {
		return messageLocale;
	}

	/* (non-Javadoc)
	 * @see com.ibm.websphere.logging.hpel.reader.RepositoryLogRecord#getParameters()
	 */
	@Override
	public Object[] getParameters() {
		return parameters;
	}

	/* (non-Javadoc)
	 * @see com.ibm.websphere.logging.hpel.reader.RepositoryLogRecord#getRawData()
	 */
	@Override
	public byte[] getRawData() {
		return rawData;
	}

	/* (non-Javadoc)
	 * @see com.ibm.websphere.logging.hpel.reader.RepositoryLogRecord#getRawMessage()
	 */
	@Override
	public String getRawMessage() {
		return rawMessage;
	}

	/* (non-Javadoc)
	 * @see com.ibm.websphere.logging.hpel.reader.RepositoryLogRecord#getResourceBundleName()
	 */
	@Override
	public String getResourceBundleName() {
		return resourceBundleName;
	}

	/* (non-Javadoc)
	 * @see com.ibm.websphere.logging.hpel.reader.RepositoryLogRecord#getSequence()
	 */
	@Override
	public long getSequence() {
		return sequence;
	}

	/* (non-Javadoc)
	 * @see com.ibm.websphere.logging.hpel.reader.RepositoryLogRecord#getSourceClassName()
	 */
	@Override
	public String getSourceClassName() {
		return sourceClassName;
	}

	/* (non-Javadoc)
	 * @see com.ibm.websphere.logging.hpel.reader.RepositoryLogRecord#getSourceMethodName()
	 */
	@Override
	public String getSourceMethodName() {
		return sourceMethodName;
	}

	/* (non-Javadoc)
	 * @see com.ibm.websphere.logging.hpel.reader.RepositoryLogRecord#getStackTrace()
	 */
	@Override
	public String getStackTrace() {
		return stackTrace;
	}

	/* (non-Javadoc)
	 * @see com.ibm.websphere.logging.hpel.reader.RepositoryLogRecordHeader#getLevel()
	 */
	@Override
	public Level getLevel() {
		return level;
	}

	/* (non-Javadoc)
	 * @see com.ibm.websphere.logging.hpel.reader.RepositoryLogRecordHeader#getMillis()
	 */
	@Override
	public long getMillis() {
		return millis;
	}

	/* (non-Javadoc)
	 * @see com.ibm.websphere.logging.hpel.reader.RepositoryLogRecordHeader#getRepositoryPointer()
	 */
	@Override
	public RepositoryPointer getRepositoryPointer() {
		throw new UnsupportedOperationException("Repository Pointer property is not applicable to this wrapper implementation.");
	}

	/* (non-Javadoc)
	 * @see com.ibm.websphere.logging.hpel.reader.RepositoryLogRecordHeader#getThreadID()
	 */
	@Override
	public int getThreadID() {
		return threadID;
	}

}