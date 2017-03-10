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

#include "include/mvs_utils.h"
#include "stack_services_c.c"

/**
* Routine to push a key 8 concurrent_stack_element on the specified key 8 concurrent_stack
* when running in a key 2 pc routine.
*
* @param inputStack_p    concurrent_stack to push on to.
* @param inputElement_p  concurrent_stack_element to push onto the stack.
*
*/
void push_on_key8_stack(concurrent_stack * inputStack_p, concurrent_stack_element * inputElement_p) {
    concurrent_stack localNew_cdsg_area;

    memcpy_dk(&(inputElement_p->old_cdsg_area), inputStack_p, sizeof(inputElement_p->old_cdsg_area), 8);

    do {
        memcpy(&localNew_cdsg_area, &(inputElement_p->old_cdsg_area), sizeof(localNew_cdsg_area));
        localNew_cdsg_area.concurrent_stack_sequence += 1;
        localNew_cdsg_area.concurrent_stack_cur_element_count += 1;
        localNew_cdsg_area.concurrent_stack_stack_element_p = inputElement_p;

        memcpy_dk(&(inputElement_p->stack_element_next_p), &(inputElement_p->old_cdsg_area.concurrent_stack_stack_element_p),
                  sizeof(inputElement_p->stack_element_next_p), 8);

        memcpy_dk(&(inputElement_p->new_cdsg_area), &localNew_cdsg_area, sizeof(localNew_cdsg_area), 8);

        unsigned char old_key;
        /* Store the original key                                          */
        __asm(" IPK\n"
              " STC 2,%0\n" :
              "=m"(old_key) : :
              "r2");
        /* switch to key 8 */
        unsigned char new_key = 0x80;
        __asm(" MODESET KEYADDR=%0,WORKREG=11" : :
              "m"(new_key) : "r0","r1","r11","r14","r15");

        /* put element on stack */
        inputElement_p->cdsgRetCode = __cdsg(&(inputElement_p->old_cdsg_area),     // old
                                             inputStack_p,                         // current
                                             &(inputElement_p->new_cdsg_area));    // new
        /* return to original key */
        __asm(" MODESET KEYADDR=%0,WORKREG=11" : :
              "m"(old_key) : "r0","r1","r11","r14","r15");
    } while(inputElement_p->cdsgRetCode);

} // end push_on_key8_stack


/**
 * Routine to pop a key 8 concurrent_stack_element off the specified key 8 concurrent_stack
 * when running in a key 2 pc routine.
 *
 * @param inputStack_p concurrent_stack to pop off of.
 * @param inputElement_p concurrent_stack_element to pop off of the stack.
 *
 * @return a pointer to a concurrent_stack_element.
 *
 */
concurrent_stack_element * pop_off_key8_stack(concurrent_stack * inputStack_p, concurrent_stack_element * inputElement_p)
{
  int cdsgRetCode;
  concurrent_stack_element * outputElement_p = 0;
  concurrent_stack localNew_cdsg_area;

  memcpy_dk(&(inputElement_p->old_cdsg_area), inputStack_p, sizeof(inputElement_p->old_cdsg_area), 8);

  do {
      memcpy(&localNew_cdsg_area, &(inputElement_p->old_cdsg_area), sizeof(inputElement_p->new_cdsg_area));

      if (localNew_cdsg_area.concurrent_stack_stack_element_p == 0 ) {
          outputElement_p = 0;
          break;
      }
      localNew_cdsg_area.concurrent_stack_sequence += 1;
      localNew_cdsg_area.concurrent_stack_cur_element_count -= 1;
      outputElement_p = localNew_cdsg_area.concurrent_stack_stack_element_p;
      localNew_cdsg_area.concurrent_stack_stack_element_p = outputElement_p->stack_element_next_p;

      memcpy_dk(&(inputElement_p->new_cdsg_area), &localNew_cdsg_area, sizeof(localNew_cdsg_area), 8);

      unsigned char old_key;
      /* Store the original key                                          */
      __asm(" IPK\n"
            " STC 2,%0\n" :
            "=m"(old_key) : :
            "r2");
      /* switch to key 8 */
      unsigned char new_key = 0x80;
      __asm(" MODESET KEYADDR=%0,WORKREG=11" : :
            "m"(new_key) : "r0","r1","r11","r14","r15");
      /* pop off stack */
      inputElement_p->cdsgRetCode = __cdsg(&(inputElement_p->old_cdsg_area),     // old
                                           inputStack_p,                         // current
                                           &(inputElement_p->new_cdsg_area));    // new
      /* return to original key */
      __asm(" MODESET KEYADDR=%0,WORKREG=11" : :
            "m"(old_key) : "r0","r1","r11","r14","r15");

  } while(inputElement_p->cdsgRetCode);
  //---------------------------------------------------------------------
  // Repeat until the compare and double swap is successful
  //---------------------------------------------------------------------

  return outputElement_p;
} // end pop_off_key8_stack




/**
 *
 * A double threaded element
 *
 */

void clearPtrs(ElementDT*  element_p) {
    element_p->element_next_p = NULL;
    element_p->element_prev_p = NULL;
}

