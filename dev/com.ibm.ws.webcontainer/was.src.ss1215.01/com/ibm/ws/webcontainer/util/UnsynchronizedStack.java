// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//    LIDB4408-1      02/22/06      todkap              LIDB4408-1 web container changes to limit pooling
//    LIDB3518-1.1    06-23-07      mmolden             ARD
//

package com.ibm.ws.webcontainer.util;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class UnsynchronizedStack <E> extends LinkedList<E>  {
    private static final long serialVersionUID = 3257562923390809657L;
    public UnsynchronizedStack() {
        super();
    }
    public E peek(){
        try{
            return this.getLast();
        }catch (NoSuchElementException e){
            return null;
        }
    }
    public E pop(){
        try{
            return this.removeLast();
        }catch (NoSuchElementException e){
            return null;
        }
    }
    
    public void push(E obj){
        this.add(obj);
    }
}
