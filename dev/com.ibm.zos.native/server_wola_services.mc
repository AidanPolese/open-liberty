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
 * @file
 *
 * Assorted authorized routines used by the WOLA code.
 *
 */

#include <metal.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#include "include/gen/bpxzotcb.h"
#include "include/bpx_ipt.h"
#include "include/common_defines.h"
#include "include/mvs_abend.h"
#include "include/mvs_cell_pool_services.h"
#include "include/mvs_contents_supervisor.h"
#include "include/mvs_enq.h"
#include "include/mvs_iarv64.h"
#include "include/mvs_user_token_manager.h"
#include "include/mvs_utils.h"
#include "include/ras_tracing.h"
#include "include/server_local_comm_client.h"
#include "include/server_process_data.h"
#include "include/server_wola_registration_server.h"
#include "include/server_wola_services.h"
#include "include/server_wola_service_queues.h"
#include "include/server_wola_shared_memory_anchor_server.h"
#include "include/util_registry.h"

#include "include/angel_bgvt_services.h"
#include "include/mvs_storage.h"
#include "include/bpx_load.h"

#define RAS_MODULE_CONST  RAS_MODULE_SERVER_WOLA_SERVICES

#define LOAD_BBOACALL_OK 0
#define LOAD_BBOACALL_FAIL 4
#define LOAD_BBOACALL_NULL_PATH 8

/**
 * {@inheritDoc}
 *
 * PC routine for creating (if necessary) and attaching to the WOLA group shared memory area
 * anchored by BBOASHR.
 *
 * @return  parms->return_code_p = 8, if the MVS name token could not be created because it
 *          already exists; however the retrieve of the name token either failed or it 
 *          returned a NULL bboashr_p.
 *          parms->iean4rt_rc_p,
 *          parms->iean4cr_rc_p,
 *          parms->registry_rc_p,
 */
void pc_attachToBboashr(pc_attachToBboashr_parms *parms) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(1),
                    "pc_attachToBboashr entry",
                    TRACE_DATA_RAWDATA(sizeof(pc_attachToBboashr_parms), parms, "Parms"),
                    TRACE_DATA_END_PARMS);
    }

    long long user_token = getAddressSpaceSupervisorStateUserToken();

    // Lookup the bboashr pointer in the name token
    int rc;
    void * bboashr_p = getBboashrForWolaGroup(parms->wola_group, &rc ) ;

    if (bboashr_p != NULL) {

        // Found it! Attach to it.
        accessSharedAbove( bboashr_p, user_token );

    } else if (rc != 4) {

        // Unexpected failure from iean4rt.  Copy it back and return.
        memcpy_dk( parms->iean4rt_rc_p,
                   &rc,
                   sizeof(int),
                   8);
        return;

    } else {

        // The name token doesn't exist (which means the BBOASHR doesn't exist).
        // Create the shared memory area.
        // TODO: is fetchprotected?
        bboashr_p = getSharedAbove( WOLA_SMA_SIZE_MB, 0, user_token );

        // Immediately attach to it so we can initialize the BBOASHR.
        accessSharedAbove( bboashr_p, user_token );
        initializeBboashr( bboashr_p, parms->wola_group );

        // Create a name token for it.
        rc = createBboashrNameToken(parms->wola_group, bboashr_p);

        if (rc == 4) {
            // The name token could not be created because it already exists.
            // I.e Somebody else has already created it, which means they also 
            // already created the BBOASHR area.  Delete the SMA we just obtained.
            
            // Note: must detach both LOCAL and SYSTEM interest so that the shared 
            // memory is reclaimed.
            detachSharedAbove( bboashr_p, user_token, 0); 
            detachSharedAbove( bboashr_p, user_token, 1);

            // This time it best not be NULL...
            bboashr_p = getBboashrForWolaGroup(parms->wola_group, &rc );

            if (bboashr_p == NULL) {
                
                // Very unexpected. Copy back iean4rt rc and an error code and return.
                memcpy_dk( parms->iean4rt_rc_p,
                           &rc,
                           sizeof(int),
                           8);

                rc = 8;
                memcpy_dk( parms->return_code_p,
                           &rc,
                           sizeof(int),
                           8);

                return;
            }

            accessSharedAbove( bboashr_p, user_token );

        } else if (rc != 0 ) {

            // Unexpected failure from iean4cr.  Copy it back and return.
            memcpy_dk( parms->iean4cr_rc_p,
                       &rc,
                       sizeof(int),
                       8);
            return;
        }
    }

    // Wrap bboashr_p in a RegistryToken and return it to the caller.
    RegistryToken registry_token;
    rc = allocateRegistryTokenForBboashr( bboashr_p, &registry_token );

    if (rc == 0) {
        // Copy the token back to the key-8 caller
        memcpy_dk(parms->registry_token_p, 
                  &registry_token,
                  sizeof(RegistryToken),
                  8);

        // Copy back rc=0
        memcpy_dk(parms->return_code_p, 
                  &rc,
                  sizeof(int),
                  8);

    } else {

        // Some sort of registry failure. Copy back rc.
        memcpy_dk( parms->registry_rc_p,
                   &rc,
                   sizeof(int),
                   8 );

        // Delete the SMA we just obtained.
        // Note: must detach both LOCAL and SYSTEM interest so that the shared 
        // memory is reclaimed.
        detachSharedAbove( bboashr_p, user_token, 0); 
        detachSharedAbove( bboashr_p, user_token, 1);
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(2),
                    "pc_attachToBboashr exit",
                    TRACE_DATA_HEX_INT(rc, "rc"),
                    TRACE_DATA_RAWDATA(((rc != 0) ? 0 : sizeof(WolaSharedMemoryAnchor_t)), bboashr_p, "BBOASHR"),
                    TRACE_DATA_END_PARMS);
    }
}

