package com.ibm.ws.wsoc;

import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;

import javax.websocket.SendHandler;
import javax.websocket.SendResult;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.wsoc.SendFuture.FUTURE_STATUS;

public class SendHandlerForFuture implements SendHandler {

    private static final TraceComponent tc = Tr.register(SendHandlerForFuture.class);

    SendFuture future = null;

    public void initialize(SendFuture fut) {
        future = fut;
    }

    @Override
    public void onResult(SendResult result) {

        if (future == null) {
            // sanity check, should never get here, if so trace and return
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "returning because we have no futurenot in right state to cancel");
            }
            return;
        }

        if (result.isOK()) {
            future.setStatus(FUTURE_STATUS.DONE);
        } else {
            Throwable t = result.getException();
            boolean changed = false;

            // if this was a timeout exception, and there is a future cancel in progress, then we timed out because of the cancel.
            if (t instanceof SocketTimeoutException) {
                changed = future.changeStatus(FUTURE_STATUS.CANCEL_PENDING, FUTURE_STATUS.CANCELLED);
            }

            // if not a socketTimeoutException, or the cancel state was not pending, then this is an unexpected exception
            if (!changed) {
                ExecutionException ex = new ExecutionException(t);
                future.setStatus(FUTURE_STATUS.ERROR, ex);
            }
        }

        future.notifyAllNow();
    }

}
