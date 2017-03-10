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

#include <stdlib.h>

#include "include/stack_services.h"

#include "include/common_defines.h"

/**
 * @file
 *
 * Methods for managing a serialized stack
 *
 */

/**
 * Routine to check if the specified concurrent_stack is empty
 * @param inputStack_p concurrent_stack to check for empty
 * @return a 1 it the stack empty and 0 if the stack is not empty
 *
 */
int is_stack_empty(concurrent_stack * inputStack_p) {
    return (inputStack_p->concurrent_stack_stack_element_p ? 0 : 1);
} // end is_stack_empty

/**
 * Routine to push a concurrent_stack_element on the specified concurrent_stack.
 *
 * @param inputStack_p    concurrent_stack to push on to.
 * @param inputElement_p  concurrent_stack_element to push onto the stack.
 * @param maxElements     The maximum number of elements allowed on the stack
 *                        after the push in order for the push to be successful.
 *                        This value is only honored if checkMaxElements is set
 *                        to TRUE (1).
 * @param checkMaxElements Set to TRUE (1) if we should perform the maxElements
 *                         check.  If FALSE (0), the push will be unconditional.
 *
 * @return 0 if the push was successful.  If the push is not successful, the
 *         caller is responsible for disposing of the inputElement in some way.
 */
static int push_on_stack_common(concurrent_stack* inputStack_p, concurrent_stack_element* inputElement_p, unsigned short maxElements, unsigned char checkMaxElements) {
    concurrent_stack  oldCDSG_Area, newCDSG_Area;

    int cdsgRetCode = 0, noSpaceLeft = FALSE;
    oldCDSG_Area = *inputStack_p;

    do {
      newCDSG_Area = oldCDSG_Area;
      newCDSG_Area.concurrent_stack_sequence = newCDSG_Area.concurrent_stack_sequence + 1;
      newCDSG_Area.concurrent_stack_cur_element_count = newCDSG_Area.concurrent_stack_cur_element_count + 1;
      inputElement_p->stack_element_next_p = oldCDSG_Area.concurrent_stack_stack_element_p;
      newCDSG_Area.concurrent_stack_stack_element_p = inputElement_p;

      if ((checkMaxElements == FALSE) || ((newCDSG_Area.concurrent_stack_cur_element_count <= maxElements) && (newCDSG_Area.concurrent_stack_cur_element_count != 0))) {
          cdsgRetCode = __cdsg(&oldCDSG_Area,                        // old
                               inputStack_p,                         // current
                               &newCDSG_Area);                       // new
      } else {
          noSpaceLeft = TRUE;
      }
    } while((cdsgRetCode) && (noSpaceLeft == FALSE));
    //---------------------------------------------------------------------
    // Repeat until the compare and double swap is successful
    //---------------------------------------------------------------------

    return noSpaceLeft;
}

/**
 * Routine to push a concurrent_stack_element on the specified concurrent_stack
 * @param inputStack_p    concurrent_stack to push on to
 * @param inputElement_p  stack_element to push onto the stack
 *
 */
void push_on_stack(concurrent_stack * inputStack_p, concurrent_stack_element * inputElement_p) {
    push_on_stack_common(inputStack_p, inputElement_p, 0, FALSE);
} // end push_on_stack

// Push on stack if space available (maxElements).  See doxygen doc in .h part.
int push_on_stack_cond_max_elements(concurrent_stack* inputStack_p, concurrent_stack_element* inputElement_p, unsigned short maxElements) {
    return push_on_stack_common(inputStack_p, inputElement_p, maxElements, TRUE);
}

/**
 * Routine to pop a concurrent_stack_element off the specified concurrent_stack
 * @param inputStack_p concurrent_stack to pop off of
 * @return a pointer to a concurrent_stack_element
 *
 */