/**
 * {@inheritDoc}
 *
 * PC routine for detaching from the WOLA group shared memory area (anchored by BBOASHR).
 */
void pc_detachFromBboashr(pc_attachToBboashr_parms *parms) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(11),
                    "pc_detachFromBboashr entry",
                    TRACE_DATA_RAWDATA(sizeof(pc_attachToBboashr_parms), parms, "Parms"),
                    TRACE_DATA_END_PARMS);
    }

    // Lookup the bboashr_p from the registry token, then detach from it.
    int registryFree_rc = -1;
    int rc;
    void * bboashr_p = getBboashrFromRegistryToken( &parms->registry_token, &rc );

    if (bboashr_p != NULL) {
        // Now detach.
        //
        // Note: we detach our LOCAL affinity/interest in the SMA.
        // The SYSTEM interest still exists, so the SMA won't go away until
        // a) someone issues a SYSTEM detach (we have no code to do this)
        // b) the machine is IPL'ed 
        //
        // The WOLA group SMA is supposed to stay around forever, so this
        // is all working as designed.
        //
        // Note: the detach will ABEND if it fails.
        
        long long user_token = getAddressSpaceSupervisorStateUserToken();
        detachSharedAbove( bboashr_p, user_token, 0);   

        // Free the registry token.  Not bothering to check the RC, but we do trace it.
        registryFree_rc = registryFree(&parms->registry_token, 0);

    } else {
        // Huh?  Soem registry error occurred. Copy back to caller and return.
        memcpy_dk( parms->registry_rc_p,
                   &rc,
                   sizeof(int),
                   8 );
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(12),
                    "pc_detachFromBboashr exit",
                    TRACE_DATA_HEX_INT(rc, "rc"),
                    TRACE_DATA_HEX_INT(registryFree_rc, "registryFree rc"),
                    TRACE_DATA_PTR(bboashr_p, "bboashr_p"),
                    TRACE_DATA_END_PARMS);
    }

    return;
}

/**
 * Construct the RNAME for the ENQ used by WOLA to advertise the server's presence.
 *
 * @param rname - Output - a char[BBGZ_ENQ_MAX_RNAME_LEN] that will contain the RNAME
 * @param wola_group - The wola group name
 * @param wola_name2 - The wola name 2nd part
 * @param wola_name3 - The wola name 3rd part
 *
 * @return rname
 */
char * buildWolaEnqRname(char * rname, char * wola_group, char * wola_name2, char * wola_name3) {

    memset(rname, 0, BBGZ_ENQ_MAX_RNAME_LEN);
    snprintf(rname, 
             BBGZ_ENQ_MAX_RNAME_LEN, 
             ADVERTISE_WOLA_SERVER_ENQ_RNAME_PATTERN,
             wola_group, 
             wola_name2,
             wola_name3);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(91),
                    "buildWolaEnqRname",
                    TRACE_DATA_RAWDATA(BBGZ_ENQ_MAX_RNAME_LEN, rname, "rname"),
                    TRACE_DATA_END_PARMS);
    }

    return rname;
}


/**
 * Map out the RegistryDataArea to hold an ENQ.
 *
 * Note: enqtokens are 32 bytes; RegistryDataAreas are 36 bytes.
 */
struct RegistryDataArea_EnqMap{
    enqtoken enq_token;
    char unused[sizeof(RegistryDataArea)-sizeof(enqtoken)];
};

/**
 * Allocate a RegistryToken for the given ENQ.  The RegistryToken is
 * passed back to the Java caller for later use under pc_deadvertiseWolaServer.
 * We can't pass back the actual ENQ, because then the deadvertise routine
 * would take an ENQ parm, and we'd effectively be giving the Java caller
 * access to an authorized routine that they could use to DEQUE any ENQ
 * (unless we added a bunch of checks).  Not kosher.
 *
 * @param enq_token_p - The enq token to save in the registry token
 * @param registry_token - Output - The RegistryToken
 *
 * @return the RC from util_registry::registryPut
 */
