/*----------------------------------------------------------------------
 *
 *  Module Name:  BBOACSVL.H
 *
 *  Descriptive Name: Optimized Adapters CICS Link Server List
 *      Acronym:  N/A
 *
 *  Proprietary Statement
 *
 * IBM Confidential
 * OCO Source Materials
 * 5655-I35 (C) Copyright IBM Corp. 2010
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has been
 * deposited with the U.S. Copyright Office.
 * Status = H28W700
 *
 *  Function:  Keeps a list of CICS link servers for this address space,
 *             along with their state.  Use by the link servers and link
 *             task to differentiate WAS comm errors from server
 *             deregisters (which both expose themselves the same way).
 *
 *   Eye Catcher:
 *     Offset:
 *     Length:
 *
 *  Storage Attributes:
 *    Subpool:
 *    Key:
 *    Residency:
 *
 *  Size:   XXXX Bytes
 *
 *  Created by: User or System
 *
 *  Pointed to by: User or System
 *
 *  Serialization: None Required.
 *
 *  Headers included:
 *    C++: #include <private/bboacsvl.h>
 *
 *  Deleted by: System
 *
 *  Frequency:
 *    One per system
 *
 *  Dependencies:  None
 *
 *  No Message IDs used in this part:
 *
 *  Change Activity:
 *   $F003691-20607 , H28W700, 20100203, PDFG: Creation
 *   $PM90865       , H28W855, 20130706, JTM:  Added more data to node.
 *   $PI19688       , H28W800, 20140612, PDFG: Add a serialized counter used
 *                                             by link servers when starting
 *                                             link tasks.
 *   $PI24358       , H28W855, 20140904, PDFG: Pending unregister bit
 *   $PI52665       , H2W8555, 20151108, JTM:  Add new option to pass TransID/SYSID on
 *                                             EC LINK and option to set it (RTX),
 *                                             add support for RETRY mode,
 *                                             and long BBOC command strings.
 *
 *----------------------------------------------------------------------*/
#ifndef _BBOA_PRIVATE_BBOACSVL_H_INCLUDE
#define _BBOA_PRIVATE_BBOACSVL_H_INCLUDE

#include "bboieant.h"
#ifndef _BBOA_PRIVATE_BBOACLST_H_INCLUDE
#include "bboaclst.h"
#define _BBOA_PRIVATE_BBOACLST_H_INCLUDE
#endif


#ifndef TRUE
#define TRUE 1
#endif

#ifndef FALSE
#define FALSE 0
#endif

#define BBOA_LINK_SRVR_STATUS_NODE_EYE "BBOACSVL"

#define BBOA_LINK_SRVR_STATUS_ENQ_NAME "BBOACSVL_ENQ"

#define BBOA_LINK_SRVR_STATUS_TOKEN_NAME "BBOACSVLNAMETOKN"       /* PI19688A*/

/*---------------------------------------------------------------------------*/
/* Structure representing a link server                                      */
/*---------------------------------------------------------------------------*/
#pragma pack(1)
struct bboa_link_srvr_status_node
{
  char eye[8];      /* 0x00 "BBOACSRV"      */
  short version;    /* 0x08  V3             */
  short _rsvd1;     /* 0x0A                 */
  char regname[12]; /* 0x0C  Register name  */
  char dgname[8];   /* 0x1A        @PM90865A*/
  char ndname[8];   /* 0x24        @PM90865A*/
  char svrname[8];  /* 0x2C        @PM90865A*/
  char svcname[8];  /* 0x34        @PM90865A*/
  int  reuc;        /* 0x3C        @PM90865A*/
  int  reut;        /* 0x40        @PM90865A*/
  char reu[1];      /* 0x44        @PM90865A*/
  char txn[1];      /* 0x45        @PM90865A*/
  char sec[1];      /* 0x46        @PM90865A*/
  char lsync[1];    /* 0x47        @PM90865A*/
  char ltx[4];      /* 0x48        @PM90865A*/
  int  minconn;     /* 0x4C        @PM90865A*/
  int  maxconn;     /* 0x50        @PM90865A*/
  char urg_pend;    /* 0x54        @PI24358A*/
  char rtxp[1];     /* 0x55        @PI52665A*/
  char retry[1];    /* 0x56        @PI52665A*/
  char retmode[1];  /* 0x57        @PI52665A*/
  char rtx[4];      /* 0x58        @PI52665A*/
  char rtxsys[4];   /* 0x5C        @PI52665A*/
  int  retcnt;      /* 0x60        @PI52665A*/
  int  retint;      /* 0x64        @PI52665A*/
  char taskid[8];   /* 0x68        @PI53321A*/
                    /* 0x70        @PI52665A*/
  struct bboa_link_srvr_status_node* next_p;
};
#pragma pack(reset)

