/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#ifndef _BBOZ_BBGZARMV_H
#define _BBOZ_BBGZARMV_H

/**@file
 * Angel Replaceable Module Vector
 */

/*
 * We have a separate name token prefix for ARMV attachments for a server and
 * a client.  This is because we can have both a server and a client in the
 * same address space.  We do not want the client to be freeing the server's
 * ARMV attachments, and vice versa.
 *
 * I don't remember what OAM stands for, so the client prefix is OAN.  It
 * probably makes no sense if you know what OAM stands for.
 */
#define ARMV_ATTACHMENT_NAME_TOKEN_PREFIX "BBGZOAM"
#define ARMV_ATTACHMENT_CLIENT_NAME_TOKEN_PREFIX "BBGZOAN"

#pragma pack(1)

typedef struct bbgzarmv_usecount_s bbgzarmv_usecount_s;
struct bbgzarmv_usecount_s
{
  int inactive : 1;
  int count  : 31;
};

/**
 * The ARMV control block represents a loaded copy of the dynamic replaceable
 * module.  The current ARMV is always hung off of the SGOO.  When a new copy
 * of the dynamic replaceable module is loaded, a new ARMV is created and
 * hung off of the SGOO.  When a server has registered and is invoking code
 * in the ARMV, it increments the use count in the ARMV.  If a new ARMV is
 * created and there are still servers using the old ARMV, a name token is
 * created to represent the old ARMV and the new ARMV is hung off the SGOO.
 * Then later when all servers using the old ARMV have stopped, the ARMV is
 * cleaned up and its storage is released.
 */
typedef struct bbgzarmv bbgzarmv;
struct bbgzarmv
{
  unsigned char            bbgzarmv_eyecatcher[8];     /* 0x000*/
  short                    bbgzarmv_version;           /* 0x008*/
  short                    bbgzarmv_length;            /* 0x00A*/
  bbgzarmv_usecount_s      bbgzarmv_usecount;          /* 0x00C*/
  long long                bbgzarmv_drm_len;           /* 0x010*/
  void*                    bbgzarmv_drm_mod_p;         /* 0x018*/
  struct bbgzadrm*         bbgzarmv_drm;               /* 0x020*/
  unsigned char            bbgzarmv_drm_del_token[8];  /* 0x028*/
  unsigned char            bbgzarmv_instancecount;     /* 0x030*/
  unsigned char            _reserved1[3];              /* 0x031*/
  unsigned char            _reserved2[76];             /* 0x034*/
};                                                     /* 0x080*/

#pragma pack(reset)

#endif
