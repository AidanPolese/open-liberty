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
#include <metal.h>
#include <stdlib.h>
#include <string.h>

#include "include/mvs_resmgr.h"
#include "include/mvs_utils.h"

/* List form of RESMGR ADD macro */
__asm(" RESMGR ADD,MF=L" : "DS"(listrma));

/* List form of RESMGR DELETE macro */
__asm(" RESMGR DELETE,MF=L" : "DS"(listrmd));

/* Add a resource manager */
int addResourceManager(int* token_p, void* param_p, int type,
                       rmgr_entry_t* rmgr_entry)
{
  int rc = -1;

  struct parm31 {
    char buffrma[sizeof(listrma)];
    int token;
    char param_data[8];
    rmgr_entry_t* __ptr32 rmgr_entry;
    int rc;
  };

  struct parm31* parm_p = __malloc31(sizeof(struct parm31));
  
  if (parm_p != NULL)
  {
    memcpy(parm_p->buffrma, &listrma, sizeof(listrma));
    parm_p->token = 0;
    memcpy(parm_p->param_data, param_p, sizeof(parm_p->param_data));
    parm_p->rmgr_entry = rmgr_entry;

    if (type == BBOZRMGR_TYPE_AS)
    {
      __asm(" SAM31\n"
            " SYSSTATE AMODE64=NO\n"
            " RESMGR ADD,TOKEN=(%1),TYPE=ADDRSPC,ASID=CURRENT,"
            "ROUTINE=(BRANCH,(%2)),PARAM=(%3),MF=(E,(%4))\n"
            " ST 15,%0\n"
            " SYSSTATE AMODE64=YES\n"
            " SAM64":
            "=m"(parm_p->rc) :
            "r"(&(parm_p->token)),"r"(parm_p->rmgr_entry),
            "r"(parm_p->param_data),"r"(parm_p->buffrma) :
            "r0","r1","r14","r15");

      rc = parm_p->rc;
    }
    else if (type == BBOZRMGR_TYPE_TASK)
    {
      __asm(" SAM31\n"
            " SYSSTATE AMODE64=NO\n"
            " RESMGR ADD,TOKEN=(%1),TYPE=TASK,TCB=CURRENT,ASID=CURRENT,"
            "ROUTINE=(BRANCH,(%2)),PARAM=(%3),MF=(E,(%4))\n"
            " ST 15,%0\n"
            " SYSSTATE AMODE64=NO\n"
            " SAM64" :
            "=m"(parm_p->rc) :
            "r"(&(parm_p->token)),"r"(parm_p->rmgr_entry),
            "r"(parm_p->param_data),"r"(parm_p->buffrma) :
            "r0","r1","r14","r15");

      rc = parm_p->rc;
    }
    else if (type == BBOZRMGR_TYPE_ALLTASKS)
    {
        __asm(" SAM31\n"
              " SYSSTATE AMODE64=NO\n"
              " RESMGR ADD,TOKEN=(%1),TYPE=TASK,TCB=ALL,ASID=CURRENT,"
              "ROUTINE=(BRANCH,(%2)),PARAM=(%3),MF=(E,(%4))\n"
              " ST 15,%0\n"
              " SYSSTATE AMODE64=NO\n"
              " SAM64" :
              "=m"(parm_p->rc) :
              "r"(&(parm_p->token)),"r"(parm_p->rmgr_entry),
              "r"(parm_p->param_data),"r"(parm_p->buffrma) :
              "r0","r1","r14","r15");

        rc = parm_p->rc;
    }

    if (token_p != NULL) *token_p = parm_p->token;
    free(parm_p);
  }

  return rc;
}

