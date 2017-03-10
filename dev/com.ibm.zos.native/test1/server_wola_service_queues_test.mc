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
#include <metal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "../include/server_wola_registration.h"
#include "../include/server_wola_shared_memory_anchor.h"
#include "../include/server_wola_avail_service.h"
#include "../include/server_wola_wait_service.h"
#include "../include/server_wola_service_queues.h"
#include "../include/mvs_plo.h"
#include "../include/mvs_cell_pool_services.h"
#include "../include/server_process_data.h"

#include "include/CuTest.h"

/**
 * Forward declares.
 */
void println(char * messgae_p, ...);
AvailableService_t * findClientAvailableServiceByName(unsigned char* serviceName, struct wolaRegistration * reg_p);
WaitService_t * findWaitServiceByName(unsigned char* serviceName, struct wolaRegistration * reg_p);
WaitService_t * findWaitServiceByToken(long long uniqueToken, struct wolaRegistration * reg_p);
int removeAvailableServiceFromChain(PloCompareAndSwapAreaDoubleWord_t* swapArea, struct wolaRegistration* reg_p, struct availableService* availServ_p);
int removeWaitServiceFromChain(PloCompareAndSwapAreaDoubleWord_t* swapArea, struct wolaRegistration* reg_p, struct waitService* waitServ_p);
int addWaitServiceToChain(PloCompareAndSwapAreaDoubleWord_t* swapArea, struct wolaRegistration* reg_p, struct waitService* newWaitingService);
int addAvailableServiceToChain(PloCompareAndSwapAreaDoubleWord_t* swapArea, struct wolaRegistration* reg_p, struct availableService* newAvailService);
int isMatch(const char* string, const char* pattern);
void * getOuterCellPoolAddrAndSize( WolaSharedMemoryAnchor_t * bboashr_p, long long * outer_cp_size_p );
long long buildInnerCellPool( WolaSharedMemoryAnchor_t * wolaAnchor_p, long long cell_size, char * name );
long long buildCellPool(void* storage_p, long long storage_len, long long cell_size, char* name_p, buildCellPoolFlags flags);
WaitService_t * createWaitService(iea_PEToken pet, unsigned char* serviceName, struct wolaRegistration* reg_p, long long waiterToken);
int startTimerForWaitService(WaitService_t * waitService_p, int timeout_s, PetVet * petvet_p) ;
int cancelTimerForWaitService(WaitService_t * waitService_p ) ;
WaitService_t * initializeWaitService(WaitService_t * waitService_p, iea_PEToken pet, unsigned char* serviceName, struct wolaRegistration* reg_p, long long waiterToken) ;
int pauseWaitService(WaitService_t * waitService_p);
int strnlen(const char * str, int maxLen) ;
AvailableService_t * initializeAvailableService(AvailableService_t * newAvailService, struct localCommClientConnectionHandle* clientConnHandle, unsigned char* serviceName, struct wolaRegistration* reg_p) ;


/**
 * Find an available client service from a given name.
 */
void test_findClientAvailableServiceByName(CuTest * tc) {
    println(__FUNCTION__ ": entry");

    //Create service with name
    unsigned char serviceName[] = "myService";
    struct availableService availServ;
    struct availableService* availServ_p = &availServ;
    memset(availServ_p, 0, sizeof(struct availableService)); //clear memory
    memcpy(availServ_p->serviceName, serviceName, sizeof(serviceName));

    println(__FUNCTION__ ": created available service: %s", availServ_p->serviceName);

    //Create registration
    struct wolaRegistration reg;
    struct wolaRegistration* reg_p = &reg;
    memset(reg_p, 0, sizeof(struct wolaRegistration)); //clear memory
    reg_p->availServiceFirst_p = availServ_p;

    struct availableService* result_p = findClientAvailableServiceByName(serviceName, reg_p);

    if(result_p != NULL){
        println(__FUNCTION__ ": found available service: %s", result_p->serviceName);
    }else{
        println(__FUNCTION__ ": lookup failed");
    }

    CuAssertPtrEquals(tc, availServ_p, result_p);

    println(__FUNCTION__ ": exit");
}

/**
 * Find a waiting client service from a given name
 */
void test_findWaitServiceByName(CuTest * tc) {
    println(__FUNCTION__ ": entry");

    //Create service with name
    char serviceName[] = "myService";
    struct waitService waitServ;
    struct waitService* waitServ_p = &waitServ;
    memset(waitServ_p, 0, sizeof(struct waitService)); //clear memory
    memcpy(waitServ_p->serviceName, serviceName, sizeof(serviceName));

    println(__FUNCTION__ ": created wait service: %s", waitServ_p->serviceName);

    //Create registration
    struct wolaRegistration reg;
    struct wolaRegistration* reg_p = &reg;
    memset(reg_p, 0, sizeof(struct wolaRegistration)); //clear memory
    reg_p->waitServiceFirst_p = waitServ_p;

    struct waitService* result_p = findWaitServiceByName(serviceName, reg_p);

    if(result_p != NULL){
        println(__FUNCTION__ ": found wait service: %s", result_p->serviceName);
    }else{
        println(__FUNCTION__ ": lookup failed");
    }

    CuAssertPtrEquals(tc, waitServ_p, result_p);

    println(__FUNCTION__ ": exit");
}

/**
 * Find an available client service using an incorrect name.
 * This ensures that NULL is correctly returned
 */
void test_findClientAvailableServiceByNameNotFound(CuTest * tc) {
    println(__FUNCTION__ ": entry");

    //Create service with name
    unsigned char serviceName[] = "myService";
    unsigned char serviceNameBad[] = "notMyService";
    struct availableService availServ;
    struct availableService* availServ_p = &availServ;
    memset(availServ_p, 0, sizeof(struct availableService)); //clear memory
    memcpy(availServ_p->serviceName, serviceName, sizeof(serviceName));

    println(__FUNCTION__ ": created available service: %s", availServ_p->serviceName);

    //Create registration
    struct wolaRegistration reg;
    struct wolaRegistration* reg_p = &reg;
    memset(reg_p, 0, sizeof(struct wolaRegistration)); //clear memory
    reg_p->availServiceFirst_p = availServ_p;

    struct availableService* result_p = findClientAvailableServiceByName(serviceNameBad, reg_p);

    if(result_p == NULL){
        println(__FUNCTION__ ": lookup returned null as expected");
    }else{
        println(__FUNCTION__ ": lookup failed. Returned non-null element");
    }

    CuAssertPtrEquals(tc, NULL, result_p);

    println(__FUNCTION__ ": exit");
}