concurrent_stack_element * pop_off_stack(concurrent_stack * inputStack_p)
{

  concurrent_stack  oldCDSG_Area, newCDSG_Area;
  int cdsgRetCode;
  concurrent_stack_element * outputElement_p = 0;

  oldCDSG_Area = *inputStack_p;

  do {
    newCDSG_Area = oldCDSG_Area;
    if (newCDSG_Area.concurrent_stack_stack_element_p == 0 ) {
        outputElement_p = 0;
        break;
    }

    newCDSG_Area.concurrent_stack_sequence = newCDSG_Area.concurrent_stack_sequence + 1;
    newCDSG_Area.concurrent_stack_cur_element_count = newCDSG_Area.concurrent_stack_cur_element_count - 1;
    outputElement_p = newCDSG_Area.concurrent_stack_stack_element_p;
    newCDSG_Area.concurrent_stack_stack_element_p = outputElement_p->stack_element_next_p;

    cdsgRetCode = __cdsg(&oldCDSG_Area,                        // old
                         inputStack_p,                         // current
                         &newCDSG_Area);                       // new
  } while(cdsgRetCode);
  //---------------------------------------------------------------------
  // Repeat until the compare and double swap is successful
  //---------------------------------------------------------------------

  return outputElement_p;
} // end pop_off_stack

// Pop multiple elements off the stack.  Doxygen in the .h part.
int pop_batch_off_stack(concurrent_stack* inputStack_p, unsigned short batchSize, concurrent_stack_element** elements) {
    concurrent_stack  oldCDSG_Area, newCDSG_Area;
    int cdsgRetCode = -1;

    oldCDSG_Area = *inputStack_p;

    do {
        // Only allow if there are at least batch size elements on the stack.
        newCDSG_Area = oldCDSG_Area;
        if (newCDSG_Area.concurrent_stack_cur_element_count < batchSize) {
            break;
        }

        newCDSG_Area.concurrent_stack_sequence = newCDSG_Area.concurrent_stack_sequence + 1;
        newCDSG_Area.concurrent_stack_cur_element_count = newCDSG_Area.concurrent_stack_cur_element_count - batchSize;

        // Copy batchSize elements to the supplied storage.  If for some reason
        // the next pointer is null, someone else modified the stack and we
        // need to start over (the CDSG will fail in this case).
        concurrent_stack_element** curElement = elements;
        for (int x = 0; ((x < batchSize) && (newCDSG_Area.concurrent_stack_stack_element_p != NULL)); x++) {
            *curElement = newCDSG_Area.concurrent_stack_stack_element_p;
            curElement = curElement + 1;
            newCDSG_Area.concurrent_stack_stack_element_p = newCDSG_Area.concurrent_stack_stack_element_p->stack_element_next_p;
        }

        cdsgRetCode = __cdsg(&oldCDSG_Area,                        // old
                             inputStack_p,                         // current
                             &newCDSG_Area);                       // new
    } while(cdsgRetCode);
    //---------------------------------------------------------------------
    // Repeat until the compare and double swap is successful
    //---------------------------------------------------------------------

    return cdsgRetCode;
}

/**
 * Routine to get entire chain of concurrent_stack_elements off the specified concurrent_stack
 * @param inputStack_p concurrent_stack to remove the entire list of concurrent_stack_elements
 * @return a pointer to a chain of concurrent_stack_elements
 *
 */
concurrent_stack_element * get_entire_stack(concurrent_stack * inputStack_p) {
    concurrent_stack  oldCDSG_Area, newCDSG_Area;
    int cdsgRetCode;
    concurrent_stack_element * outputElement_p = 0;

    oldCDSG_Area = *inputStack_p;

    do {
      newCDSG_Area = oldCDSG_Area;
      newCDSG_Area.concurrent_stack_sequence = newCDSG_Area.concurrent_stack_sequence + 1;
      newCDSG_Area.concurrent_stack_cur_element_count = 0;
      outputElement_p = oldCDSG_Area.concurrent_stack_stack_element_p;
      if(outputElement_p == 0) {
          break;
      }
      newCDSG_Area.concurrent_stack_stack_element_p = 0;

      cdsgRetCode = __cdsg(&oldCDSG_Area,                        // old
                           inputStack_p,                         // current
                           &newCDSG_Area);                       // new
    } while(cdsgRetCode);
    //---------------------------------------------------------------------
    // Repeat until the compare and double swap is successful
    //---------------------------------------------------------------------

    return outputElement_p;
} // end get_entire_stack