int allocateRegistryTokenForEnq( enqtoken * enq_token_p, RegistryToken * registry_token ) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(101),
                    "allocateRegistryTokenForEnq entry",
                    TRACE_DATA_RAWDATA(sizeof(enqtoken), enq_token_p, "enq_token_p"),
                    TRACE_DATA_END_PARMS);
    }

    struct RegistryDataArea_EnqMap dataArea;
    memset(&dataArea, 0, sizeof(RegistryDataArea));
    memcpy(&dataArea.enq_token, enq_token_p, sizeof(enqtoken));

    // Put it in there.
    int rc = registryPut(WOLATKN, (RegistryDataArea *)&dataArea, registry_token);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(102),
                    "allocateRegistryTokenForEnq exit",
                    TRACE_DATA_INT(rc, "return code"),
                    TRACE_DATA_RAWDATA(sizeof(RegistryToken), registry_token, "registry token"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * Get the ENQ from the given registry token.
 *
 * @param enq_token_p - Output - The enq obtained from the registry token
 * @param registry_token - The registry token
 *
 * @return the rc from util_registry::registryGetAndSetUsed
 */
int getEnqFromRegistryToken(enqtoken * enq_token_p, RegistryToken * registry_token) {

    struct RegistryDataArea_EnqMap dataArea;

    int rc = registryGetAndSetUsed(registry_token, (RegistryDataArea *)&dataArea);
    int setUnused_rc = -1;

    if (rc == 0) {
        memcpy(enq_token_p, &dataArea.enq_token, sizeof(enqtoken));

        // Note: not checking RC on this.  No need.  It gets traced, just in case.
        setUnused_rc = registrySetUnused(registry_token, 0);
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(131),
                    "getEnqFromRegistryToken",
                    TRACE_DATA_INT(rc, "registryGetAndSetUsed rc"),
                    TRACE_DATA_INT(setUnused_rc, "registrySetUnused rc"),
                    TRACE_DATA_RAWDATA(sizeof(struct RegistryDataArea_EnqMap), &dataArea, "RegistryDataArea"),
                    TRACE_DATA_RAWDATA(sizeof(enqtoken), enq_token_p, "enq_token_p"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * Create Master Client Stub table and anchor to Global BGVT.
 * Load latest BBOACALL module by checking the version number of client vector table
 *
 * @param bboacall_path - The bboacall module path
 *
 * @return return code
 */
int load_client_stubs(char* bboacall_path) {
    int load_bboacall_rc = 0;
    int amcstSlotNumber = 1; // this is the slot #2
    int amcstSubpool = 241; // subpool 241
    int amcstKey     = 2;  // key 2
    int amcstNumSlots =  128; // max num of slots
    int amcstLength = sizeof(bboamcst);


    bgvt* bgvt_p = findOrCreateBGVT();

    if (bgvt_p != NULL) {
        int amcstOldCount = bgvt_p->bbodbgvt_bboamcst_count;
        if(bgvt_p->bbodbgvt_bboamcst_ptr == NULL) { // no existing MCST table , get the storage obtain, set in master BGVT,

            if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(116),
                            "load_client_stubs: no existing BBOAMCST",
                            TRACE_DATA_INT(amcstLength,"amcstlength"),
                            TRACE_DATA_END_PARMS);
            }
            int sto_rc;
            bboamcst* amcstNewPtr = storageObtain(amcstLength, amcstSubpool, amcstKey, &sto_rc);

            // storageObtain OK.
            if (sto_rc == 0) {
                // clear the storage
                memset(amcstNewPtr, 0, amcstLength);
                memcpy(amcstNewPtr->amcst_eye,"BBOAMCST",8);

                amcstNewPtr->amcst_ver = 1;
                amcstNewPtr->amcst_num_slots = amcstNumSlots;

                // anchor in bgvt slot
                //Todo: switch to PLO to set in BGVT
                bgvt_p->bbodbgvt_bboamcst_ptr = amcstNewPtr;
            }
        }
        // -----------------------------------------------------------------
        // Load the bboacall module
        // -----------------------------------------------------------------
        loadhfs_details* bboacall_details_p = load_from_hfs(bboacall_path);
        if (bboacall_details_p != NULL) {

            // off the highorder and low order bit everytime we map the vector from assembly in c
            bboacall* bboacall_p = (bboacall*) (((long long)(bboacall_details_p->entry_p)) & 0xFFFFFFFFFFFFFFFEL);

            // ---------------------------------------------------------------
            // Check for slot availability, If our slot does not exist yet,
            // we need to allocate storage for AMCSS struct and set it up to
            // use the newly loaded BBOACALL it.
            // If there is am AMCSS already setup, check the version number
            // of the new instance of BBOACALL against the one that is
            // hung off the global BGVT.
            // If the version of BBOACALL in our slot is lower than new
            // version, we need to replace it with the new BBOACALL and
            // move the old version to a dead queue, because there could
            // still be someone using that BBOACALL.
            // ---------------------------------------------------------------
            short int old_mod_version = 0;
            short int new_mod_version = 0;
            unsigned char keep_new_mod = 0;

            new_mod_version = bboacall_p->stubVecSlotlevel;

            bboamcst* bboamcst_p = (bboamcst*)(bgvt_p->bbodbgvt_bboamcst_ptr);
            bboamcss  * __ptr32* slot2_p = &( bboamcst_p->amcst_slots[amcstSlotNumber]);

            if (*slot2_p != NULL) {  // slot already exists
                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(117),
                                "load_client_stubs: slot2 not empty",
                                TRACE_DATA_END_PARMS);
                }

                //save the bboamcss ptr
                bboamcss* bboamcss_oldslot2_p = *slot2_p;
                //check the version number against the bboacall version number
                old_mod_version = bboamcss_oldslot2_p->amcss_slot_ver;
                if( (new_mod_version > old_mod_version) || (new_mod_version == 9999) ) {
                    // move the old version into dead queue using __cs1()
                    int cs_rc = 1;

                    void* __ptr32 OldQval = bboamcst_p->amcst_dead_slots; // Get Old value
                    void* __ptr32 NewQval = bboamcss_oldslot2_p; // Get new value to use
                    while(cs_rc !=0 ) {
                        bboamcss_oldslot2_p->amcss_dead_next = bboamcst_p->amcst_dead_slots;  //Link together
                        cs_rc = __cs1(&OldQval, &(bboamcst_p->amcst_dead_slots), NewQval); // Put element on queue
                    }
                    keep_new_mod = 1;
                } else { // new bboacall version is < old bboamcss version , so dont save the new one
                    keep_new_mod = 0;
                }

                if (TraceActive(trc_level_detailed)) {
                                    TraceRecord(trc_level_detailed,
                                                TP(117),
                                                "load_client_stubs: checking version numbers",
                                                TRACE_DATA_INT(old_mod_version,"old_mod_version"),
                                                TRACE_DATA_INT(new_mod_version,"new_mod_version"),
                                                TRACE_DATA_END_PARMS);
                                }
            } else { // no existing bboamcss or if new version is > old bboamcss version
                keep_new_mod = 1;
            }

            if (keep_new_mod == 1) {
                // storage obtain for bboamcss, fill the values , put the version number from bboacall
                int amcss_rc;
                bboamcss* amcss_p = storageObtain(sizeof(bboamcss), amcstSubpool, amcstKey, &amcss_rc);

                if (amcss_rc == 0) { // storageObtain OK.
                    if (TraceActive(trc_level_detailed)) {
                        TraceRecord(trc_level_detailed,
                                    TP(118),
                                    "load_client_stubs: BBOAMCSS doesnot exist or new version > old version",
                                    TRACE_DATA_END_PARMS);
                    }

                    // clear the storage
                    memset(amcss_p, 0, sizeof(bboamcss));
                    memcpy(amcss_p->amcss_eye,"BBOAMCSS",8);
                    amcss_p->amcss_ver = 1;
                    amcss_p->amcss_slot_num = amcstSlotNumber;
                    amcss_p->amcss_slot_ver = bboacall_p->stubVecSlotlevel; // this is the slot level specified in bboavec.s
                    amcss_p->amcss_code_len = bboacall_details_p->mod_len; // the load module BBOACALL has some defined length.. this is that value
                    amcss_p->amcss_code_ptr = bboacall_details_p->mod_p; // in the load_from_hfs, the actual BBOACALL load module, there is a constant address where the start pt is .. this is that
                    amcss_p->amcss_vector_ptr = bboacall_p; // entry point is the instance of the bboacall module
                }

                //put it in slot2,
                //Todo:compare and swap using PLO, if PLO fails, do the storage release for bboamcss
                *slot2_p = amcss_p;

                // Tell contents supervisor about it.
                lpmea modinfo;
                int addReturnCode = contentsSupervisorAddToDynamicLPA(bboacall_path,
                                                                      bboacall_details_p->entry_p,
                                                                      bboacall_details_p->mod_p,
                                                                      bboacall_details_p->mod_len,
                                                                      &modinfo,
                                                                      NULL);
                if (addReturnCode == 0) {
                    // TODO: ??? normally save the delete token from modinfo somewhere.
                    memcpy(bboacall_details_p->delete_token, modinfo.lpmeadeletetoken, sizeof(bboacall_details_p->delete_token));
                } else {
                    // TODO: Remove me
                    abend(ABEND_TYPE_SERVER, 0xDEADBEEA);
                }


            } else { // unload the new bboacall module
                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(119),
                                "load_client_stubs: BBOAMCSS exist, unload the new bboacall",
                                TRACE_DATA_END_PARMS);
                }
                unload_from_hfs(bboacall_details_p);
            }
        } else {  // Couldn't load bboacall module from hfs for some reason.  Fail out.
            load_bboacall_rc = LOAD_BBOACALL_FAIL;
        }
    }
    return load_bboacall_rc;
}
/**
 * {@inheritDoc}
 *
 * Advertise the WOLA server's presence by obtaining an ENQ using the server's WOLA group
 * and WOLA identity name.
 *
 * The ENQ is tucked into a RegistryToken and returned to the caller.  The caller will later
 * invoke pc_deadvertiseWolaServer, passing back the RegistryToken, in order to release the
 * ENQ when the WOLA server is shutting down.
 */