/**
 * Find a waiting client service using an incorrect name.
 * This ensures that NULL is correctly returned.
 */
void test_findWaitServiceByNameNotFound(CuTest * tc) {
    println(__FUNCTION__ ": entry");

    //Create service with name
    unsigned char serviceName[] = "myService";
    unsigned char serviceNameBad[] = "notMyService";
    struct waitService waitServ;
    struct waitService* waitServ_p = &waitServ;
    memset(waitServ_p, 0, sizeof(struct waitService)); //clear memory
    memcpy(waitServ_p->serviceName, serviceName, sizeof(serviceName));

    println(__FUNCTION__ ": created wait service: %s", waitServ_p->serviceName);

    //Create registration
    struct wolaRegistration reg;
    struct wolaRegistration* reg_p = &reg;
    memset(reg_p, 0, sizeof(struct wolaRegistration)); //clear memory
    reg_p->waitServiceFirst_p = waitServ_p;

    struct waitService* result_p = findWaitServiceByName(serviceNameBad, reg_p);

    if(result_p == NULL){
        println(__FUNCTION__ ": lookup returned null as expected");
    }else{
        println(__FUNCTION__ ": lookup failed. Returned non-null element");
    }

    CuAssertPtrEquals(tc, NULL, result_p);

    println(__FUNCTION__ ": exit");
}

/**
 * Remove the only available service from the chain. This tests
 * that the appropriate first and last pointers are reset to NULL
 * and that the count is reset to 0.
 */
void test_removeOnlyAvailableServiceFromChain(CuTest * tc) {
    println(__FUNCTION__ ": entry");

    //Create service with name
    unsigned char serviceName[] = "myService";
    struct availableService availServ;
    struct availableService* availServ_p = &availServ;
    memset(availServ_p, 0, sizeof(struct availableService)); //clear memory
    memcpy(availServ_p->serviceName, serviceName, sizeof(serviceName));
    availServ_p->previousService = NULL;
    availServ_p->nextService = NULL;

    println(__FUNCTION__ ": created available service: %s", availServ_p->serviceName);

    //Create registration
    struct wolaRegistration reg;
    struct wolaRegistration* reg_p = &reg;
    memset(reg_p, 0, sizeof(struct wolaRegistration)); //clear memory
    reg_p->availServiceFirst_p = availServ_p;
    reg_p->availServiceLast_p = availServ_p;
    reg_p->availServiceCount = 1;

    struct availableService*  result_p = findClientAvailableServiceByName(serviceName, reg_p);
    CuAssertPtrEquals(tc, availServ_p, result_p);

    println(__FUNCTION__ ": lookup returned available service: %s", result_p->serviceName);

    //Set PLO swap area
    PloCompareAndSwapAreaDoubleWord_t swapArea;
    unsigned long long* counter_p = ((unsigned long long*)(&(reg_p->serviceQueuesPLOCounter)));
    swapArea.expectedValue = *counter_p;
    swapArea.replaceValue = swapArea.expectedValue + 1;
    swapArea.compare_p = counter_p;

    int ploRC = removeAvailableServiceFromChain(&swapArea, reg_p, availServ_p);
    result_p = findClientAvailableServiceByName(serviceName, reg_p);

    if(result_p == NULL &&
        reg_p->availServiceCount == 0 &&
        reg_p->availServiceFirst_p == NULL &&
        reg_p->availServiceLast_p == NULL){

        println(__FUNCTION__ ": available service successfully removed");
    }else{
        println(__FUNCTION__ ": removal FAILED");
    }

    CuAssertPtrEquals(tc, NULL, result_p);

    println(__FUNCTION__ ": exit");
}

/**
 * Remove the only wait service on the chain. This ensures that
 * the appropriate first and last wait pointers are reset to NULL
 * and that the wait service counter is reset to 0.
 */
void test_removeOnlyWaitServiceFromChain(CuTest * tc) {
    println(__FUNCTION__ ": entry");

    //Create service with name
    unsigned char serviceName[] = "myService";
    struct waitService waitServ;
    struct waitService* waitServ_p = &waitServ;
    memset(waitServ_p, 0, sizeof(struct waitService)); //clear memory
    memcpy(waitServ_p->serviceName, serviceName, sizeof(serviceName));
    waitServ_p->previousService = NULL;
    waitServ_p->nextService = NULL;

    println(__FUNCTION__ ": created wait service: %s", waitServ_p->serviceName);

    //Create registration
    struct wolaRegistration reg;
    struct wolaRegistration* reg_p = &reg;
    memset(reg_p, 0, sizeof(struct wolaRegistration)); //clear memory
    reg_p->waitServiceFirst_p = waitServ_p;
    reg_p->waitServiceLast_p = waitServ_p;
    reg_p->waitServiceCount = 1;

    struct waitService*  result_p = findWaitServiceByName(serviceName, reg_p);
    CuAssertPtrEquals(tc, waitServ_p, result_p);

    println(__FUNCTION__ ": lookup returned wait service: %s", result_p->serviceName);

    //Set PLO swap area
    PloCompareAndSwapAreaDoubleWord_t swapArea;
    unsigned long long* counter_p = ((unsigned long long*)(&(reg_p->serviceQueuesPLOCounter)));
    swapArea.expectedValue = *counter_p;
    swapArea.replaceValue = swapArea.expectedValue + 1;
    swapArea.compare_p = counter_p;

    int ploRC = removeWaitServiceFromChain(&swapArea, reg_p, waitServ_p);
    result_p = findWaitServiceByName(serviceName, reg_p);

    if(result_p == NULL &&
        reg_p->waitServiceCount == 0 &&
        reg_p->waitServiceFirst_p == NULL &&
        reg_p->waitServiceLast_p == NULL){

        println(__FUNCTION__ ": wait service successfully removed");
    }else{
        println(__FUNCTION__ ": removal FAILED");
    }

    CuAssertPtrEquals(tc, NULL, result_p);

    println(__FUNCTION__ ": exit");
}