/*---------------------------------------------------------------------------*/
/* Structure representing the name token name used to anchor the link server */
/* status nodes, and the link server start task counter.                     */
/*---------------------------------------------------------------------------*/
#pragma pack(1)                                                   /* PI19688A*/
struct bboa_link_srvr_status_name_token                           /* PI19688M*/
{                                                                 /* PI19688M*/
  void* _ptr1;                                                    /* PI19688M*/
  void* _ptr2;                                                    /* PI19688M*/
  int volatile*  startCounter_p;                                  /* PI19688C*/
  bboa_link_srvr_status_node** head_p;                            /* PI19688M*/
};                                                                /* PI19688M*/
#pragma pack(reset)                                               /* PI19688A*/

/*---------------------------------------------------------------------------*/
/* Gets the lock on the link server list.                                    */
/*---------------------------------------------------------------------------*/
void
getLinkServerListLock()
{
  char enq_name[16];
  short enq_len;

  strncpy(enq_name, BBOA_LINK_SRVR_STATUS_ENQ_NAME, sizeof(enq_name));
  enq_len = strlen(BBOA_LINK_SRVR_STATUS_ENQ_NAME);
  if (enq_len > sizeof(enq_name)) enq_len = sizeof(enq_name);

  EXEC CICS ENQ RESOURCE(enq_name) LENGTH(enq_len) UOW;
}

/*---------------------------------------------------------------------------*/
/* Releases the lock on the link server list.                                */
/*---------------------------------------------------------------------------*/
void
releaseLinkServerListLock()
{
  char enq_name[16];
  short enq_len;

  strncpy(enq_name, BBOA_LINK_SRVR_STATUS_ENQ_NAME, sizeof(enq_name));
  enq_len = strlen(BBOA_LINK_SRVR_STATUS_ENQ_NAME);
  if (enq_len > sizeof(enq_name)) enq_len = sizeof(enq_name);

  EXEC CICS DEQ RESOURCE(enq_name) LENGTH(enq_len) UOW;
}

/*---------------------------------------------------------------------------*/
/* Gets the pointer to the link server start task counter.  The counter is   */
/* used by link servers to generate a unique REQID which is passed to the    */
/* EXEC CICS START TRANSID request to start a link task.                     */
/*---------------------------------------------------------------------------*/
int volatile*
getLinkServerStartCounter()                                       /* PI19688A*/
{
  char name[16];

  struct bboa_link_srvr_status_name_token token;

  int rc = 0;

  /*-------------------------------------------------------------------------*/
  /* Look up the name/token pair.                                            */
  /*-------------------------------------------------------------------------*/
  memset(&token, 0, sizeof(token));
  memcpy(name, BBOA_LINK_SRVR_STATUS_TOKEN_NAME, sizeof(name));

  ieantrt(IEANT_HOME_LEVEL,
          (char*)name,
          (char*)&token,
          &rc);

  /*-------------------------------------------------------------------------*/
  /* Note that not found is an error in this case.  The name token must have */
  /* been created if there is a link task being started.                     */
  /*-------------------------------------------------------------------------*/
  return token.startCounter_p;
}

/*---------------------------------------------------------------------------*/
/* Gets the head node of the link server list, via a name/token pair.  If    */
/* the name/token pair does not exist yet, create it as well as the storage  */
/* for the head node.                                                        */
/*---------------------------------------------------------------------------*/
bboa_link_srvr_status_node**
getHeadNode()
{
  char name[16];

  struct bboa_link_srvr_status_name_token token;                  /* PI19688C*/

  int rc = 0;

  /*-------------------------------------------------------------------------*/
  /* Look up the name/token pair.                                            */
  /*-------------------------------------------------------------------------*/
  memset(&token, 0, sizeof(token));
  memcpy(name, BBOA_LINK_SRVR_STATUS_TOKEN_NAME, sizeof(name));   /* PI19688C*/

  ieantrt(IEANT_HOME_LEVEL,
          (char*)name,
          (char*)&token,
          &rc);

  if (rc == IEANT_NOT_FOUND)
  {
    EXEC CICS GETMAIN SET(token.head_p) FLENGTH(8) INITIMG(0) SHARED;
    token.startCounter_p = (int*)(((char*)(token.head_p)) + 4);   /*2PI19688C*/

    ieantcr(IEANT_HOME_LEVEL,
            (char*)name,
            (char*)&token,
            IEANT_NOPERSIST,
            &rc);
  }

  return token.head_p;
}

