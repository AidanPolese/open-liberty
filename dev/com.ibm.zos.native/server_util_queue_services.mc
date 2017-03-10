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
#include <metal.h>
#include <stdlib.h>
#include "include/stack_services.h"
#include "include/util_queue_services.h"

/**
 * Clear the pointers of the queue
 */
void initializeQueue(Que* myQueue) {
    myQueue->queue_header.head = NULL;
    myQueue->queue_header.tail = NULL;
}


/**
 * Return True if queue is empty
 */
int isQueueEmpty(Que* myQueue) {
    return (myQueue->queue_header.head ? 0 : 1);
}

/**
 * Get the pointer to the first element in the queue
 */
ElementDT* getFirst(Que* myQueue) {
    if (!isQueueEmpty(myQueue)) {
        return ( myQueue->queue_header.head );
    } else {
        return ( NULL );
    }
}

/**
 * Get the pointer to the last element in the queue
 */
ElementDT* getLast(Que* myQueue) {
    if (!isQueueEmpty(myQueue)) {
        return ( myQueue->queue_header.tail );
    } else {
        return ( NULL );
    }
}


/**
* Routine to push QElement on the queue
* @param newElement  CompletionQElement to push onto the queue.
*
*/
void enqueue(Que* myQueue, ElementDT* newElement) {
    ElementDT* lastElement;
    // If the queue is not currently empty...
    if (!isQueueEmpty(myQueue)) {
        // Add the new element to the end of the list
        newElement->element_next_p = NULL;
        newElement->element_prev_p = myQueue->queue_header.tail;
        lastElement = myQueue->queue_header.tail;
        lastElement->element_next_p = newElement;
        myQueue->queue_header.tail = newElement;
    } else {  // the queue is currently empty...
        newElement->element_next_p = NULL;
        newElement->element_prev_p = NULL;
        // Set the head and tail to this element
        myQueue->queue_header.head = newElement;
        myQueue->queue_header.tail = newElement;
    }

}


/**
 * Routine to pop a QElement from the top of the queue
 * @return a pointer to a QElement.
 *
 */
ElementDT* dequeue(Que* myQueue) {
    ElementDT* next_Element;    // Next Element
    ElementDT* target_Element;  // Target Element (First)

    // if the queue is not currently empty...
    if (!isQueueEmpty(myQueue)) {
        // Get the first element
        target_Element = getFirst(myQueue);
        // Get the next element
        next_Element = target_Element->element_next_p;
        // Set head of queue to next element
        myQueue->queue_header.head = next_Element;
        // if the next element is null
        if (next_Element == NULL) {
           // the list is empty now, set last
            myQueue->queue_header.tail = NULL;
        } else {  // there is a next element
            next_Element->element_prev_p = NULL;
            // clear the removed element's NEXT pointer
            target_Element->element_next_p = NULL;
        }
    }
    else {  // the queue is currently empty...return NULL pointer
        return NULL;
    }

    return target_Element;
}
