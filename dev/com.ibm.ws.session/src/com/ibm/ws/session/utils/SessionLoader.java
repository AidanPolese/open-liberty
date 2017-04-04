/*COPYRIGHT_START***********************************************************
 *
 * IBM Confidential OCO Source Material
 * 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2012
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *   IBM DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE, INCLUDING
 *   ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *   PURPOSE. IN NO EVENT SHALL IBM BE LIABLE FOR ANY SPECIAL, INDIRECT OR
 *   CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF
 *   USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
 *   OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE
 *   OR PERFORMANCE OF THIS SOFTWARE.
 *
 *  @(#) 1.5 SERV1/ws/code/session.store/src/com/ibm/ws/session/utils/SessionLoader.java, WAS.session, WASX.SERV1, ff1146.05 3/12/08 09:22:08 [11/21/11 18:33:10]
 *
 * @(#)file   SessionLoader.java
 * @(#)version   1.5
 * @(#)date      3/12/08
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.ws.session.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.AccessController;

import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.serialization.SerializationService;
import com.ibm.ws.util.ThreadContextAccessor;
import com.ibm.wsspi.session.ILoader;

/**
 * This class is an implementation of the ILoader interface and it allows the store to
 * load objects from their serialized forms. This object needs to be primed
 * with the classloader to use before it can do its task.
 * 
 */
public class SessionLoader implements ILoader {

    private static final ThreadContextAccessor threadContextAccessor =
                    AccessController.doPrivileged(ThreadContextAccessor.getPrivilegedAction());

    private static ClassLoader getContextClassLoader() {
        return threadContextAccessor.getContextClassLoaderForUnprivileged(Thread.currentThread());
    }

    private final SerializationService _serializationService;

    private ClassLoader _classLoader;

    public SessionLoader(SerializationService serializationService, ClassLoader classLoader, boolean isApplicationSession) {
        _serializationService = serializationService;
        _classLoader = isApplicationSession ? null : classLoader;
    }

    /**
     * Loads an object using the classloader associated with this loader.
     * <p>
     * 
     * @see com.ibm.wsspi.session.ILoader#loadObject(java.io.InputStream)
     */
    public Object loadObject(InputStream inputStream) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = _classLoader;
        if (classLoader == null) {
            classLoader = getContextClassLoader();
        }

        ObjectInputStream objectInputStream = _serializationService.createObjectInputStream(inputStream, classLoader);
        Object object = null;
        try {
            object = objectInputStream.readObject();
        } catch (Throwable t) {
            FFDCFilter.processException(
                                        t,
                                        "com.ibm.ws.session.SessionLoader.loadObject",
                                        "82",
                                        this);

            if (t instanceof IOException) {
                throw (IOException) t;
            }

            if (t instanceof ClassNotFoundException) {
                throw (ClassNotFoundException) t;
            }
        }

        return object;
    }
}
