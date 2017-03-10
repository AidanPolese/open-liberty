/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

// The primary purpose of this file is to list all of the name token
// names / prefixes that we are using to avoid collisions.  Lets keep
// them alphabetical, and link to the part where they are used and/or
// give a description of what they are used for.

// --------------------------------------------------------------------
// 1234567890123456 Level Description
// --------------------------------------------------------------------

// BBGZAC*          SYS   Angel client process data post-named-angel.
//                        The '*' is a 2 byte hex angel anchor ID +
//                        the 8 byte STOKEN in hex.
// BBGZACPD         HOME  Angel client process data.
// BBGZACPD*        HOME  Angel client process data post-named-angel.
//                        The '*' is a 3 byte angel anchor ID.

// BBGZAPD_         HOME  Angel process data
// BBGZAPD_*        SYS   Angel process data (* is STOKEN)

// BBGZARMV*        HOME  Angel process saving an old ARMV
//                        The '*' is the ARMV instance count.

// --------------------------------------------------------------------
// 1234567890123456 Level Description
// --------------------------------------------------------------------

// BBGZCMPD*        HOME  Server common function module process data
//                        The '*' is the token provided by the angel.

// BBGZLOCL         HOME  Store the address of this server's LOCL

// BBGZOAM*         SYS   Angel ARMV attachment for server.
//                        The '*' is a one byte count + 8 byte stoken
// BBGZOAN*         SYS   Angel ARMV attachment for client.
//                        The '*' is a one byte count + 8 byte stoken

// BBGZSHR_*        SYS   WOLA BBOASHR control block (* is wola group name)

// BBGZSPD_         HOME  Server process data control block

// BBGZTRGOO        HOME  Tracing data control block

// --------------------------------------------------------------------
// 1234567890123456 Level Description
// --------------------------------------------------------------------

// BBGZ_KEY8_METALC HOME  Unauthorized metal C environment for server

// BBGZ_LC_GLOBAL_L TASK  Local comm global lock.
//                        ** TODO what is this used for?  **

// BBGZ_OLD_CGOO_PT SYS   Pointer to old CGOO when COLD=Y

// BBGZ_RAS_LVL_PTR HOME  RAS aggregate trace level

// BBGZ_RAS_ZOSTEST HOME  unit test stuff

// BBOZ*            PRI   WOLA registration (* is registration name)
// BBOa*            HOME  WOLA caching bind information in client.
//                        The '*' is the registration name.  Note the
//                        lower case A, it is intentional (tWAS uses
//                        an upper case A).