void pc_advertiseWolaServer(pc_advertise_parms * parms) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(111),
                    "pc_advertiseWolaServer entry",
                    TRACE_DATA_RAWDATA(sizeof(pc_advertise_parms), parms, "Parms"),
                    TRACE_DATA_END_PARMS);
    }

    // Get IPT TTOKEN
    TToken ipt_ttoken;
    int rc = getIPT_TToken(&ipt_ttoken);

    if (rc != 0) {
        // Couldn't obtain IPT TToken for some reason.  Fail out.
        memcpy_dk(parms->getIPT_TToken_rc_p,
                  &rc,
                  sizeof(int),
                  8);
        return;
    }


    // -----------------------------------------------------------------
    // Load the bboacall module
    // -----------------------------------------------------------------
    int load_bboacall_rc = LOAD_BBOACALL_OK;
    char* bboacall_path = strdup(parms->bboacall_module_name);
    if (bboacall_path  != NULL) {
        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(115),
                        "pc_advertiseWolaServer bboacall_path not null",
                        TRACE_DATA_STRING(bboacall_path,"bboacall_path"),
                        TRACE_DATA_END_PARMS);
        }

        load_bboacall_rc = load_client_stubs(bboacall_path);

    } else {  // bboacall path is not correct
        load_bboacall_rc = LOAD_BBOACALL_NULL_PATH;
    }

    memcpy_dk(parms->load_bboacall_rc_p,
              &load_bboacall_rc,
              sizeof(int),
              8);

    // Construct the ENQ RNAME using the wola group and wola identity name.
    char rname[BBGZ_ENQ_MAX_RNAME_LEN];
    buildWolaEnqRname( rname, parms->wola_group, parms->wola_name2, parms->wola_name3);

    // Grab the ENQ.
    enqtoken enq_token;
    rc = get_enq_exclusive_system_conditional_token( BBGZ_ENQ_QNAME, rname, (char *)&ipt_ttoken, &enq_token);
    RegistryToken registry_token;


    if( rc == 0 ) {
       // Successfully aquired enq
       // Tuck the ENQ into a registry token to return to the caller.
       rc = allocateRegistryTokenForEnq( &enq_token, &registry_token );

       if (rc == 0) {
           // Copy the token back to the key-8 caller
           memcpy_dk(parms->registry_token_p,
                     &registry_token,
                     sizeof(RegistryToken),
                     8);

           // Copy back rc=0
           memcpy_dk(parms->return_code_p,
                     &rc,
                     sizeof(int),
                     8);
       } else {
           // Some sort of registry failure. Copy back rc.
           memcpy_dk( parms->registry_rc_p,
                      &rc,
                      sizeof(int),
                      8 );

           // Release the enq.
           // Note: this method will ABEND if it fails.
           release_enq_owning( &enq_token, (char *)&ipt_ttoken );
       }
    } else {
        // Unable to obtain resource
         memcpy_dk( parms->return_code_p,
                    &rc,
                    sizeof(int),
                    8 );

    }
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(112),
                    "pc_advertiseWolaServer exit",
                    TRACE_DATA_HEX_INT(rc, "rc"),
                    TRACE_DATA_RAWDATA(sizeof(enqtoken), &enq_token, "enq_token"),
                    TRACE_DATA_RAWDATA(sizeof(RegistryToken), &registry_token, "registry_token"),
                    TRACE_DATA_END_PARMS);
    }

}

