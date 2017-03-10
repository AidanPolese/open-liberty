/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

/**
 * Authorized services needed by the WOLA channel.
 */
#ifndef SERVER_WOLA_SERVICES_H_
#define SERVER_WOLA_SERVICES_H_

#include "util_registry.h"
#include "client_wola_stubs.h"
#include "bboaims.h"

#include "server_local_comm_client.h"

/**
 * Params for @c pc_getClientService
 */
typedef struct  {
    unsigned char registration[17];                                  //!< Input - The client registration name (16+1 bytes to allow for null term)
    unsigned char serviceName[257];                                  //!< Input - The client service name (256+1 bytes to allow for null term)
    unsigned char wolaGroup[9];                                      //!< Input - The wola group name (8+1 bytes to allow for null term).
    int timeout_s;
    long long waiterToken;                                           //!< Input - If we make a waiter, the unique token to use.
    LocalCommClientConnectionHandle_t * client_conn_handle_p;        //!< Output - To contain the client connection handle
    int * return_code_p;                                             //!< Output - The pc routine return code
    int * iean4rt_rc_p;                                              //!< Output - The rc from iean4rt
    int * getClientService_rc_p;                                     //!< Output - The rc from getClientService (internal function)
} pc_wolaServiceQueueParms;

/**
 * Search the available queue for the given registration and service
 * name. If found, return the LocalCommClientConnectionHandle and
 * remove the available service from the queue. If not found, create
 * a wait service with a PEToken, add the service to the wait queue,
 * and pause the service until unpaused. When unpaused, the
 * LocalCommClientConnectionHandle should have been added to the service.
 * Return that value.
 *
 * @param pc_wolaServiceQueueParms
*/
void pc_getClientService(pc_wolaServiceQueueParms* parms);

/**
 * Parms for @c pc_cancelClientService
 */
typedef struct {
    unsigned char wolaGroup[8];                                      //!< Input - The wola group name, padded to 8 bytes
    unsigned char registration[16];                                  //!< Input - The client registration name, padded to 16 bytes.
    long long waiterToken;                                           //!< Input - If we make a waiter, the unique token to use.
} pc_wolaCancelClientService_parms;

/**
 * Attempt to cancel a client waiter.
 */
void pc_cancelClientService(pc_wolaCancelClientService_parms* parms);

/**
 * Parms for @c pc_attachToBboashr, @c pc_detachFromBboashr
 */
typedef struct {
    char wola_group[9];                 //!< Input - The wola group name (8+1 bytes to allow for null term).
    RegistryToken registry_token;       //!< Input/Output - The registry token that indirectly refers to the bboashr address
    RegistryToken * registry_token_p;   //!< Output - To contain the registry token that indirectly refers to the bboashr address
    int * return_code_p;                //!< Output - The pc routine return code
    int * iean4rt_rc_p;                 //!< Output - The rc from iean4rt
    int * iean4cr_rc_p;                 //!< Output - The rc from iean4cr
    int * registry_rc_p;                //!< Output - The rc from util_registry
} pc_attachToBboashr_parms;

/**
 * Attach to the shared memory area for the given wola group. If the SMA doesn't exist,
 * then create it and attach.
 *
 * This method passes back a registry token that should be used by subsequent calls to
 * detachFromBboashr. The registry token indirectly refers to the address of the shared memory area.
 *
 * @param pc_attachToBboashr_parms
 */
void pc_attachToBboashr(pc_attachToBboashr_parms * parms);

/**
 * Detach from the wola group shared memory area.  
 *
 * Note: this method performs LOCAL affinity detach only, not SYSTEM affinity.
 *
 * @param pc_attachToBboashr_parms - The parms contain a registry token that was returned from
 *                                   a previous call to pc_attachToBboashr.  The registry token
 *                                   indirectly refers to the address of the shared memory area,
 *                                   which is needed in order to detach.
 */
void pc_detachFromBboashr(pc_attachToBboashr_parms * parms);

/**
 * Parms for @c pc_advertiseWolaServer, @c pc_deadvertiseWolaServer
 */
