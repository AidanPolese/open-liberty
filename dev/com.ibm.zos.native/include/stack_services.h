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

/**
 * @file
 *
 * Structures and methods for managing a lock free, concurrent stack.
 */
#ifndef _BBOZ_STACK_SERVICES_H
#define _BBOZ_STACK_SERVICES_H

// Forward declaration of a concurrent_stack_element element
typedef struct concurrent_stack_element concurrent_stack_element;

/**
 * A stack header.
 */
typedef struct concurrent_stack {
    int                        concurrent_stack_sequence;              /*     0x000*/
    unsigned short             concurrent_stack_cur_element_count;     /*     0x004*/
    unsigned short             _reserved0; // Reserved for flags              0x006
    concurrent_stack_element*  concurrent_stack_stack_element_p;       /*     0x008*/
} concurrent_stack;                                                    /*     0x010*/

/**
 * A stack element.  Keep the size a multiple of 16 please.
 */
struct concurrent_stack_element {
    concurrent_stack_element*  stack_element_next_p;                   /*     0x000*/
    concurrent_stack_element*  stack_element_prev_p;                   /*     0x008*/
    concurrent_stack           old_cdsg_area;                          /*     0x010*/
    concurrent_stack           new_cdsg_area;                          /*     0x020*/
    int                        cdsgRetCode;                            /*     0x030*/
    unsigned char              reserved1[12];                          /*     0x034*/
};                                                                     /*     0x040*/

/**
 * Routine to push a concurrent_stack_element on the specified concurrent_stack.
 * @param inputStack_p    concurrent_stack to push on to
 * @param inputElement_p  concurrent_stack_element to push onto the stack
 *
 */
void push_on_stack(concurrent_stack* inputStack_p, concurrent_stack_element* inputElement_p);

/**
 * Routine to push a concurrent_stack_element on the specified concurrent_stack if the
 * push will not result in the stack having more than the specified number of
 * elements on it.
 *
 * @param inputStack_p    concurrent_stack to push on to.
 * @param inputElement_p  concurrent_stack_element to push onto the stack.
 * @param maxElements     The maximum number of elements allowed on the stack
 *                        after the push in order for the push to be successful.
 *
 * @return 0 if the push was successful.  If the push is not successful, the
 *         caller is responsible for disposing of the inputElement in some way.
 */
int push_on_stack_cond_max_elements(concurrent_stack* inputStack_p, concurrent_stack_element* inputElement_p, unsigned short maxElements);

/**
 * Routine to pop a concurrent_stack_element off the specified concurrent_stack.
 * @param inputStack_p concurrent_stack to pop off of
 * @return a pointer to a concurrent_stack_element
 *
 */
concurrent_stack_element* pop_off_stack(concurrent_stack* inputStack_p);

/**
 * Routine to pop multiple concurrent_stack_elements off the specified concurrent_stack.
 *
 * @param inputStack_p concurrent_stack to pop off of.
 * @param batchSize The number of elements to pop off the stack.  The pop will
 *                  only be successful if this many elements can be removed
 *                  from the stack.
 * @param elements A pointer to an array of double words where the concurrent_stack_element
 *                 addresses will be stored.  The array should be long enough to
 *                 store the number of elements indicated by batchSize (ie a
 *                 batch size of 4 would require 4 double words of storage).
 *
 * @return 0 if the pop was successful, and elements was populated with the
 *         addresses of the stack elements.  Nonzero if the pop was not
 *         successful.
 */
int pop_batch_off_stack(concurrent_stack* inputStack_p, unsigned short batchSize, concurrent_stack_element** elements);

/**
 * Routine to get entire chain of concurrent_stack_elements off the specified concurrent_stack.
 * @param inputStack_p concurrent_stack to remove the entire list of concurrent_stack_elements
 * @return a pointer to a chain of concurrent_stack_elements
 *
 */
concurrent_stack_element* get_entire_stack(concurrent_stack* inputStack_p);

/**
 * Routine to check if the specified concurrent_stack is empty.
 * @param inputStack_p concurrent_stack to check for empty
 * @return a 1 if the stack is empty and 0 if the stack is not empty
 *
 */