/**
 * {@inheritDoc}
 *
 * DE-advertise the WOLA server's presence by releasing the ENQ it previously obtained via
 * pc_advertiseWolaServer.  
 */
void pc_deadvertiseWolaServer(pc_advertise_parms * parms) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(121),
                    "pc_deadvertiseWolaServer entry",
                    TRACE_DATA_RAWDATA(sizeof(pc_advertise_parms), parms, "Parms"),
                    TRACE_DATA_END_PARMS);
    }

    // Get IPT TTOKEN
    TToken ipt_ttoken;
    int rc = getIPT_TToken(&ipt_ttoken);

    if (rc != 0) {
        // Couldn't obtain IPT TToken for some reason.  Fail out.
        memcpy_dk(parms->getIPT_TToken_rc_p,
                  &rc,
                  sizeof(int),
                  8);
        return;
    }

    // Retrieve the ENQ token from the registry.
    int registryFree_rc = -1;
    enqtoken enq_token;
    rc = getEnqFromRegistryToken( &enq_token, &parms->registry_token );

    if (rc == 0) {

        // All's well. Release the ENQ.
        // Note: this method will ABEND if it fails.
        release_enq_owning(&enq_token, (char *) &ipt_ttoken);

        // Free the registry token.  Not bothering to check the RC, but we do trace it.
        registryFree_rc = registryFree(&parms->registry_token, 0);

    } else {

        // Huh?  Some registry error occurred. Copy back to caller.
        memcpy_dk( parms->registry_rc_p,
                   &rc,
                   sizeof(int),
                   8 );
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(122),
                    "pc_deadvertiseWolaServer exit",
                    TRACE_DATA_HEX_INT(rc, "rc"),
                    TRACE_DATA_HEX_INT(registryFree_rc, "registryFree rc"),
                    TRACE_DATA_RAWDATA(sizeof(enqtoken), &enq_token, "enq_token"),
                    TRACE_DATA_END_PARMS);
    }

}