typedef struct {
    char wola_group[9];                 //!< Input - The wola group name (8+1 bytes to allow for null term).
    char wola_name2[9];                 //!< Input - The wola 2nd name (8+1 bytes to allow for null term).
    char wola_name3[9];                 //!< Input - The wola 3rd name (8+1 bytes to allow for null term).
    char bboacall_module_name[4096];          //!< Input - The bboacall load mudule path
    RegistryToken registry_token;       //!< Input/Output - The registry token that indirectly refers to the ENQ
    RegistryToken * registry_token_p;   //!< Output - To contain the registry token that indirectly refers to the ENQ
    int * return_code_p;                //!< Output - The pc routine return code
    int * registry_rc_p;                //!< Output - The rc from util_registry
    int * getIPT_TToken_rc_p;           //!< Output - The rc from get_IPTTToken
    int * load_bboacall_rc_p;           //!< Output - The rc from load_from hfs
} pc_advertise_parms;


/**
 * BBOACALL structure mapping based on a const struct BBGAXVEC
 * create the DSECT (bboavec.s) based on this mapping in the listing
 * In the GNUMAKE file, -eBBGAXVEC is the entry point and the module name is BBOACALL
*/
typedef struct bboacall{
    unsigned char  eyecatcher[8];      /**< Eye catcher 'BBOAXVEC' */
    unsigned char  build_date[8];      /**< date                   */
    unsigned char  build_time[8];      /**< time                   */
    short int      wasVersion;         /**< Version                */
    short int      stubVecSlotlevel;   /**< stub vector slot level */
    unsigned int   flags;
    void (*asm_wola_reg)(void) ;       /**< function ptr of BBOASREG in bboasreg.s */
    int (*wola_register)(char*, char*, char*, char*, int*, int*, int*, struct bboapc1p*, int*, int*) ; /**< function ptr of ClientRegister in client_wola_stubs.mc */

    void (*asm_wola_urg)(void) ;       /**< function ptr of BBOASURG in bboasurg.s */
    int (*wola_unregister)(char*, int*,  struct bboapc1p*, int*, int*) ;  /**< function ptr of ClientUnRegister in client_wola_stubs.mc */

    void (*asm_wola_inv)(void) ;       /**< function ptr of BBOASINV in bboasinv.s */
    int (*wola_invoke)(char*, int*, char*, int*, char*, int*, char*, int*, int*, struct bboapc1p*, int*, int*, int*) ;  /**< function ptr of ClientInvoke in client_wola_stubs.mc */

    void (*asm_wola_srv)(void) ;       /**< function ptr of BBOASSRV in bboassrv.s */
    int (*wola_host_service)(char*, char*, int*, char*, int*, char*, int*, struct bboapc1p*, int*, int*, int*) ; /**< function ptr of ClientHostService in client_wola_stubs.mc */

    void (*asm_wola_srp)(void) ;       /**< function ptr of BBOASSRP in bboassrp.s */
    int (*wola_send_response)(char*, char*, int*, struct bboapc1p*, int*, int*) ;  /**< function ptr of ClientSendResponse in client_wola_stubs.mc */

    void (*asm_wola_cnr)(void) ;       /**< function ptr of BBOASCNR in bboascnr.s */
    int (*wola_conn_release)(char*, struct bboapc1p*, int*, int*) ;  /**< function ptr of ClientConnectionRelease in client_wola_stubs.mc */

    void (*asm_wola_cng)(void) ;       /**< function ptr of BBOASCNG in bboascng.s */
    int (*wola_conn_get)(char*, char*, int*, struct bboapc1p* cicsParms_p, int*, int*) ; /**< function ptr of ClientConnectionGet in client_wola_stubs.mc */

    void (*asm_wola_srq)(void) ;       /**< function ptr of BBOASSRQ in bboassrq.s */
    int (*wola_send_request)(char*, int*, char*, int*, char*, int*, int*, int*, struct bboapc1p* cicsParms_p, int*, int*) ;  /**< function ptr of ClientSendRequest in client_wola_stubs.mc */

    void (*asm_wola_get)(void) ;       /**< function ptr of BBOASGET in bboasget.s */
    int (*wola_get_data)(char*, char*, int*, struct bboapc1p* cicsParms_p, int*, int*, int*) ;  /**< function ptr of ClientGetData in client_wola_stubs.mc */

    void (*asm_wola_rcl)(void) ;       /**< function ptr of BBOASRCL in bboasrcl.s */
    int (*wola_rcv_respl)(char*, int*, int*, struct bboapc1p* cicsParms_p, int*, int*) ; /**< function ptr of ClientReceiveResponseLength in client_wola_stubs.mc */

    void (*asm_wola_rca)(void) ;       /**< function ptr of BBOASRCA in bboasrca.s */
    int (*wola_rcv_req_any)(char*, char*, char*, int*, int*, int*, struct bboapc1p* cicsParms_p, int*, int*) ;  /**< function ptr of ClientReceiveRequestAny in client_wola_stubs.mc */

    void (*asm_wola_rcs)(void) ;       /**< function ptr of BBOASRCS in bboasrcs.s */
    int (*wola_rcv_req_spec)(char*, char*, int*, int*, int*, struct bboapc1p* cicsParms_p, int*, int*) ;  /**< function ptr of ClientReceiveRequestSpecific in client_wola_stubs.mc */

    void (*asm_wola_srx)(void) ;       /**< function ptr of BBOASSRX in bboassrx.s */
    int (*wola_send_resp_exc)(char*, char*, int*, struct bboapc1p* cicsParms_p, int*, int*) ;  /**< function ptr of ClientSendResponseException in client_wola_stubs.mc */

    void (*asm_wola_gtx)(void) ;       /**< function ptr of BBOASGTX in bboasgtx.s */
    int (*wola_get_context)(char*, char*, int*, struct bboapc1p* cicsParms_p, int*, int*, int*) ;  /**< function ptr of ClientGetContext in client_wola_stubs.mc */

    void (*asm_wola_inf)(void) ;       /**< function ptr of BBOASINF in bboasinf.s */
    int (*wola_get_info)(char*, char*, char*, char*, struct bboaconn* , struct bboapc1p*, int*, int*) ;  /**< function ptr of ClientGetInfo in client_wola_stubs.mc */

} bboacall;