int is_stack_empty(concurrent_stack* inputStack_p);

static const char  zero_stack_header[sizeof(concurrent_stack)] = {{0}};

#ifdef __IBM_METAL__
/**
 * Routine to push a key 8 concurrent_stack_element on the specified key 8 concurrent_stack
 * when running in a key 2 pc routine.
 * @param inputStack_p    concurrent_stack to push on to
 * @param inputElement_p  concurrent_stack_element to push onto the stack
 *
 */
void push_on_key8_stack(concurrent_stack* inputStack_p, concurrent_stack_element * inputElement_p);

/**
 * Routine to pop a key 8 concurrent_stack_element off the specified key 8 concurrent_stack
 * when running in a key 2 pc routine.
 * @param inputStack_p concurrent_stack to pop off of
 * @return a pointer to a concurrent_stack_element
 *
 */
concurrent_stack_element* pop_off_key8_stack(concurrent_stack* inputStack_p, concurrent_stack_element* inputElement_p);

#endif // __IBM_METAL__






// Forward declaration
typedef struct ElementDT ElementDT;

/**
 * A double threaded element
 */
struct ElementDT {
    ElementDT*  element_next_p;
    ElementDT*  element_prev_p;
};

/**
 * Clear the chain pointers in the supplied element
 */
void clearPtrs(ElementDT*  element_p);


/**
 * A bounded stack.  No serialization.
 */
static const int BOUNDED_UNSERIALIZED_STACK_LIMIT_DEFAULT = 1024;
typedef struct UnSerializedStack {
    int                element_count;
    int                stack_limit;
    ElementDT*         head_p;
} UnSerializedStack;

/**
 * Initialized the stack
 */
void initializeUnSerializedStack(UnSerializedStack* bs_p, int stackLimit);

/**
 * Add an element to the stack
 */
int pushOnUnSerializedStack(UnSerializedStack* bs_p, ElementDT * element);

/**
 * Pop an Element from the stack
 */
ElementDT * popOffUnSerializedStack(UnSerializedStack* bs_p);

/**
 * Grab the entire stack
 */
ElementDT  * popEntireUnSerializedStack(UnSerializedStack* bs_p);


/**
 * A Stack header.
 */
typedef struct StackHeader {
    void* first;             // Top of Stack
} StackHeader;

/**
 * Structure for Stack
 */
typedef struct Stack {
   StackHeader stack_header;
} Stack;                     // Size 0x08

/**
 * Return True if stack is empty
 */
int isStackEmpty(Stack* baseStack);

/**
 * A bounded Stack with serialization using CDSG
 * use pointer and sequence number for serialization.
 */
typedef struct SerializedStack {
    Stack baseStack;
    int sequenceNum;
    int counter;
    int limit;
    unsigned char reserved[12];
} SerializedStack;           // Size 0x20

/**
 * Routine to initialize the specified SerializedStack.
 * Note: elements need to come from a cell pool instead of storage obtain.
 * if not, there is a window where pop_cdsg can reference already released storage.
 *
 * @param serializedStack SerializedStack to initialize.
 * @param limit Maximum number of elements allowed on the stack.
 *
 */
void initializeSerializedStack(SerializedStack* serializedStack, int limit);

/**
 * Routine to push a ElementDT on the specified SerializedStack if the
 * push will not result in the stack going over the element limit.
 *
 * @param serializedStack SerializedStack to push on to.
 * @param newStackElement ElementDT to push onto the stack.
 *
 * @return 0 if the push was successful.  If the push is not successful, the
 *         caller is responsible for disposing of the newStackElement in some way.
 *
 */
int pushOnSerializedStack(SerializedStack* serializedStack, ElementDT* newStackElement);

/**
 * Routine to pop a ElementDT off the specified SerializedStack.
 *
 * @param serializedStack SerializedStack to pop off of.
 *
 * @return A pointer to a ElementDT.
 *
 */
ElementDT* popOffSerializedStack(SerializedStack* serializedStack);

#endif
