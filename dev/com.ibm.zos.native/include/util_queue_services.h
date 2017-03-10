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

/**
 * @file
 *
 * Structures and methods for managing a queue.
 */
#ifndef _ZAIO_QUEUE_SERVICES_H
#define _ZAIO_QUEUE_SERVICES_H

/**
 * A queue header.
 */
typedef struct QueHeader {
    void* head;             // Head of Queue
    void* tail;             // Tail of Queue
} QueHeader;

/**
 * Structure for Queue
 */
typedef struct Que{
   QueHeader queue_header;
} Que;

struct ElementDT;
void initializeQueue(Que* myQueue);
int isQueueEmpty(Que* myQueue);
struct ElementDT* getFirst(Que* myQueue);
struct ElementDT* getLast(Que* myQueue);
void enqueue(Que* myQueue, struct ElementDT* newElement);
struct ElementDT* dequeue(Que* myQueue);

#endif /* _ZAIO_QUEUE_SERVICES_H */