/*---------------------------------------------------------------------------*/
/* Finds a node of the link server list.                                     */
/*---------------------------------------------------------------------------*/
bboa_link_srvr_status_node*
findNode(char* regname, char* taskid)
{
  bboa_link_srvr_status_node** head_p = getHeadNode();
  bboa_link_srvr_status_node* found_p = NULL;

  bboa_link_srvr_status_node* cur_p = *head_p;
  /*
  printf("!!Find: head = %p  *head = %p\n", head_p, *head_p);
  printf("!!Find: looking for %.12s\n", regname);
  printf("!!Find: looking for Task %.8s\n", taskid);
  printf("!!Find: looking for %.8X%.8X%.8X\n",
         *((int*)(regname + 0)),
         *((int*)(regname + 4)),
         *((int*)(regname + 8)));
  */

  while (cur_p != NULL)
  {
     /*
      printf("!!Find checking node %p: %.12s\n", cur_p, cur_p->regname);
      printf("!!Find checking taskid %.8s\n", cur_p->taskid);
      printf("!!Find checking %.8X%.8X%.8X\n",
             *((int*)(&(cur_p->regname[0]))),
             *((int*)(&(cur_p->regname[4]))),
             *((int*)(&(cur_p->regname[8]))));
     */
    if ( (memcmp(cur_p->regname, regname, sizeof(cur_p->regname)) == 0)  &&
         (memcmp(cur_p->taskid, taskid,   sizeof(cur_p->taskid )) == 0) )
    {
      //printf("!!Find found node \n");
      found_p = cur_p;
      cur_p = NULL;
    }
    else
    {

      cur_p = cur_p->next_p;
    }
  }
  //if(found_p == NULL)
    //  printf("!!Find node %.12s and taskid %.8s not found\n", regname, taskid);
  return found_p;
}

/*---------------------------------------------------------------------------*/
/* Creates a node in the link server list.  Assumes the caller already made  */
/* sure that the node did not already exist.                                 */
/*---------------------------------------------------------------------------*/
struct bboa_link_srvr_status_node*
createNode(char* regname, char* dgname, char* ndname, char* svrname,
               char* svcname, char* reu, int reuc, int reut, char* txn,
               char* sec, char* ltx, char* lsync,
               int minconn, int maxconn,
               char* rtxp, char* rtx, char* rtxsys,
               char* retry, char* retmode,
               int retcnt, int retint, char* taskid)         /* @PI52665C*/
{
  bboa_link_srvr_status_node** head_p = getHeadNode();
  bboa_link_srvr_status_node* new_p = NULL;

  int node_len = sizeof(struct bboa_link_srvr_status_node);

  EXEC CICS GETMAIN SET(new_p) FLENGTH(node_len) INITIMG(0) SHARED;

  memcpy(new_p->eye, BBOA_LINK_SRVR_STATUS_NODE_EYE, sizeof(new_p->eye));
  memcpy(new_p->regname, regname, sizeof(new_p->regname));
  memcpy(new_p->dgname, dgname, sizeof(new_p->dgname));      /* @PM90865*/
  memcpy(new_p->ndname, ndname, sizeof(new_p->ndname));      /* @PM90865*/
  memcpy(new_p->svrname, svrname, sizeof(new_p->svrname));   /* @PM90865*/
  memcpy(new_p->svcname, svcname, sizeof(new_p->svcname));   /* @PM90865*/
  memcpy(new_p->reu, reu, sizeof(new_p->reu));               /* @PM90865*/
  new_p->reuc = reuc;                                        /* @PM90865*/
  new_p->reut = reut;                                        /* @PM90865*/
  memcpy(new_p->txn, txn, sizeof(new_p->txn));               /* @PM90865*/
  memcpy(new_p->sec, sec, sizeof(new_p->sec));               /* @PM90865*/
  memcpy(new_p->ltx, ltx, sizeof(new_p->ltx));               /* @PM90865*/
  memcpy(new_p->lsync, lsync, sizeof(new_p->lsync));         /* @PM90865*/
  new_p->minconn = minconn;                                  /* @PM90865*/
  new_p->maxconn = maxconn;                                  /* @PM90865*/
  memcpy(new_p->rtxp, rtxp, sizeof(new_p->rtxp));           /* @PI52665A*/
  memcpy(new_p->rtx, rtx, sizeof(new_p->rtx));              /* @PI52665A*/
  memcpy(new_p->rtxsys, rtxsys, sizeof(new_p->rtxsys));     /* @PI52665A*/
  memcpy(new_p->retry, retry, sizeof(new_p->retry));        /* @PI52665A*/
  memcpy(new_p->retmode, retmode,
         sizeof(new_p->retmode));                           /* @PI52665A*/
  new_p->retcnt = retcnt;                                   /* @PI52665A*/
  new_p->retint = retint;                                   /* @PI52665A*/
  
  new_p->next_p = *head_p;
  memcpy(new_p->taskid, taskid, sizeof(new_p->taskid));      /* @PI53321*/
  *head_p = new_p;

  return new_p;
}