/**
 * Remove an available service from the middle of a chain of services.
 * This ensures that the previous and next service pointers are correctly set
 * and that the available service count has been correctly decremented.
 */
void test_removeAvailableServiceFromMiddleOfChain(CuTest * tc) {
    println(__FUNCTION__ ": entry");

    //Create three available services
    unsigned char service1Name[] = "myService1";
    struct availableService availServ1;
    struct availableService* availServ1_p = &availServ1;
    memset(availServ1_p, 0, sizeof(struct availableService)); //clear memory
    memcpy(availServ1_p->serviceName, service1Name, sizeof(service1Name));

    unsigned char service2Name[] = "myService2";
    struct availableService availServ2;
    struct availableService* availServ2_p = &availServ2;
    memset(availServ2_p, 0, sizeof(struct availableService)); //clear memory
    memcpy(availServ2_p->serviceName, service2Name, sizeof(service2Name));

    unsigned char service3Name[] = "myService3";
    struct availableService availServ3;
    struct availableService* availServ3_p = &availServ3;
    memset(availServ3_p, 0, sizeof(struct availableService)); //clear memory
    memcpy(availServ3_p->serviceName, service3Name, sizeof(service3Name));

    availServ1_p->previousService = NULL;
    availServ1_p->nextService = availServ2_p;
    availServ2_p->previousService = availServ1_p;
    availServ2_p->nextService = availServ3_p;
    availServ3_p->previousService = availServ2_p;
    availServ3_p->nextService = NULL;

    println(__FUNCTION__ ": created available service: %s", availServ1_p->serviceName);
    println(__FUNCTION__ ": created available service: %s", availServ2_p->serviceName);
    println(__FUNCTION__ ": created available service: %s", availServ3_p->serviceName);

    //Create registration
    struct wolaRegistration reg;
    struct wolaRegistration* reg_p = &reg;
    memset(reg_p, 0, sizeof(struct wolaRegistration)); //clear memory
    reg_p->availServiceFirst_p = availServ1_p;
    reg_p->availServiceLast_p = availServ3_p;
    reg_p->availServiceCount= 3;

    struct availableService*  result_p = findClientAvailableServiceByName(service2Name, reg_p);
    CuAssertPtrEquals(tc, availServ2_p, result_p);

    println(__FUNCTION__ ": lookup returned available service: %s", result_p->serviceName);

    //Set PLO swap area
    PloCompareAndSwapAreaDoubleWord_t swapArea;
    unsigned long long* counter_p = ((unsigned long long*)(&(reg_p->serviceQueuesPLOCounter)));
    swapArea.expectedValue = *counter_p;
    swapArea.replaceValue = swapArea.expectedValue + 1;
    swapArea.compare_p = counter_p;

    int ploRC = removeAvailableServiceFromChain(&swapArea, reg_p, availServ2_p);
    result_p = findClientAvailableServiceByName(service2Name, reg_p);

    if(result_p == NULL &&
        reg_p->availServiceCount == 2 &&
        availServ1_p->nextService == availServ3_p &&
        availServ3_p->previousService == availServ1_p){

        println(__FUNCTION__ ": available service successfully removed");
    }else{
        println(__FUNCTION__ ": removal FAILED");
    }

    CuAssertIntEquals(tc, 0, ploRC);
    CuAssertPtrEquals(tc, availServ3_p, availServ1_p->nextService);
    CuAssertPtrEquals(tc, availServ1_p, availServ3_p->previousService);
    CuAssertPtrEquals(tc, NULL, result_p);
    CuAssertIntEquals(tc, 2, reg_p->availServiceCount);


    println(__FUNCTION__ ": exit");
}

/**
 * Remove a wait service from the middle of a chain of wait services.
 * This ensures that the previous and next wait service pointers are
 * correctly set and that the wait service counter is correctly decremented.
 */