/**
 * BBOAMCSS structure , BBOAMCSS dsect done in BBOAMCST macro
 * This structure is common to tWAS and should not be changed
*/
typedef struct bboamcss{
    unsigned char  amcss_eye[8];       /**< Eye catcher 'BBOAMCSS'*/
    short int      amcss_slot_num;     /**< Slot number occupied  */
    short int      amcss_slot_ver;     /**< version of slot       */
    short int      amcss_ver;          /**< Version of the struct */
    unsigned char  reserved1[10];      /**< Filler1               */
    unsigned char  amcss_dead_stck[8]; /** Time placed on dead q  */
    void* __ptr32  amcss_dead_next;    /** next entry on dead q   */
    int            amcss_code_len;     /** length of BBOACALL     */
    void* __ptr32  amcss_code_ptr;     /** Pointer to BBOACALL    */
    void* __ptr32  amcss_vector_ptr;   /** Pointer to vector      */
} bboamcss;

/**
 * BBOAMCST structure , dsect done in BBOAMCST macro
 * This structure is common to tWAS and should not be changed
*/
typedef struct bboamcst{
    unsigned char  amcst_eye[8];            /**< Eye catcher 'BBOAMCST' */
    short int      amcst_ver;               /**< Version                */
    short int      amcst_flags;             /**< Flags                  */
    short int      amcst_num_slots;         /**< Number of slots        */
    short int      reserved1;               /**< Filler1                */
    bboamcss* __ptr32    amcst_dead_slots;  /**< Old Copies of BBOAMCSS */
    unsigned char  reserved2[32];           /**< Filler2                */
    bboamcss* __ptr32 amcst_slots[128];     /**< Array of Slot pointers */
} bboamcst;




/**
 * Advertise the WOLA server's presence by obtaining an ENQ using the server's WOLA 3-part name.
 *
 * The ENQ is tucked into a RegistryToken and returned to the caller.  The caller will later
 * invoke pc_deadvertiseWolaServer, passing back the RegistryToken, in order to release the
 * ENQ when the WOLA server is shutting down.
 *
 * @param parms - pc_advertise_parms
 */