/*-----------------------------------------------------------------PI53321---*/
/* Removes all nodes from the link server list with a given REGNAME          */
/*---------------------------------------------------------------------------*/
void
removeNodes(char* regname)
{
  bboa_link_srvr_status_node** head_p = getHeadNode();

  bboa_link_srvr_status_node* cur_p = *head_p;
  bboa_link_srvr_status_node* prev_p = NULL;

  bboa_link_srvr_status_node* remove_p = NULL;

  /*
  printf("!!Remove: head = %p  *head = %p\n", head_p, *head_p);
  printf("!!Remove: looking for %.12s\n", regname);
  printf("!!Remove: looking for %.8X%.8X%.8X\n",
         *((int*)(regname + 0)),
         *((int*)(regname + 4)),
         *((int*)(regname + 8)));
  */
  while (cur_p != NULL)
  {
    /*
    printf("!!Remove checking node %p: %.12s\n", cur_p, cur_p->regname);
    printf("!!Remove checking %.8X%.8X%.8X\n",
           *((int*)(&(cur_p->regname[0]))),
           *((int*)(&(cur_p->regname[4]))),
           *((int*)(&(cur_p->regname[8]))));
    */
    if (memcmp(cur_p->regname, regname, sizeof(cur_p->regname)) == 0)
    {
      if (prev_p == NULL)
      {
        //printf("!!Remove matched head\n");
        *head_p = cur_p->next_p;
      }
      else
      {
        //printf("!!Remove matched node\n");
        prev_p->next_p = cur_p->next_p;
      }

      remove_p = cur_p;
      cur_p = cur_p->next_p;  /* PI53321 */
      //printf("!!Remove freeing %p\n", remove_p);
      EXEC CICS FREEMAIN DATAPOINTER(remove_p);
    }
    else
    {
      //printf("!!Remove did not match, advancing\n");
      prev_p = cur_p;
      cur_p = cur_p->next_p;
    }
  }

  if (remove_p != NULL)
  {
    //printf("!!Remove freeing %p\n", remove_p);
    EXEC CICS FREEMAIN DATAPOINTER(remove_p);
  }
}

/*---------------------------------------------------------------------------*/
/* Removes a node from the link server list.                                 */
/*---------------------------------------------------------------------------*/
void
removeNode(char* regname, char* taskid)  /* @PI533321 */
{
  bboa_link_srvr_status_node** head_p = getHeadNode();

  bboa_link_srvr_status_node* cur_p = *head_p;
  bboa_link_srvr_status_node* prev_p = NULL;

  bboa_link_srvr_status_node* remove_p = NULL;

  /*
  printf("!!Remove: head = %p  *head = %p\n", head_p, *head_p);
  printf("!!Remove: looking for %.12s\n", regname);
  printf("!!Remove: Looking for %.8s\n", taskid);
  printf("!!Remove: looking for %.8X%.8X%.8X\n",
         *((int*)(regname + 0)),
         *((int*)(regname + 4)),
         *((int*)(regname + 8)));
  */
  while (cur_p != NULL)
  {
    /*
    printf("!!Remove checking node %p: %.12s\n", cur_p, cur_p->regname);
    printf("!!Remove checking taskid %.8s\n", cur_p->taskid);
    printf("!!Remove checking %.8X%.8X%.8X\n",
           *((int*)(&(cur_p->regname[0]))),
           *((int*)(&(cur_p->regname[4]))),
           *((int*)(&(cur_p->regname[8]))));
    */
    if ((memcmp(cur_p->regname, regname, sizeof(cur_p->regname)) == 0) &&
        (memcmp(cur_p->taskid,  taskid,   sizeof(cur_p->taskid) ) == 0))    /* @PI53321C*/
    {
      if (prev_p == NULL)
      {
        //printf("!!Remove matched head\n");
        *head_p = cur_p->next_p;
      }
      else
      {
        //printf("!!Remove matched node\n");
        prev_p->next_p = cur_p->next_p;
      }

      remove_p = cur_p;
      cur_p = NULL;
    }
    else
    {
      //printf("!!Remove did not match, advancing\n");
      prev_p = cur_p;
      cur_p = cur_p->next_p;
    }
  }

  if (remove_p != NULL)
  {
    //printf("!!Remove freeing %p\n", remove_p);
    EXEC CICS FREEMAIN DATAPOINTER(remove_p);
  }
}