/**
 * Return True if stack is empty
 */
int isStackEmpty(Stack* baseStack) {
    return (baseStack->stack_header.first ? 0 : 1);
}

/**
 * A bounded stack.  No serialization.
 */

/**
 * Initialize the stack
 * @param bs_p Pointer to the bounded stack
 * @param stackLimit Maximum number of allowed elements in the stack
 */
void initializeUnSerializedStack(UnSerializedStack* bs_p, int stackLimit) {
    bs_p->element_count = 0;
    bs_p->stack_limit   = stackLimit ? stackLimit : BOUNDED_UNSERIALIZED_STACK_LIMIT_DEFAULT;
    bs_p->head_p        = NULL;
}

/**
 * Push an element onto the stack
 * @param bs_p Pointer to the bounded stack
 * @param element Element to add to the stack
 * @return
 */
int pushOnUnSerializedStack(UnSerializedStack* bs_p, ElementDT * element) {
    int rc = 0;
    if (bs_p->element_count < bs_p->stack_limit) {
        bs_p->element_count++;
        element->element_prev_p = bs_p->head_p;
        bs_p->head_p = element;
    } else {
        rc = 4;
    }
    return rc;
}

/**
 * Pop an Element from the stack
 * @param bs_p Pointer to the bounded stack
 * @return pointer to the returned ElementDT or NULL
 */
ElementDT * popOffUnSerializedStack(UnSerializedStack* bs_p) {
    ElementDT * element = bs_p->head_p;

    if (element != NULL) {
        bs_p->head_p = element->element_prev_p;
        bs_p->element_count--;
        clearPtrs(element);
    }

    return element;
}

/**
 * Pop the entire stack
 *
 * @param bs_p Pointer to the bounded stack
 * @return Pointer to the last element added to the stack or NULL.
 */
ElementDT  * popEntireUnSerializedStack(UnSerializedStack* bs_p) {
    ElementDT * elements = bs_p->head_p;
    bs_p->head_p         = NULL;
    bs_p->element_count  = 0;
    return elements;
}

/**
 * Routine to initialize the specified SerializedStack.
 * Note: elements need to come from a cell pool instead of storage obtain.
 * if not, there is a window where pop_cdsg can reference already released storage.
 *
 * @param serializedStack SerializedStack to initialize.
 * @param limit Maximum number of elements allowed on the stack. Pass 0 for an unlimited number of elements.
 *
 */
void initializeSerializedStack(SerializedStack* serializedStack, int limit) {
    serializedStack->baseStack.stack_header.first = NULL;
    serializedStack->limit = limit;
    serializedStack->counter = 0;
    serializedStack->sequenceNum = 1;
}

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
int pushOnSerializedStack(SerializedStack* serializedStack, ElementDT* newStackElement) {
    SerializedStack oldCompareArea, newCompareArea;
    int cdsgRetCode = 0;
    //oldCompareArea = this;// do a copy
    memcpy(&oldCompareArea, serializedStack, sizeof(SerializedStack));
    do {
        newCompareArea = oldCompareArea;
        newCompareArea.sequenceNum = newCompareArea.sequenceNum + 1;
        // Add the new element to the top of the list
        newStackElement->element_next_p = oldCompareArea.baseStack.stack_header.first;

        newCompareArea.baseStack.stack_header.first = newStackElement;
        newCompareArea.counter = newCompareArea.counter + 1;
        if((oldCompareArea.limit == 0) || (oldCompareArea.counter < oldCompareArea.limit)) {
            cdsgRetCode = __cdsg(&oldCompareArea,              // old
                                 serializedStack,              // current
                                 &newCompareArea);             // new
        } else { // full stack
            return 1;
        }
    } while(cdsgRetCode);
    return 0;
}


/**
 * Routine to pop a ElementDT off the specified SerializedStack.
 *
 * @param serializedStack SerializedStack to pop off of.
 *
 * @return A pointer to a ElementDT.
 *
 */
ElementDT* popOffSerializedStack(SerializedStack* serializedStack) {
    SerializedStack oldCompareArea, newCompareArea;
    int cdsgRetCode = 0;
    ElementDT* target_Element;

    memcpy(&oldCompareArea, serializedStack, sizeof(SerializedStack));
    do {
        if(oldCompareArea.baseStack.stack_header.first != NULL) {
            newCompareArea = oldCompareArea;
            newCompareArea.sequenceNum = newCompareArea.sequenceNum + 1;
            newCompareArea.counter = newCompareArea.counter - 1;

            target_Element = newCompareArea.baseStack.stack_header.first;
            newCompareArea.baseStack.stack_header.first = target_Element->element_next_p; // this puts a requirement that we need to use cell pool instead of storage obtain.. otherwise three is a window where we refer to already release storage
            cdsgRetCode = __cdsg(&oldCompareArea,              // old
                                 serializedStack,              // current
                                 &newCompareArea);             // new

        } else { // empty
            return NULL;
        }

    } while(cdsgRetCode);
    target_Element->element_next_p = NULL;
    target_Element->element_prev_p = NULL;
    return target_Element;
}