int addResourceManagerForAnotherTask(int* token_p, void* param_p, tcb* tcb_p, rmgr_entry_t* rmgr_entry)
{
    int rc = -1;

    struct parm31 {
      char buffrma[sizeof(listrma)];
      int token;
      char param_data[8];
      rmgr_entry_t* __ptr32 rmgr_entry;
      tcb* tcb_p;
      int rc;
    };

    struct parm31* parm_p = __malloc31(sizeof(struct parm31));

    if (parm_p != NULL)
    {
      memcpy(parm_p->buffrma, &listrma, sizeof(listrma));
      parm_p->token = 0;
      memcpy(parm_p->param_data, param_p, sizeof(parm_p->param_data));
      parm_p->rmgr_entry = rmgr_entry;
      parm_p->tcb_p = tcb_p;

      __asm(" SAM31\n"
            " SYSSTATE AMODE64=NO\n"
            " RESMGR ADD,TOKEN=(%1),TYPE=TASK,TCB=(%5),ASID=CURRENT,"
            "ROUTINE=(BRANCH,(%2)),PARAM=(%3),MF=(E,(%4))\n"
            " ST 15,%0\n"
            " SYSSTATE AMODE64=NO\n"
            " SAM64" :
            "=m"(parm_p->rc) :
            "r"(&(parm_p->token)),"r"(parm_p->rmgr_entry),
            "r"(parm_p->param_data),"r"(parm_p->buffrma),
            "r"(parm_p->tcb_p):
            "r0","r1","r14","r15");

      rc = parm_p->rc;
      if (token_p != NULL) *token_p = parm_p->token;

      free(parm_p);
    }

    return rc;
}

/* Delete a resource manager */
int deleteResourceManager(int* token_p, int type)
{
  int rc = -1;

  struct parm31 {
    char buffrmd[sizeof(listrmd)];
    int token;
    int rc;
  };

  struct parm31* parm_p = __malloc31(sizeof(struct parm31));

  if (parm_p != NULL)
  {
    memcpy(parm_p->buffrmd, &listrmd, sizeof(parm_p->buffrmd));
    parm_p->token = *token_p;
    
    if (type == BBOZRMGR_TYPE_AS)
    {
      __asm(" SAM31\n"
            " SYSSTATE AMODE64=NO\n"
            " RESMGR DELETE,TOKEN=(%1),TYPE=ADDRSPC,ASID=CURRENT,MF=(E,(%2))\n"
            " ST 15,%0\n"
            " SYSSTATE AMODE64=YES\n"
            " SAM64" :
            "=m"(parm_p->rc) :
            "r"(&(parm_p->token)),"r"(parm_p->buffrmd) :
            "r0","r1","r14","r15");
    }
    else if (type == BBOZRMGR_TYPE_TASK)
    {
        __asm(" SAM31\n"
              " SYSSTATE AMODE64=NO\n"
              " RESMGR DELETE,TOKEN=(%1),TYPE=TASK,TCB=CURRENT,ASID=CURRENT,MF=(E,(%2))\n"
              " ST 15,%0\n"
              " SYSSTATE AMODE64=YES\n"
              " SAM64" :
              "=m"(parm_p->rc) :
              "r"(&(parm_p->token)),"r"(parm_p->buffrmd) :
              "r0","r1","r14","r15");
    }
    else if (type == BBOZRMGR_TYPE_ALLTASKS)
    {
        __asm(" SAM31\n"
              " SYSSTATE AMODE64=NO\n"
              " RESMGR DELETE,TOKEN=(%1),TYPE=TASK,TCB=ALL,ASID=CURRENT,MF=(E,(%2))\n"
              " ST 15,%0\n"
              " SYSSTATE AMODE64=YES\n"
              " SAM64" :
              "=m"(parm_p->rc) :
              "r"(&(parm_p->token)),"r"(parm_p->buffrmd) :
              "r0","r1","r14","r15");
    }

    rc = parm_p->rc;

    free(parm_p);
  }

  return rc;
}

int deleteResourceManagerForAnotherTask(int* token_p, tcb* tcb_p)
{
    int rc = -1;

    struct parm31 {
      char buffrmd[sizeof(listrmd)];
      int token;
      tcb* tcb_p;
      int rc;
    };

    struct parm31* parm_p = __malloc31(sizeof(struct parm31));

    if (parm_p != NULL)
    {
      memcpy(parm_p->buffrmd, &listrmd, sizeof(listrmd));
      parm_p->token = *token_p;
      parm_p->tcb_p = tcb_p;

      __asm(" SAM31\n"
            " SYSSTATE AMODE64=NO\n"
            " RESMGR DELETE,TOKEN=(%1),TYPE=TASK,TCB=(%3),ASID=CURRENT,MF=(E,(%2))\n"
            " ST 15,%0\n"
            " SYSSTATE AMODE64=NO\n"
            " SAM64" :
            "=m"(parm_p->rc) :
            "r"(&(parm_p->token)),"r"(parm_p->buffrmd),
            "r"(parm_p->tcb_p):
            "r0","r1","r14","r15");

      rc = parm_p->rc;

      free(parm_p);
    }

    return rc;
}