void test_removeWaitServiceFromMiddleOfChain(CuTest * tc) {
    println(__FUNCTION__ ": entry");

    //Create three available services
    unsigned char service1Name[] = "myService1";
    struct waitService waitServ1;
    struct waitService* waitServ1_p = &waitServ1;
    memset(waitServ1_p, 0, sizeof(struct waitService)); //clear memory
    memcpy(waitServ1_p->serviceName, service1Name, sizeof(service1Name));

    unsigned char service2Name[] = "myService2";
    struct waitService waitServ2;
    struct waitService* waitServ2_p = &waitServ2;
    memset(waitServ2_p, 0, sizeof(struct waitService)); //clear memory
    memcpy(waitServ2_p->serviceName, service2Name, sizeof(service2Name));

    unsigned char service3Name[] = "myService3";
    struct waitService waitServ3;
    struct waitService* waitServ3_p = &waitServ3;
    memset(waitServ3_p, 0, sizeof(struct waitService)); //clear memory
    memcpy(waitServ3_p->serviceName, service3Name, sizeof(service3Name));

    waitServ1_p->previousService = NULL;
    waitServ1_p->nextService = waitServ2_p;
    waitServ2_p->previousService = waitServ1_p;
    waitServ2_p->nextService = waitServ3_p;
    waitServ3_p->previousService = waitServ2_p;
    waitServ3_p->nextService = NULL;

    println(__FUNCTION__ ": created wait service: %s", waitServ1_p->serviceName);
    println(__FUNCTION__ ": created wait service: %s", waitServ2_p->serviceName);
    println(__FUNCTION__ ": created wait service: %s", waitServ3_p->serviceName);

    //Create registration
    struct wolaRegistration reg;
    struct wolaRegistration* reg_p = &reg;
    memset(reg_p, 0, sizeof(struct wolaRegistration)); //clear memory
    reg_p->waitServiceFirst_p = waitServ1_p;
    reg_p->waitServiceLast_p = waitServ3_p;
    reg_p->waitServiceCount= 3;

    struct waitService*  result_p = findWaitServiceByName(service2Name, reg_p);
    CuAssertPtrEquals(tc, waitServ2_p, result_p);

    println(__FUNCTION__ ": lookup returned wait service: %s", result_p->serviceName);

    //Set PLO swap area
    PloCompareAndSwapAreaDoubleWord_t swapArea;
    unsigned long long* counter_p = ((unsigned long long*)(&(reg_p->serviceQueuesPLOCounter)));
    swapArea.expectedValue = *counter_p;
    swapArea.replaceValue = swapArea.expectedValue + 1;
    swapArea.compare_p = counter_p;

    removeWaitServiceFromChain(&swapArea, reg_p, waitServ2_p);
    result_p = findWaitServiceByName(service2Name, reg_p);


    if(result_p == NULL &&
        reg_p->waitServiceCount == 2 &&
        waitServ1_p->nextService == waitServ3_p &&
        waitServ3_p->previousService == waitServ1_p){

        println(__FUNCTION__ ": wait service successfully removed");
    }else{
        println(__FUNCTION__ ": removal FAILED");
    }

    CuAssertIntEquals(tc, reg_p->waitServiceCount, 2);
    CuAssertPtrEquals(tc, waitServ1_p->nextService, waitServ3_p);
    CuAssertPtrEquals(tc, waitServ3_p->previousService, waitServ1_p);
    CuAssertPtrEquals(tc, NULL, result_p);

    println(__FUNCTION__ ": exit");
}

/**
 * Add two wait services to the service chain.
 * This ensures that adding to both an empty chain
 * and a non-empty chain will correctly increment the
 * service counter and maintain first/last, previous/next
 * pointers.
 */
void test_addWaitServicesToChain(CuTest * tc) {
    println(__FUNCTION__ ": entry");

    //Create two services
    unsigned char service1Name[] = "myWaitService1";
    struct waitService waitServ1;
    struct waitService* waitServ1_p = &waitServ1;
    memset(waitServ1_p, 0, sizeof(struct waitService)); //clear memory
    memcpy(waitServ1_p->serviceName, service1Name, sizeof(service1Name));

    unsigned char service2Name[] = "myWaitService2";
    struct waitService waitServ2;
    struct waitService* waitServ2_p = &waitServ2;
    memset(waitServ2_p, 0, sizeof(struct waitService)); //clear memory
    memcpy(waitServ2_p->serviceName, service2Name, sizeof(service2Name));

    println(__FUNCTION__ ": created wait service: %s", waitServ1_p->serviceName);
    println(__FUNCTION__ ": created wait service: %s", waitServ2_p->serviceName);

    //Create registration
    struct wolaRegistration reg;
    struct wolaRegistration* reg_p = &reg;
    memset(reg_p, 0, sizeof(struct wolaRegistration)); //clear memory

    //Set PLO swap area
    PloCompareAndSwapAreaDoubleWord_t swapArea;
    unsigned long long* counter_p = ((unsigned long long*)(&(reg_p->serviceQueuesPLOCounter)));
    swapArea.expectedValue = *counter_p;
    swapArea.replaceValue = swapArea.expectedValue + 1;
    swapArea.compare_p = counter_p;

    //Add both services to the chain
    addWaitServiceToChain(&swapArea, reg_p, waitServ1_p);

    //Reset PLO swap area
    counter_p = ((unsigned long long*)(&(reg_p->serviceQueuesPLOCounter)));
    swapArea.expectedValue = *counter_p;
    swapArea.replaceValue = swapArea.expectedValue + 1;
    swapArea.compare_p = counter_p;

    addWaitServiceToChain(&swapArea, reg_p, waitServ2_p);

    if(reg_p->waitServiceLast_p == waitServ2_p &&
        reg_p->waitServiceCount == 2 &&
        waitServ1_p->nextService == waitServ2_p &&
        waitServ2_p->previousService == waitServ1_p){
        println(__FUNCTION__ ": successfully added wait service: %s", service2Name);
    }else{
        println(__FUNCTION__ ": addition FAILED");
    }

    CuAssertIntEquals(tc,2,reg_p->waitServiceCount);
    CuAssertPtrEquals(tc, reg_p->waitServiceLast_p, waitServ2_p);
    CuAssertPtrEquals(tc, waitServ1_p->nextService, waitServ2_p);
    CuAssertPtrEquals(tc, waitServ2_p->previousService, waitServ1_p);

    println(__FUNCTION__ ": exit");
}

/**
 * Add two available services to the service chain.
 * This ensures that adding to both an empty chain
 * and a non-empty chain will correctly increment the
 * service counter and maintain first/last, previous/next
 * pointers.
 */