/*-----------------------------------------------------------------PI53321-*/
/* set any node with a given regname to unregister pending                 */
/*-------------------------------------------------------------------------*/
int setNodesUregPendingFlg(char* regname, int pending) {

  bboa_link_srvr_status_node** head_p = getHeadNode();

  bboa_link_srvr_status_node* cur_p = *head_p;
  bboa_link_srvr_status_node* prev_p = NULL;

  bboa_link_srvr_status_node* remove_p = NULL;
  int uregcnt = 0;

  while (cur_p != NULL)
  {
      if (memcmp(cur_p->regname, regname, sizeof(cur_p->regname)) == 0)
      {
           cur_p->urg_pend = pending;
           uregcnt++;
      }

      cur_p = cur_p->next_p;

  }
  return uregcnt;
}
/*----------------------------------------------------------------@PI53321---*/
/* sets the taskId of the node                                               */
/*---------------------------------------------------------------------------*/
void
setNodeTaskId(char* regname, char* taskid)
{
  bboa_link_srvr_status_node** head_p = getHeadNode();
  bboa_link_srvr_status_node* found_p = NULL;
  char defaultTaskNum[8] = "DEFAULT";

  bboa_link_srvr_status_node* cur_p = *head_p;
  /*
  printf("!!taskid. passed regname [%.12s]\n", regname);
  printf("!!taskid. passed taskid [%.8s]\n", taskid);
  printf("!!taskid. passed taskid size = %d\n",sizeof(taskid));
  */
  while (cur_p != NULL)
  {
   /*
    printf("!!taskid: head = %p  *head = %p\n", head_p, *head_p);
    printf("!!taskid: Next = %p\n",cur_p->next_p);
    printf("!!taskid: Checking regname [%.12s]\n", cur_p->regname);
    printf("!!taskid: checking taskid [%8s]\n",cur_p->taskid);
    printf("!!taskid: Checking taskid size = [%d]\n", sizeof(cur_p->taskid));
    */

    if ((memcmp(cur_p->regname, regname, sizeof(cur_p->regname)) == 0) &&
        (memcmp(cur_p->taskid, defaultTaskNum, 7) == 0))
    {
        strncpy(cur_p->taskid, taskid, sizeof(cur_p->taskid));
        cur_p = NULL;
      //  printf("!!taskid: regname %.12s and taskid %.8s found!!!!\n",regname, taskid);
    } else {
      //  printf("!!taskid: regname %.12s and taskid %.8s Not found!!!!\n",regname, defaultTaskNum);
        cur_p = cur_p->next_p;
    }
  }
}

/*-----------------------------------------------------------------@PI53321--*/
/* Removes all link server from the link server list. for a given regname    */
/*---------------------------------------------------------------------------*/
void
removeLinkServers(char* regname)
{
  /*-------------------------------------------------------------------------*/
  /* Grab the lock.                                                          */
  /*-------------------------------------------------------------------------*/
  getLinkServerListLock();

  /*-------------------------------------------------------------------------*/
  /* Remove the node.                                                        */
  /*-------------------------------------------------------------------------*/
  removeNodes(regname);

  /*-------------------------------------------------------------------------*/
  /* Release the lock.                                                       */
  /*-------------------------------------------------------------------------*/
  releaseLinkServerListLock();
}