void pc_advertiseWolaServer(pc_advertise_parms * parms) ;

/**
 * DE-advertise the WOLA server's presence by releasing the ENQ it previously obtained via
 * pc_advertiseWolaServer.  
 *
 * @param parms - pc_advertise_parms - contains the registry_token, previously obtained via
 *        pc_advertiseWolaServer, that refers to the ENQ that must be released.
 */
void pc_deadvertiseWolaServer(pc_advertise_parms * parms) ;

/**
 * Parms for @c pc_activateWolaRegistration, @c pc_deactivateWolaRegistration
 */
typedef struct {
    char wola_group[9];                 //!< Input - The wola group name (8+1 bytes to allow for null term).
    char wola_name2[9];                 //!< Input - The wola 2nd name (8+1 bytes to allow for null term).
    char wola_name3[9];                 //!< Input - The wola 3rd name (8+1 bytes to allow for null term).
    char useCicsTaskUserId;             //!< Input - Allow CICS to pass an alternate ACEE on WOLA requests.
    RegistryToken registry_token;       //!< Input/Output - The registry token that indirectly refers to the ENQ
    RegistryToken * registry_token_p;   //!< Output - To contain the registry token that indirectly refers to the ENQ
    int * return_code_p;                //!< Output - The pc routine return code
    int * registry_rc_p;                //!< Output - The rc from util_registry
    int * iean4rt_rc_p;                 //!< Output - The rc from iean4rt (used to lookup the BBOASHR name token)
    int * cell_pool_rc_p;               //!< Output - The rc from getCellPoolCell_rc (CSRC4GT1)
} pc_activateWolaRegistration_parms;

/**
 * Activate the WOLA registration (BBOARGE) for the server identified by the WOLA 3-part name.
 *
 * If the registration doesn't exist, create it.
 *
 * The BBOARGE pointer is tucked into a RegistryToken and returned to the caller.  The caller 
 * will later invoke pc_deactivateWolaRegistration, passing back the RegistryToken.
 *
 * @param parms - pc_activateWolaRegistration_parms
 */
void pc_activateWolaRegistration(pc_activateWolaRegistration_parms * parms) ;

/**
 * DE-activate the WOLA registration referenced by the RegistryToken in the parms.
 *
 * The RegistryToken was previously obtained via pc_activateWolaRegistration.
 *
 * @param parms - pc_activateWolaRegistration_parms - contains the registry_token, 
 *        previously obtained via pc_activateWolaRegistration, that refers to the 
 *        BBOARGE pointer.
 */
void pc_deactivateWolaRegistration(pc_activateWolaRegistration_parms * parms) ;

/**
 * Parms for @c pc_addOTMAAnchorToSPD, @c pc_removeOTMAAnchorFromSPD
 */
struct pc_addOTMAAnchorToSPD_parms
{
    otma_anchor_t    otma_anchor;
    otma_grp_name_t  otma_group_name;
    otma_srv_name_t  otma_member_name;
    otma_clt_name_t  otma_partner_name;
    struct pc_addOTMAAnchorToSPD_parms* nextAnchor_p;
} pc_addOTMAAnchorToSPD_parms;
//typedef struct addOTMAAnchorToSPD_parms pc_addOTMAAnchorToSPD_parms;

/**
 * Add the OTMA anchor to the server process data for use in the
 * event that the server crashed before OTMA_CLOSE can be called.
 *
 * @param pc_addOTMAAnchorToPSD_parms
*/
void pc_addOTMAAnchorToSPD(struct pc_addOTMAAnchorToSPD_parms * parms);

/**
 * Remove the OTMA anchor from the server process data. This
 * routine is called whenever OTMA close is called on an anchor.
 *
 * @param pc_addOTMAAnchorToPSD_parms
*/
void pc_removeOTMAAnchorFromSPD(struct pc_addOTMAAnchorToSPD_parms * parms);


#endif /* SERVER_WOLA_SERVICES_H_ */