void test_addAvailableServicesToChain(CuTest * tc) {
    println(__FUNCTION__ ": entry");

    //Create two services
    unsigned char service1Name[] = "myService1";
    struct availableService availServ1;
    struct availableService* availServ1_p = &availServ1;
    memset(availServ1_p, 0, sizeof(struct availableService)); //clear memory
    memcpy(availServ1_p->serviceName, service1Name, sizeof(service1Name));

    unsigned char service2Name[] = "myService2";
    struct availableService availServ2;
    struct availableService* availServ2_p = &availServ2;
    memset(availServ2_p, 0, sizeof(struct availableService)); //clear memory
    memcpy(availServ2_p->serviceName, service2Name, sizeof(service2Name));

    println(__FUNCTION__ ": created available service: %s", availServ1_p->serviceName);
    println(__FUNCTION__ ": created available service: %s", availServ2_p->serviceName);

    //Create registration
    struct wolaRegistration reg;
    struct wolaRegistration* reg_p = &reg;
    memset(reg_p, 0, sizeof(struct wolaRegistration)); //clear memory

    //Set PLO swap area
    PloCompareAndSwapAreaDoubleWord_t swapArea;
    unsigned long long* counter_p = ((unsigned long long*)(&(reg_p->serviceQueuesPLOCounter)));
    swapArea.expectedValue = *counter_p;
    swapArea.replaceValue = swapArea.expectedValue + 1;
    swapArea.compare_p = counter_p;

    //Add services to chain
    int ploRC = addAvailableServiceToChain(&swapArea, reg_p, availServ1_p);

    CuAssertIntEquals(tc, 0, ploRC);

    //Reset plo swap area
    counter_p = ((unsigned long long*)(&(reg_p->serviceQueuesPLOCounter)));
    swapArea.expectedValue = *counter_p;
    swapArea.replaceValue = swapArea.expectedValue + 1;
    swapArea.compare_p = counter_p;

    ploRC = addAvailableServiceToChain(&swapArea, reg_p, availServ2_p);

    CuAssertIntEquals(tc, 0, ploRC);

    if(reg_p->availServiceLast_p == availServ2_p &&
        reg_p->availServiceCount == 2 &&
        availServ1_p->nextService == availServ2_p &&
        availServ2_p->previousService == availServ1_p){
        println(__FUNCTION__ ": successfully added available service: %s", service2Name);
    }else{
        println(__FUNCTION__ ": addition FAILED");
    }

    CuAssertIntEquals(tc, 2, reg_p->availServiceCount);
    CuAssertPtrEquals(tc, reg_p->availServiceLast_p, availServ2_p);
    CuAssertPtrEquals(tc, availServ1_p->nextService, availServ2_p);
    CuAssertPtrEquals(tc, availServ2_p->previousService, availServ1_p);

    println(__FUNCTION__ ": exit");
}

/**
 * Compare two identical strings
 */
void test_isMatch1(CuTest * tc){
    println(__FUNCTION__ ": entry");

    char serviceName[] = "aaa";
    char pattern[] = "aaa";
    int rc = 0;

    rc = isMatch(serviceName, pattern);

    CuAssertIntEquals(tc, 0, rc);

    println(__FUNCTION__ ": exit rc = %d", rc);
}

/**
 * Compare two mismatch strings
 */
void test_isMatch2(CuTest * tc){
    println(__FUNCTION__ ": entry");

    char serviceName[] = "aaa";
    char pattern[] = "aab";
    int rc = 0;

    rc = isMatch(serviceName, pattern);

    CuAssertIntEquals(tc, -1, rc);

    println(__FUNCTION__ ": exit rc = %d", rc);
}

/**
 * Compare two strings with a leading wildcard
 */
void test_isMatch3(CuTest * tc){
    println(__FUNCTION__ ": entry");

    char serviceName[] = "accc";
    char pattern[] = "*ccc";
    int rc = 0;

    rc = isMatch(serviceName, pattern);

    CuAssertIntEquals(tc, 0, rc);

    println(__FUNCTION__ ": exit rc = %d", rc);
}

/**
 * Compare two strings with leading wildcard and no leading characters
 */
void test_isMatch4(CuTest * tc){
    println(__FUNCTION__ ": entry");

    char serviceName[] = "aaa";
    char pattern[] = "*aaa";
    int rc = 0;

    rc = isMatch(serviceName, pattern);

    CuAssertIntEquals(tc, 0, rc);

    println(__FUNCTION__ ": exit rc = %d", rc);
}

/**
 * Compare two strings with center wildcard matching
 */
void test_isMatch5(CuTest * tc){
    println(__FUNCTION__ ": entry");

    char serviceName[] = "aaaXXbb";
    char pattern[] = "aaa*bb";
    int rc = 0;

    rc = isMatch(serviceName, pattern);

    CuAssertIntEquals(tc, 0, rc);

    println(__FUNCTION__ ": exit rc = %d", rc);
}

/**
 * Compare two strings with center wildcard not matching
 */
void test_isMatch6(CuTest * tc){
    println(__FUNCTION__ ": entry");

    char serviceName[] = "aaa34XXY";
    char* pattern = "aaa*XXX";
    int rc = 0;

    rc = isMatch(serviceName, pattern);

    CuAssertIntEquals(tc, -1, rc);

    println(__FUNCTION__ ": exit rc = %d", rc);
}

/**
 * Compare two unmatching strings where all
 * of the pattern is found in the string.
 */
void test_isMatch7(CuTest * tc){
    println(__FUNCTION__ ": entry");

    char serviceName[] = "aabbb";
    char pattern[] = "abbb";
    int rc = 0;

    rc = isMatch(serviceName, pattern);

    CuAssertIntEquals(tc, -2, rc);

    println(__FUNCTION__ ": exit rc = %d", rc);
}

/**
 * Compare two strings with trailing wildcard
 */
void test_isMatch8(CuTest * tc){
    println(__FUNCTION__ ": entry");

    char serviceName[] = "aaa34";
    char pattern[] = "aaa*";
    int rc = 0;

    rc = isMatch(serviceName, pattern);

    CuAssertIntEquals(tc, 0, rc);

    println(__FUNCTION__ ": exit rc = %d", rc);
}

/**
 * Compare two strings with no trailing wildcard
 * but trailing string characters
 */
void test_isMatch9(CuTest * tc){
    println(__FUNCTION__ ": entry");

    char serviceName[] = "aa34";
    char pattern[] = "aa";
    int rc = 0;

    rc = isMatch(serviceName, pattern);

    CuAssertIntEquals(tc, -3, rc);

    println(__FUNCTION__ ": exit rc = %d", rc);
}

void test_isMatch10(CuTest * tc){
    println(__FUNCTION__ ": entry");

    char serviceName[] = "a";
    char pattern[] = "*";
    int rc = 0;

    rc = isMatch(serviceName, pattern);

    CuAssertIntEquals(tc, 0, rc);

    println(__FUNCTION__ ": exit rc = %d", rc);
}