/**
 * {@inheritDoc}
 *
 * Activate the WOLA server's registration (BBOARGE).
 *
 * If the BBOARGE doesn't exist, it is created.
 * 
 * The BBOARGE pointer is tucked into a RegistryToken and returned to the caller.  The caller 
 * will later invoke pc_deactivateWolaRegistration, passing back the RegistryToken.
 *
 * @return return_code = 8: could not obtain bboashr_p (see iean4rt_rc).
 *         return_ocde = 12: could not obtain storage for the BBOARGE (see cell_pool_rc).
 *         return_code = 16: add failed because the BBOARGE already exists, but find couldn't find it
 *
 */
void pc_activateWolaRegistration(pc_activateWolaRegistration_parms * parms) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(141),
                    "pc_activateWolaRegistration entry",
                    TRACE_DATA_RAWDATA(sizeof(pc_activateWolaRegistration_parms), parms, "Parms"),
                    TRACE_DATA_END_PARMS);
    }

    // Lookup the bboashr pointer 
    int rc;
    WolaSharedMemoryAnchor_t * bboashr_p = (WolaSharedMemoryAnchor_t *) getBboashrForWolaGroup( parms->wola_group, &rc ); 

    if (bboashr_p == NULL) {
        // Something went horribly wrong.
        memcpy_dk( parms->iean4rt_rc_p,
                   &rc,
                   sizeof(int),
                   8 );

        // Copy back a general error code for the return_code field.
        rc = 8;
        memcpy_dk( parms->return_code_p,
                   &rc,
                   sizeof(int),
                   8 );

        return;
    }

    // We assume we're already attached to the shared memory area. This
    // was done under pc_attachToBboashr.

    WolaRegistration_t * bboarge_p = findServerBboargeInChain( bboashr_p->firstRge_p,      
                                                               parms->wola_name2,
                                                               parms->wola_name3);

    if (bboarge_p == NULL) {

        // The BBOARGE doesn't exist yet.  Create it.
        
        int cell_pool_rc = 0;
        bboarge_p = (WolaRegistration_t *) getCellPoolCell( bboashr_p->registationCellPoolID);  

        if (bboarge_p == NULL) {

            // Couldn't allocate storage for the BBOARGE.  Fail out.
            memcpy_dk( parms->cell_pool_rc_p,
                       &cell_pool_rc,
                       sizeof(int),
                       8 );

            // Copy back a general error code for the return_code field.
            rc = 12;
            memcpy_dk( parms->return_code_p,
                       &rc,
                       sizeof(int),
                       8 );

            return;
        }

        // Initialize the bboarge and add it to the chain.
        initializeServerBboarge(bboarge_p, parms->wola_name2, parms->wola_name3, bboashr_p);
        addBboargeToChain( bboashr_p, bboarge_p ); // TODO: other server using same name?
    }

    // Activate the wola registration.
    activateBboarge(bboarge_p, parms->useCicsTaskUserId);

    // Save bboarge_p in server_process_data, in case the server fails catastrophically and
    // we must deactivate the registration from the RESMGR (instead of the normal deactivation
    // via deactivateWolaRegistration)
    server_process_data* spd = getServerProcessData();
    if (spd != NULL) {
        spd->wola_bboarge_p = bboarge_p;
    }

    // Wrap bboarge_p with a RegistryToken that can be returned to the caller 
    // (for subsequently passing to deactivateWolaRegistration).
    RegistryToken registry_token;
    rc = allocateRegistryTokenForBboarge(bboarge_p, &registry_token ); 

    if (rc == 0) {
        // Copy the token back to the key-8 caller
        memcpy_dk(parms->registry_token_p, 
                  &registry_token,
                  sizeof(RegistryToken),
                  8);

        // Copy back rc=0
        memcpy_dk(parms->return_code_p, 
                  &rc,
                  sizeof(int),
                  8);
    } else {

        // Some sort of registry failure. Copy back rc.
        memcpy_dk( parms->registry_rc_p,
                   &rc,
                   sizeof(int),
                   8 );

        deactivateBboarge(bboarge_p);
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(142),
                    "pc_activateWolaRegistration exit",
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_RAWDATA(((rc != 0) ? 0 : sizeof(WolaRegistration_t)), bboarge_p, "BBOARGE"),
                    TRACE_DATA_END_PARMS);
    }
}

