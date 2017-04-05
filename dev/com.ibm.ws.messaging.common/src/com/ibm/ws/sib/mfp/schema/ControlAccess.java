// Generated from C:\SIB\Code\WASX.SIB\dd\SIB\ws\code\sib.mfp.impl\src\com\ibm\ws\sib\mfp\schema\ControlSchema.schema: do not edit directly
package com.ibm.ws.sib.mfp.schema;
import com.ibm.ws.sib.mfp.jmf.impl.JSchema;
public final class ControlAccess {
  public final static JSchema schema = new ControlSchema();
  public final static int SUBTYPE = 0;
  public final static int PRIORITY = 1;
  public final static int RELIABILITY = 2;
  public final static int FLAGS = 3;
  public final static int ROUTINGDESTINATION = 103;
  public final static int IS_ROUTINGDESTINATION_EMPTY = 0;
  public final static int IS_ROUTINGDESTINATION_VALUE = 1;
  public final static int ROUTINGDESTINATION_VALUE_NAME = 4;
  public final static int ROUTINGDESTINATION_VALUE_MEID = 5;
  public final static int ROUTINGDESTINATION_VALUE_BUSNAME = 6;
  public final static int SOURCEMEUUID = 7;
  public final static int TARGETMEUUID = 8;
  public final static int TARGETDESTDEFUUID = 9;
  public final static int STREAMUUID = 10;
  public final static int PROTOCOLTYPE = 11;
  public final static int PROTOCOLVERSION = 12;
  public final static int GUARANTEEDXBUS = 104;
  public final static int IS_GUARANTEEDXBUS_EMPTY = 0;
  public final static int IS_GUARANTEEDXBUS_SET = 1;
  public final static int GUARANTEEDXBUS_SET_LINKNAME = 13;
  public final static int GUARANTEEDXBUS_SET_SOURCEBUSUUID = 14;
  public final static int BODY = 105;
  public final static int IS_BODY_EMPTY = 0;
  public final static int IS_BODY_ACKEXPECTED = 1;
  public final static int IS_BODY_SILENCE = 2;
  public final static int IS_BODY_ACK = 3;
  public final static int IS_BODY_NACK = 4;
  public final static int IS_BODY_PREVALUE = 5;
  public final static int IS_BODY_ACCEPT = 6;
  public final static int IS_BODY_REJECT = 7;
  public final static int IS_BODY_DECISION = 8;
  public final static int IS_BODY_REQUEST = 9;
  public final static int IS_BODY_REQUESTACK = 10;
  public final static int IS_BODY_REQUESTHIGHESTGENERATEDTICK = 11;
  public final static int IS_BODY_HIGHESTGENERATEDTICK = 12;
  public final static int IS_BODY_RESETREQUESTACK = 13;
  public final static int IS_BODY_RESETREQUESTACKACK = 14;
  public final static int IS_BODY_BROWSEGET = 15;
  public final static int IS_BODY_BROWSEEND = 16;
  public final static int IS_BODY_BROWSESTATUS = 17;
  public final static int IS_BODY_COMPLETED = 18;
  public final static int IS_BODY_DECISIONEXPECTED = 19;
  public final static int IS_BODY_CREATESTREAM = 20;
  public final static int IS_BODY_CREATEDURABLE = 21;
  public final static int IS_BODY_DELETEDURABLE = 22;
  public final static int IS_BODY_DURABLECONFIRM = 23;
  public final static int IS_BODY_AREYOUFLUSHED = 24;
  public final static int IS_BODY_FLUSHED = 25;
  public final static int IS_BODY_NOTFLUSHED = 26;
  public final static int IS_BODY_REQUESTFLUSH = 27;
  public final static int IS_BODY_REQUESTCARDINALITYINFO = 28;
  public final static int IS_BODY_CARDINALITYINFO = 29;
  public final static int BODY_ACKEXPECTED_TICK = 15;
  public final static int BODY_SILENCE_STARTTICK = 16;
  public final static int BODY_SILENCE_ENDTICK = 17;
  public final static int BODY_SILENCE_COMPLETEDPREFIX = 18;
  public final static int BODY_SILENCE_FORCE = 19;
  public final static int BODY_SILENCE_REQUESTEDONLY = 20;
  public final static int BODY_ACK_ACKPREFIX = 21;
  public final static int BODY_NACK_STARTTICK = 22;
  public final static int BODY_NACK_ENDTICK = 23;
  public final static int BODY_NACK_FORCE = 24;
  public final static int BODY_PREVALUE_STARTTICK = 25;
  public final static int BODY_PREVALUE_ENDTICK = 26;
  public final static int BODY_PREVALUE_VALUETICK = 27;
  public final static int BODY_PREVALUE_COMPLETEDPREFIX = 28;
  public final static int BODY_PREVALUE_FORCE = 29;
  public final static int BODY_PREVALUE_REQUESTEDONLY = 30;
  public final static int BODY_ACCEPT_TICK = 31;
  public final static int BODY_REJECT_STARTTICK = 32;
  public final static int BODY_REJECT_ENDTICK = 33;
  public final static int BODY_REJECT_RECOVERY = 34;
  public final static int BODY_REJECT_RMEUNLOCKCOUNT = 106;
  public final static int IS_BODY_REJECT_RMEUNLOCKCOUNT_UNSET = 0;
  public final static int IS_BODY_REJECT_RMEUNLOCKCOUNT_VALUE = 1;
  public final static int BODY_REJECT_RMEUNLOCKCOUNT_VALUE = 35;
  public final static int BODY_DECISION_STARTTICK = 36;
  public final static int BODY_DECISION_ENDTICK = 37;
  public final static int BODY_DECISION_COMPLETEDPREFIX = 38;
  public final static int BODY_REQUEST_FILTER = 39;
  public final static int BODY_REQUEST_DISCRIMINATOR = 40;
  public final static int BODY_REQUEST_SELECTORDOMAIN = 41;
  public final static int BODY_REQUEST_REJECTSTARTTICK = 42;
  public final static int BODY_REQUEST_GETTICK = 43;
  public final static int BODY_REQUEST_TIMEOUT = 44;
  public final static int BODY_REQUESTACK_DMEVERSION = 45;
  public final static int BODY_REQUESTACK_TICK = 46;
  public final static int BODY_REQUESTHIGHESTGENERATEDTICK_REQUESTID = 47;
  public final static int BODY_HIGHESTGENERATEDTICK_REQUESTID = 48;
  public final static int BODY_HIGHESTGENERATEDTICK_TICK = 49;
  public final static int BODY_RESETREQUESTACK_DMEVERSION = 50;
  public final static int BODY_RESETREQUESTACKACK_DMEVERSION = 51;
  public final static int BODY_BROWSEGET_BROWSEID = 52;
  public final static int BODY_BROWSEGET_SEQUENCENUMBER = 53;
  public final static int BODY_BROWSEGET_FILTER = 54;
  public final static int BODY_BROWSEGET_DISCRIMINATOR = 55;
  public final static int BODY_BROWSEGET_SELECTORDOMAIN = 56;
  public final static int BODY_BROWSEEND_BROWSEID = 57;
  public final static int BODY_BROWSEEND_EXCEPTIONCODE = 58;
  public final static int BODY_BROWSESTATUS_BROWSEID = 59;
  public final static int BODY_BROWSESTATUS_STATUS = 60;
  public final static int BODY_COMPLETED_STARTTICK = 61;
  public final static int BODY_COMPLETED_ENDTICK = 62;
  public final static int BODY_DECISIONEXPECTED_TICK = 63;
  public final static int BODY_CREATESTREAM_REQUESTID = 64;
  public final static int BODY_CREATESTREAM_SUBNAME = 65;
  public final static int BODY_CREATESTREAM_DISCRIMINATOR = 66;
  public final static int BODY_CREATESTREAM_SELECTOR = 67;
  public final static int BODY_CREATESTREAM_SELECTORDOMAIN = 68;
  public final static int BODY_CREATESTREAM_SECURITYSENTBYSYSTEM = 69;
  public final static int BODY_CREATESTREAM_SECURITYUSERID = 70;
  public final static int BODY_CREATESTREAM_NOLOCAL = 107;
  public final static int IS_BODY_CREATESTREAM_NOLOCAL_UNSET = 0;
  public final static int IS_BODY_CREATESTREAM_NOLOCAL_VALUE = 1;
  public final static int BODY_CREATESTREAM_NOLOCAL_VALUE = 71;
  public final static int BODY_CREATESTREAM_CLONED = 108;
  public final static int IS_BODY_CREATESTREAM_CLONED_UNSET = 0;
  public final static int IS_BODY_CREATESTREAM_CLONED_VALUE = 1;
  public final static int BODY_CREATESTREAM_CLONED_VALUE = 72;
  public final static int BODY_CREATEDURABLE_REQUESTID = 73;
  public final static int BODY_CREATEDURABLE_SUBNAME = 74;
  public final static int BODY_CREATEDURABLE_DISCRIMINATOR = 75;
  public final static int BODY_CREATEDURABLE_SELECTOR = 76;
  public final static int BODY_CREATEDURABLE_SELECTORDOMAIN = 77;
  public final static int BODY_CREATEDURABLE_SECURITYSENTBYSYSTEM = 78;
  public final static int BODY_CREATEDURABLE_SECURITYUSERID = 79;
  public final static int BODY_CREATEDURABLE_NOLOCAL = 109;
  public final static int IS_BODY_CREATEDURABLE_NOLOCAL_UNSET = 0;
  public final static int IS_BODY_CREATEDURABLE_NOLOCAL_VALUE = 1;
  public final static int BODY_CREATEDURABLE_NOLOCAL_VALUE = 80;
  public final static int BODY_CREATEDURABLE_CLONED = 110;
  public final static int IS_BODY_CREATEDURABLE_CLONED_UNSET = 0;
  public final static int IS_BODY_CREATEDURABLE_CLONED_VALUE = 1;
  public final static int BODY_CREATEDURABLE_CLONED_VALUE = 81;
  public final static int BODY_CREATEDURABLE_NAMESPACEMAP = 111;
  public final static int IS_BODY_CREATEDURABLE_NAMESPACEMAP_UNSET = 0;
  public final static int IS_BODY_CREATEDURABLE_NAMESPACEMAP_MAP = 1;
  public final static int BODY_CREATEDURABLE_NAMESPACEMAP_MAP_NAME = 82;
  public final static int BODY_CREATEDURABLE_NAMESPACEMAP_MAP_VALUE = 83;
  public final static int BODY_DELETEDURABLE_REQUESTID = 84;
  public final static int BODY_DELETEDURABLE_SUBNAME = 85;
  public final static int BODY_DELETEDURABLE_SECURITYUSERID = 86;
  public final static int BODY_DURABLECONFIRM_REQUESTID = 87;
  public final static int BODY_DURABLECONFIRM_STATUS = 88;
  public final static int BODY_AREYOUFLUSHED_REQUESTID = 89;
  public final static int BODY_NOTFLUSHED_REQUESTID = 90;
  public final static int BODY_NOTFLUSHED_COMPLETEDQOS = 91;
  public final static int BODY_NOTFLUSHED_COMPLETEDPRIORITY = 92;
  public final static int BODY_NOTFLUSHED_COMPLETEDPREFIX = 93;
  public final static int BODY_NOTFLUSHED_DUPLICATEQOS = 94;
  public final static int BODY_NOTFLUSHED_DUPLICATEPRIORITY = 95;
  public final static int BODY_NOTFLUSHED_DUPLICATEPREFIX = 96;
  public final static int BODY_REQUESTFLUSH_REQUESTID = 97;
  public final static int BODY_REQUESTFLUSH_INDOUBTDISCARD = 98;
  public final static int BODY_REQUESTCARDINALITYINFO_REQUESTID = 99;
  public final static int BODY_CARDINALITYINFO_REQUESTID = 100;
  public final static int BODY_CARDINALITYINFO_CURRENTCARDINALITY = 101;
  public final static int GATHERINGTARGETUUID = 112;
  public final static int IS_GATHERINGTARGETUUID_EMPTY = 0;
  public final static int IS_GATHERINGTARGETUUID_VALUE = 1;
  public final static int GATHERINGTARGETUUID_VALUE = 102;
}