void test_isMatch11(CuTest * tc){
    println(__FUNCTION__ ": entry");

    char serviceName[] = "aaa";
    char pattern[] = "*";
    int rc = 0;

    rc = isMatch(serviceName, pattern);

    CuAssertIntEquals(tc, 0, rc);

    println(__FUNCTION__ ": exit rc = %d", rc);
}

void test_isMatch12(CuTest * tc){
    println(__FUNCTION__ ": entry");

    char serviceName[] = "a";
    char pattern[] = "**";
    int rc = 0;

    rc = isMatch(serviceName, pattern);

    CuAssertIntEquals(tc, 0, rc);

    println(__FUNCTION__ ": exit rc = %d", rc);
}

void test_isMatch13(CuTest * tc){
    println(__FUNCTION__ ": entry");

    char serviceName[] = "aaa";
    char pattern[] = "**";
    int rc = 0;

    rc = isMatch(serviceName, pattern);

    CuAssertIntEquals(tc, 0, rc);

    println(__FUNCTION__ ": exit rc = %d", rc);
}

void test_isMatch14(CuTest * tc){
    println(__FUNCTION__ ": entry");

    char serviceName[] = "a";
    char pattern[] = "*a*";
    int rc = 0;

    rc = isMatch(serviceName, pattern);

    CuAssertIntEquals(tc, 0, rc);

    println(__FUNCTION__ ": exit rc = %d", rc);
}

void test_isMatch15(CuTest * tc){
    println(__FUNCTION__ ": entry");

    char serviceName[] = "aaa";
    char pattern[] = "*a*";
    int rc = 0;

    rc = isMatch(serviceName, pattern);

    CuAssertIntEquals(tc, 0, rc);

    println(__FUNCTION__ ": exit rc = %d", rc);
}

void test_isMatch16(CuTest * tc){
    println(__FUNCTION__ ": entry");

    char serviceName[] = "ab";
    char pattern[] = "ab**";
    int rc = 0;

    rc = isMatch(serviceName, pattern);

    CuAssertIntEquals(tc, 0, rc);

    println(__FUNCTION__ ": exit rc = %d", rc);
}

void test_isMatch17(CuTest * tc){
    println(__FUNCTION__ ": entry");

    char serviceName[] = "abc";
    char pattern[] = "ab**";
    int rc = 0;

    rc = isMatch(serviceName, pattern);

    CuAssertIntEquals(tc, 0, rc);

    println(__FUNCTION__ ": exit rc = %d", rc);
}

void test_isMatch18(CuTest * tc){
    println(__FUNCTION__ ": entry");

    char serviceName[] = "abc";
    char pattern[] = "**bc";
    int rc = 0;

    rc = isMatch(serviceName, pattern);

    CuAssertIntEquals(tc, 0, rc);

    println(__FUNCTION__ ": exit rc = %d", rc);
}

void test_isMatch19(CuTest * tc){
    println(__FUNCTION__ ": entry");

    char serviceName[] = "apaycpaye";
    char pattern[] = "a*c*e";
    int rc = 0;

    rc = isMatch(serviceName, pattern);

    CuAssertIntEquals(tc, 0, rc);

    println(__FUNCTION__ ": exit rc = %d", rc);
}

void test_PutAndGet(CuTest * tc){
    println(__FUNCTION__ ": entry");
    unsigned char callerKey = 2;

    //Setup Anchor (link cell pool ID)
    WolaSharedMemoryAnchor_t * wolaAnchor_p = (WolaSharedMemoryAnchor_t *) malloc(WOLA_SMA_SIZE_MB * 1024 * 1024);
    memset(wolaAnchor_p, 0, sizeof(struct wolaSharedMemoryAnchor)); //clear memory
    wolaAnchor_p->anchorRequestedSize = WOLA_SMA_SIZE_MB * 1024 * 1024;

    //Setup CellPool (get ID)
    long long outer_cp_size;
    void * outer_cp_addr = getOuterCellPoolAddrAndSize( wolaAnchor_p, &outer_cp_size );

    buildCellPoolFlags flags;
    memset(&flags, 0, sizeof(buildCellPoolFlags));
    wolaAnchor_p->outerCellPoolID = buildCellPool(outer_cp_addr,
                                               outer_cp_size,
                                               1024 * 1024,
                                               "BBGZWASP",
                                               flags);

    wolaAnchor_p->availServiceCellPoolID = buildInnerCellPool( wolaAnchor_p, sizeof(struct availableService), "BBGZASQP");


    //bboashr_p->waitServiceCellPoolID = buildInnerCellPool( bboashr_p, sizeof(struct waitService), "BBGZWWSP");

     println(__FUNCTION__ ": created Cell Pool: %d", wolaAnchor_p->availServiceCellPoolID);

    //Setup Registration (link Anchor)
    struct wolaRegistration reg;
    struct wolaRegistration* reg_p = &reg;
    memset(reg_p, 0, sizeof(struct wolaRegistration)); //clear memory

    reg_p->wolaAnchor_p = wolaAnchor_p;
    reg_p->serviceQueuesState = 0;
    reg_p->serviceQueuesPLOCounter = 0;
    reg_p->waitServiceFirst_p = NULL;

    //Setup ConnLink (this will be put in the available service and returned by the get call)
    struct localCommClientConnectionHandle clientConnHandle;
    struct localCommClientConnectionHandle* clientConnHandle_p = &clientConnHandle;
    memset(clientConnHandle_p, 'A', sizeof(struct localCommClientConnectionHandle));

    //Create service name
    unsigned char serviceName[] = "myService";

    //Drive put
    putClientService(clientConnHandle_p, reg_p, serviceName, 0);

    //Drive get
    struct localCommClientConnectionHandle  clientConnHandleResult;
    struct localCommClientConnectionHandle* clientConnHandleResult_p = &clientConnHandleResult;
    memset(clientConnHandleResult_p, 'B', sizeof(*clientConnHandleResult_p));
    getClientService(reg_p, serviceName, 0, clientConnHandleResult_p);

    //Verify Conn link that has been recieved
    CuAssertMemBytesEqual(tc, 'A', clientConnHandleResult_p, sizeof(clientConnHandleResult_p));


    println(__FUNCTION__ ": exit");
}