/**
 * {@inheritDoc}
 *
 * DE-Activate the WOLA server's registration (BBOARGE).
 * 
 * The BBOARGE pointer is obtained via the RegistryToken parm.  The RegistryToken was previously
 * obtained via pc_activateWolaRegistration.
 */
void pc_deactivateWolaRegistration(pc_activateWolaRegistration_parms * parms) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(151),
                    "pc_deactivateWolaRegistration entry",
                    TRACE_DATA_RAWDATA(sizeof(pc_activateWolaRegistration_parms), parms, "Parms"),
                    TRACE_DATA_END_PARMS);
    }

    // Obtain the bboarge_p from the registry token, then deactivate it.
    int registryFree_rc = -1;
    int rc;
    void * bboarge_p = getBboargeFromRegistryToken( &parms->registry_token, &rc ); 

    if (bboarge_p != NULL) {

        // Got it! Now deactivate.
        deactivateBboarge(bboarge_p);

        // Remove the server process data's link to the registration, in case the RESMGR
        // tries to clean it up as well.
        server_process_data* spd = getServerProcessData();
        if (spd->wola_bboarge_p == bboarge_p) {
            spd->wola_bboarge_p = NULL;
        }

        // Free the registry token.  Not bothering to check the RC, but we do trace it.
        registryFree_rc = registryFree(&parms->registry_token, 0);

    } else {
        // Huh?  Soem registry error occurred. Copy back to caller and return.
        memcpy_dk( parms->registry_rc_p,
                   &rc,
                   sizeof(int),
                   8 );
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(152),
                    "pc_deactivateWolaRegistration exit",
                    TRACE_DATA_HEX_INT(rc, "rc"),
                    TRACE_DATA_HEX_INT(registryFree_rc, "registryFree rc"),
                    TRACE_DATA_END_PARMS);
    }

}

/**
 * {@inheritDoc}
 *
 * @return return_code = 8:  registration was not found.
 * @return return_code = 12: getClientService failed
 * @return return_code = 16: shared memory anchor was not found.
 */
void pc_getClientService(pc_wolaServiceQueueParms* parms){

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(161),
                    "pc_getClientService entry",
                    TRACE_DATA_RAWDATA(sizeof(pc_wolaServiceQueueParms), parms, "Parms"),
                    TRACE_DATA_END_PARMS);
    }

    unsigned char callerKey = 8;

    int return_code = 0;
    int iean4rt_rc = 0;
    int getClientService_rc = 0;

    WolaSharedMemoryAnchor_t * bboashr_p = (WolaSharedMemoryAnchor_t *) getBboashrForWolaGroup(parms->wolaGroup, &iean4rt_rc);;

    if (bboashr_p != NULL){
        //Find the registration
        WolaRegistration_t * reg_p = findClientRegistrationByName(parms->registration, bboashr_p );

        if(reg_p == NULL){
            //The client registration was not found
            return_code = 8;
        }
        else{
            LocalCommClientConnectionHandle_t client_conn_handle_p;
            getClientService_rc = getClientService(reg_p, parms->serviceName, parms->timeout_s, parms->waiterToken, &client_conn_handle_p);

            if(getClientService_rc == 0){
                //Client connection handle is good. Copy it back to the caller

                memcpy_dk(parms->client_conn_handle_p, &client_conn_handle_p, sizeof(LocalCommClientConnectionHandle_t), callerKey);
            }else{
                return_code = 12;
            }
        }
    }else{
        //The shared memory anchor was not found
        return_code = 16;
    }

    // Copy back all return codes.
    memcpy_dk(parms->return_code_p, &return_code, sizeof(int), callerKey);
    memcpy_dk(parms->iean4rt_rc_p, &iean4rt_rc, sizeof(int), callerKey);
    memcpy_dk(parms->getClientService_rc_p, &getClientService_rc, sizeof(int), callerKey);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(162),
                    "pc_getClientService exit",
                    TRACE_DATA_HEX_INT(return_code, "return_code"),
                    TRACE_DATA_HEX_INT(iean4rt_rc, "iean4rt_rc"),
                    TRACE_DATA_HEX_INT(getClientService_rc, "getClientService_rc"),
                    TRACE_DATA_END_PARMS);
    }
}