/*-----------------------------------------------------------------@PI53321--*/
/* Removes a link server from the link server list.                          */
/*---------------------------------------------------------------------------*/
void
removeLinkServer(char* regname, char* taskid)
{
  /*-------------------------------------------------------------------------*/
  /* Grab the lock.                                                          */
  /*-------------------------------------------------------------------------*/
  getLinkServerListLock();

  /*-------------------------------------------------------------------------*/
  /* Remove the node.                                                        */
  /*-------------------------------------------------------------------------*/
  removeNode(regname, taskid);  /* @PI53321C*/

  /*-------------------------------------------------------------------------*/
  /* Release the lock.                                                       */
  /*-------------------------------------------------------------------------*/
  releaseLinkServerListLock();
}

 /*--------------------------------------------------------------------------*/
 /* Gets the status node for a link server.  Optionally creates the node     */
 /* requested.                                                               */
 /*--------------------------------------------------------------------------*/
 struct bboa_link_srvr_status_node*
 findLinkServer(char* regname, char* taskid)                     /* @PI53321C*/
 {
   /*------------------------------------------------------------------------*/
   /* Grab the lock.                                                         */
   /*------------------------------------------------------------------------*/
   getLinkServerListLock();

   /*------------------------------------------------------------------------*/
   /* Find the node.                                                         */
   /*------------------------------------------------------------------------*/
   bboa_link_srvr_status_node* node_p = findNode(regname, taskid); /*@PI53321C*/

                                                                /* 7@PM90865D*/

   /*------------------------------------------------------------------------*/
   /* Release the lock.                                                      */
   /*------------------------------------------------------------------------*/
   releaseLinkServerListLock();

   return node_p;
 }

/*--------------------------------------------------------------- 35@PM90865A*/
/* Creates a new link server in the Link Server list if it is not already    */
/* there.                                                                    */
/*---------------------------------------------------------------------------*/
struct bboa_link_srvr_status_node*
createLinkServer(char* regname, char* dgname, char* ndname, char* svrname,
               char* svcname, char* reu, int reuc, int reut, char* txn,
               char* sec, char* ltx, char* lsync, int minconn, int maxconn,
               char* rtxp, char* rtx, char * rtxsys,
               char* retry, char* retmode,
               int retcnt, int retint, char* taskid)             /* @PI52665C*/
{
  /*-------------------------------------------------------------------------*/
  /* Grab the lock.                                                          */
  /*-------------------------------------------------------------------------*/
  getLinkServerListLock();

  /*-------------------------------------------------------------------------*/
  /* Find the node.                                                          */
  /*-------------------------------------------------------------------------*/
  bboa_link_srvr_status_node* node_p = findNode(regname, taskid);

  /*-------------------------------------------------------------------------*/
  /* If we did not find the node, create it now.                             */
  /*-------------------------------------------------------------------------*/
  if (node_p == NULL)
  {
    node_p = createNode(regname, dgname, ndname, svrname, svcname, reu, reuc,
                        reut, txn, sec, ltx, lsync, minconn, maxconn,
                        rtxp, rtx, rtxsys,
                        retry, retmode, retcnt, retint, taskid); /* @PI52665C*/
  }

  /*-------------------------------------------------------------------------*/
  /* Release the lock.                                                       */
  /*-------------------------------------------------------------------------*/
  releaseLinkServerListLock();

  return node_p;
}
/*------------------------------------------------------------------PI53321  */
/* set ureg pending for all link server entries associated with the register */
/* name                                                                      */
/*------------------------------------------------------------------PI53321  */
int setLinkServersUregPendingFlg(char* regname, int pending) {

  int changedServers = 0;
  /*-------------------------------------------------------------------------*/
  /* Grab the lock.                                                          */
  /*-------------------------------------------------------------------------*/
  getLinkServerListLock();

  changedServers = setNodesUregPendingFlg(regname, pending);

  /*-------------------------------------------------------------------------*/
  /* Release the lock.                                                       */
  /*-------------------------------------------------------------------------*/
  releaseLinkServerListLock();

  return changedServers;
}
/*------------------------------------------------------------------PI53321  */
/* The initial link server entry created by BBOC does not have a task id     */
/* because it is not known at the time. This is used to populate the taskid  */
/* once the BBO$ tran has started.                                            */
/*------------------------------------------------------------------PI53321  */
void setLinkServerTaskid(char* regname, char* taskid) {

   /*------------------------------------------------------------------------*/
   /* Grab the lock.                                                         */
   /*------------------------------------------------------------------------*/
   getLinkServerListLock();

   /*------------------------------------------------------------------------*/
   /* Find the node.                                                         */
   /*------------------------------------------------------------------------*/
   setNodeTaskId(regname, taskid);

                                                                /* 7@PM90865D*/

   /*------------------------------------------------------------------------*/
   /* Release the lock.                                                      */
   /*------------------------------------------------------------------------*/
   releaseLinkServerListLock();

}


#endif