void test_waitServiceCreation(CuTest * tc){

    println(__FUNCTION__ ": entry");

    //Setup Anchor (link cell pool ID)
    WolaSharedMemoryAnchor_t * wolaAnchor_p = (WolaSharedMemoryAnchor_t *) malloc(WOLA_SMA_SIZE_MB * 1024 * 1024);
    memset(wolaAnchor_p, 0, sizeof(struct wolaSharedMemoryAnchor)); //clear memory
    wolaAnchor_p->anchorRequestedSize = WOLA_SMA_SIZE_MB * 1024 * 1024;

    //Setup CellPool (get ID)
    long long outer_cp_size;
    void * outer_cp_addr = getOuterCellPoolAddrAndSize( wolaAnchor_p, &outer_cp_size );

    buildCellPoolFlags flags;
    memset(&flags, 0, sizeof(buildCellPoolFlags));
    wolaAnchor_p->outerCellPoolID = buildCellPool(outer_cp_addr,
                                               outer_cp_size,
                                               1024 * 1024,
                                               "BBGZWASP",
                                               flags);

//    wolaAnchor_p->availServiceCellPoolID = buildInnerCellPool( wolaAnchor_p, sizeof(struct availableService), "BBGZASQP");


    wolaAnchor_p->waitServiceCellPoolID = buildInnerCellPool( wolaAnchor_p, sizeof(struct waitService), "BBGZWSQP");

     println(__FUNCTION__ ": created Cell Pool: %d", wolaAnchor_p->availServiceCellPoolID);

    //Setup Registration (link Anchor)
    struct wolaRegistration reg;
    struct wolaRegistration* reg_p = &reg;
    memset(reg_p, 0, sizeof(struct wolaRegistration)); //clear memory

    reg_p->wolaAnchor_p = wolaAnchor_p;
    reg_p->serviceQueuesState = 0;
    reg_p->serviceQueuesPLOCounter = 0;
    reg_p->waitServiceFirst_p = NULL;

    //Setup ConnLink (this will be put in the available service and returned by the get call)
    struct localCommClientConnectionHandle clientConnHandle;
    struct localCommClientConnectionHandle* clientConnHandle_p = &clientConnHandle;
    memset(clientConnHandle_p, 0, sizeof(struct localCommClientConnectionHandle));

    //Create service name
    unsigned char serviceName[] = "myService";

    //Get pet
    iea_PEToken currentPET = {{0}};
    // server_process_data is not available in the test env.
    // -rx- server_process_data* spd_p = getServerProcessData();

    // -rx- pickup((PetVet*)spd_p->petvet, &currentPET);

    struct waitService* newWaitService_p = createWaitService(currentPET, serviceName, reg_p);

    CuAssertPtrNotNull(tc, newWaitService_p);
    CuAssertStrEquals(tc, serviceName, newWaitService_p->serviceName);


    println(__FUNCTION__ ": exit");
}

/**
 * for sanity's sake
 */
void test_structSizes(CuTest * tc) {

    CuAssertIntEquals(tc, 0x180, sizeof(WaitService_t));
    CuAssertIntEquals(tc, 0x180, sizeof(AvailableService_t));
}

/**
 * Test starting the timer, calling pause, and having the timer pop
 * and release the pause.
 */
void test_waitServiceSTIMERM(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    PetVet test_petvet __attribute__((aligned(16))) ;
    initializePetVet(&test_petvet, 5, NULL);

    iea_PEToken pet = {{0}};
    int rc = pickup((PetVet*)&test_petvet, &pet);
    CuAssertIntEquals(tc, 0, rc);

    WolaRegistration_t reg;
    memset(&reg, 0, sizeof(reg)); //clear memory

    // Create the waitService
    WaitService_t waitService;
    initializeWaitService( &waitService, pet, "test_waitServiceSTIMERM", &reg);

    // Add the waitService to the chain.  The STIMERM exit routine will look for
    // it there before releasing.
    reg.waitServiceFirst_p = &waitService;
    reg.waitServiceLast_p = &waitService;

    println(__FUNCTION__ ": starting STIMERM with a 3 second timeout... ");

    rc = startTimerForWaitService(&waitService, 3, &test_petvet) ;
    CuAssertIntEquals(tc, 0, rc);
    CuAssertTrue(tc, (long long)waitService.mvsTimerID != 0L);

    println(__FUNCTION__ ": about to PAUSE. This pause should be released by the STIMER... ");
    println(__FUNCTION__ ": (if this is the last line in the test output well then I guess that didn't happen... sorry)");

    rc = pauseWaitService(&waitService);

    println(__FUNCTION__ ": pause released with RC: %d", rc);

    CuAssertIntEquals(tc, WOLA_SERVICE_QUEUES_RC_PAUSE_TIMEOUT, rc);

    rc = cancelTimerForWaitService(&waitService) ;
    CuAssertIntEquals(tc, 1, rc);   // 1 means the timer popped.

    board((PetVet*)&test_petvet, pet);

    println(__FUNCTION__ ": exit");
}

/**
 * Test starting and canceling the timer without having it pop.
 */
void test_waitServiceSTIMERMWithoutPause(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    PetVet test_petvet __attribute__((aligned(16))) ;
    initializePetVet(&test_petvet, 5, NULL);

    iea_PEToken pet = {{0}};
    int rc = pickup((PetVet*)&test_petvet, &pet);
    CuAssertIntEquals(tc, 0, rc);

    WaitService_t waitService;
    initializeWaitService( &waitService, pet, "test_waitServiceSTIMERM", NULL );

    println(__FUNCTION__ ": starting STIMERM with a 3 second timeout... ");

    rc = startTimerForWaitService(&waitService, 3, &test_petvet) ;
    CuAssertIntEquals(tc, 0, rc);
    CuAssertTrue(tc, (long long)waitService.mvsTimerID != 0L);

    rc = cancelTimerForWaitService(&waitService) ;
    CuAssertIntEquals(tc, 0, rc);   // 0 means the timer never popped.

    board((PetVet*)&test_petvet, pet);

    println(__FUNCTION__ ": exit");
}