/**
 * {@inheritDoc}
 */
void pc_cancelClientService(pc_wolaCancelClientService_parms* parms) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(18),
                    "pc_cancelClientService entry",
                    TRACE_DATA_RAWDATA(sizeof(pc_wolaServiceQueueParms), parms, "Parms"),
                    TRACE_DATA_END_PARMS);
    }

    unsigned char callerKey = 8;

    int return_code = 0;
    int iean4rt_rc = 0;

    WolaSharedMemoryAnchor_t * bboashr_p = (WolaSharedMemoryAnchor_t *) getBboashrForWolaGroup(parms->wolaGroup, &iean4rt_rc);

    if (bboashr_p != NULL){
        //Find the registration
        WolaRegistration_t * reg_p = findClientRegistrationByName(parms->registration, bboashr_p );

        if(reg_p == NULL){
            //The client registration was not found
            return_code = 8;
        }
        else{
            cancelClientService(reg_p, parms->waiterToken);
        }
    }else{
        //The shared memory anchor was not found
        return_code = 16;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(19),
                    "pc_cancelClientService exit",
                    TRACE_DATA_HEX_INT(return_code, "return_code"),
                    TRACE_DATA_HEX_INT(iean4rt_rc, "iean4rt_rc"),
                    TRACE_DATA_END_PARMS);
    }
}

/**
 * {@inheritDoc}
 *
 */
void pc_addOTMAAnchorToSPD(struct pc_addOTMAAnchorToSPD_parms* parms_p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(171),
                    "pc_addOTMAAnchorToSPD entry",
                    TRACE_DATA_RAWDATA(sizeof(otma_anchor_t), parms_p->otma_anchor, "OTMA Anchor data"),
                    TRACE_DATA_END_PARMS);
    }

    server_process_data* spd = getServerProcessData();


    if(spd != NULL){

       //Create a new node now that we are in key 2
        struct pc_addOTMAAnchorToSPD_parms* node_p = __malloc31(sizeof(pc_addOTMAAnchorToSPD_parms));;
        memset(node_p, 0, sizeof(pc_addOTMAAnchorToSPD_parms));

        // Setup the PC routine parms
        memcpy(node_p->otma_group_name, parms_p->otma_group_name, sizeof(otma_grp_name_t));
        memcpy(node_p->otma_member_name, parms_p->otma_member_name, sizeof(otma_srv_name_t));
        memcpy(node_p->otma_partner_name, parms_p->otma_partner_name, sizeof(otma_clt_name_t));
        memcpy(node_p->otma_anchor, parms_p->otma_anchor, sizeof(otma_anchor_t));

       //Add new anchor to head of anchor list
       node_p->nextAnchor_p = spd->wola_otma_anchors_p;
       spd->wola_otma_anchors_p = node_p;
    }

}

/**
 * {@inheritDoc}
 *
 */
void pc_removeOTMAAnchorFromSPD(struct pc_addOTMAAnchorToSPD_parms* parms) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(181),
                    "pc_removeOTMAAnchorFromSPD entry",
                    TRACE_DATA_RAWDATA(sizeof(otma_anchor_t), parms->otma_anchor, "OTMA Anchor data"),
                    TRACE_DATA_END_PARMS);
    }

    server_process_data* spd = getServerProcessData();

    if (spd != NULL) {
        struct pc_addOTMAAnchorToSPD_parms* current_anchor_p = spd->wola_otma_anchors_p;
        struct pc_addOTMAAnchorToSPD_parms* previous_anchor_p = NULL;

        while(current_anchor_p != NULL){
            if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(182),
                            "pc_removeOTMAAnchorFromSPD next anchor",
                            TRACE_DATA_RAWDATA(sizeof(otma_anchor_t), current_anchor_p->otma_anchor, "OTMA Anchor data"),
                            TRACE_DATA_RAWDATA(sizeof(pc_addOTMAAnchorToSPD_parms), current_anchor_p->nextAnchor_p, "Next Pointer"),
                            TRACE_DATA_END_PARMS);
            }

            if(memcmp(current_anchor_p->otma_anchor, parms->otma_anchor, sizeof(otma_anchor_t)) == 0){
                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(183),
                                "pc_removeOTMAAnchorFromSPD anchor match",
                                TRACE_DATA_RAWDATA(sizeof(otma_anchor_t), current_anchor_p->otma_anchor, "OTMA Anchor data"),
                                TRACE_DATA_END_PARMS);
                }

                if(previous_anchor_p == NULL){
                    spd->wola_otma_anchors_p = current_anchor_p->nextAnchor_p;
                } else{
                    previous_anchor_p->nextAnchor_p = current_anchor_p->nextAnchor_p;
                }

                free(current_anchor_p);
                break;;
            }
            previous_anchor_p = current_anchor_p;
            current_anchor_p = current_anchor_p->nextAnchor_p;
        }
    }
}
