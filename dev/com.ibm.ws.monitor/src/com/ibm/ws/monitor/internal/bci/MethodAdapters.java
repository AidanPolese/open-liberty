/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package com.ibm.ws.monitor.internal.bci;

import java.util.Set;

import com.ibm.ws.monitor.internal.ProbeListener;

public enum MethodAdapters {

    ENTRY_PROBE {
        ProbeMethodAdapter create(ProbeMethodAdapter probeMethodAdapter, MethodInfo methodInfo, Set<ProbeListener> interested) {
            return new ProbeAtEntryMethodAdapter(probeMethodAdapter, methodInfo, interested);
        }
    },
    RETURN_PROBE {
        ProbeMethodAdapter create(ProbeMethodAdapter probeMethodAdapter, MethodInfo methodInfo, Set<ProbeListener> interested) {
            return new ProbeAtReturnMethodAdapter(probeMethodAdapter, methodInfo, interested);
        }
    },
    EXCEPTION_EXIT_PROBE {
        ProbeMethodAdapter create(ProbeMethodAdapter probeMethodAdapter, MethodInfo methodInfo, Set<ProbeListener> interested) {
            return new ProbeAtExceptionExitMethodAdapter(probeMethodAdapter, methodInfo, interested);
        }
    },
    CATCH_PROBE {
        ProbeMethodAdapter create(ProbeMethodAdapter probeMethodAdapter, MethodInfo methodInfo, Set<ProbeListener> interested) {
            return new ProbeAtCatchMethodAdapter(probeMethodAdapter, methodInfo, interested);
        }
    },
    THROW_PROBE {
        ProbeMethodAdapter create(ProbeMethodAdapter probeMethodAdapter, MethodInfo methodInfo, Set<ProbeListener> interested) {
            return new ProbeAtThrowMethodAdapter(probeMethodAdapter, methodInfo, interested);
        }
    },
    METHOD_CALL_PROBE {
        ProbeMethodAdapter create(ProbeMethodAdapter probeMethodAdapter, MethodInfo methodInfo, Set<ProbeListener> interested) {
            return new ProbeMethodCallMethodAdapter(probeMethodAdapter, methodInfo, interested);
        }
    },
    FIELD_GET_PROBE {
        ProbeMethodAdapter create(ProbeMethodAdapter probeMethodAdapter, MethodInfo methodInfo, Set<ProbeListener> interested) {
            return new ProbeFieldGetMethodAdapter(probeMethodAdapter, methodInfo, interested);
        }
    },
    FIELD_SET_PROBE {
        ProbeMethodAdapter create(ProbeMethodAdapter probeMethodAdapter, MethodInfo methodInfo, Set<ProbeListener> interested) {
            return new ProbeFieldSetMethodAdapter(probeMethodAdapter, methodInfo, interested);
        }
    };

    abstract ProbeMethodAdapter create(ProbeMethodAdapter probeMethodAdapter, MethodInfo methodInfo, Set<ProbeListener> interested);

    boolean isRequiredForListener(ProbeListener listener) {
        return true;
    };

}