/**
 *
 */
void test_initializeWaitService(CuTest * tc){

    println(__FUNCTION__ ": entry");

    // Setup parms for initWaitService
    struct wolaRegistration reg;
    char * serviceName = "myservice";
    iea_PEToken pet = {{1}};

    WaitService_t waitService;
    WaitService_t * waitService_p = initializeWaitService(&waitService,
                                                          pet,
                                                          serviceName,
                                                          &reg);

    CuAssertPtrEquals(tc, &waitService, waitService_p);
    CuAssertStrEquals(tc, serviceName, waitService.serviceName);
    CuAssertIntEquals(tc, strlen(serviceName), waitService.serviceNameLength);
    CuAssertPtrEquals(tc, &reg, waitService.thisRegistration);
    CuAssertMemEquals(tc, BBOAWSRV_EYE, waitService.eye, sizeof(waitService.eye));
    CuAssertMemEquals(tc, &pet, &waitService.pet, sizeof(waitService.pet));
    CuAssertPtrIsNull(tc, waitService.previousService);
    CuAssertPtrIsNull(tc, waitService.nextService);

    println(__FUNCTION__ ": exit");
}

/**
 *
 */
void test_initializeAvailableService(CuTest * tc){

    println(__FUNCTION__ ": entry");

    // Setup parms for initAvailableService
    struct wolaRegistration reg;
    struct localCommClientConnectionHandle clientConnHandle;
    char * serviceName = "myservice";
    memset(&clientConnHandle, 2, sizeof(clientConnHandle));

    AvailableService_t availService;
    AvailableService_t * availService_p = initializeAvailableService(&availService,
                                                                     &clientConnHandle,
                                                                     serviceName,
                                                                     &reg);

    CuAssertPtrEquals(tc, &availService, availService_p);
    CuAssertStrEquals(tc, serviceName, availService.serviceName);
    CuAssertIntEquals(tc, strlen(serviceName), availService.serviceNameLength);
    CuAssertPtrEquals(tc, &reg, availService.thisRegistration);
    CuAssertMemEquals(tc, BBOAASRV_EYE, availService.eye, sizeof(availService.eye));
    CuAssertMemEquals(tc, &clientConnHandle, &availService.client_conn_handle, sizeof(availService.client_conn_handle));
    CuAssertPtrIsNull(tc, availService.previousService);
    CuAssertPtrIsNull(tc, availService.nextService);

    println(__FUNCTION__ ": exit");
}

/**
 *
 */
void test_strnlen(CuTest * tc) {
    CuAssertIntEquals(tc, 5, strnlen("hello", 10));
    CuAssertIntEquals(tc, 5, strnlen("hello", 5));
    CuAssertIntEquals(tc, 5, strnlen("thisstringisverylong", 5));
    CuAssertIntEquals(tc, 5, strnlen(NULL, 5));
}

/**
 * The test suite getter method.
 *
 * @return The suite of tests in this part.
 */
CuSuite * server_wola_service_queues_test_suite() {

    CuSuite* suite = CuSuiteNew("server_wola_service_queues_test");

    //Test isMatch function with multiple scenarios.
    SUITE_ADD_TEST(suite, test_isMatch1);
    SUITE_ADD_TEST(suite, test_isMatch2);
    SUITE_ADD_TEST(suite, test_isMatch3);
    SUITE_ADD_TEST(suite, test_isMatch4);
    SUITE_ADD_TEST(suite, test_isMatch5);
    SUITE_ADD_TEST(suite, test_isMatch6);
    SUITE_ADD_TEST(suite, test_isMatch7);
    SUITE_ADD_TEST(suite, test_isMatch8);
    SUITE_ADD_TEST(suite, test_isMatch9);
    SUITE_ADD_TEST(suite, test_isMatch10);
    SUITE_ADD_TEST(suite, test_isMatch11);
    SUITE_ADD_TEST(suite, test_isMatch12);
    SUITE_ADD_TEST(suite, test_isMatch13);
    SUITE_ADD_TEST(suite, test_isMatch14);
    SUITE_ADD_TEST(suite, test_isMatch15);
    SUITE_ADD_TEST(suite, test_isMatch16);
    SUITE_ADD_TEST(suite, test_isMatch17);
    SUITE_ADD_TEST(suite, test_isMatch18);
    SUITE_ADD_TEST(suite, test_isMatch19);

    SUITE_ADD_TEST(suite, test_findClientAvailableServiceByName);
    SUITE_ADD_TEST(suite, test_findWaitServiceByName);

    SUITE_ADD_TEST(suite, test_findClientAvailableServiceByNameNotFound);
    SUITE_ADD_TEST(suite, test_findWaitServiceByNameNotFound);

    SUITE_ADD_TEST(suite, test_removeOnlyAvailableServiceFromChain);
    SUITE_ADD_TEST(suite, test_removeOnlyWaitServiceFromChain);

    SUITE_ADD_TEST(suite, test_removeAvailableServiceFromMiddleOfChain);
    SUITE_ADD_TEST(suite, test_removeWaitServiceFromMiddleOfChain);

    SUITE_ADD_TEST(suite, test_addAvailableServicesToChain);
    SUITE_ADD_TEST(suite, test_addWaitServicesToChain);

    SUITE_ADD_TEST(suite, test_waitServiceCreation);

    SUITE_ADD_TEST(suite, test_PutAndGet);
    SUITE_ADD_TEST(suite, test_structSizes);
    SUITE_ADD_TEST(suite, test_waitServiceSTIMERM);
    SUITE_ADD_TEST(suite, test_waitServiceSTIMERMWithoutPause);
    SUITE_ADD_TEST(suite, test_initializeWaitService);
    SUITE_ADD_TEST(suite, test_initializeAvailableService);
    SUITE_ADD_TEST(suite, test_strnlen);

    return suite;
}

