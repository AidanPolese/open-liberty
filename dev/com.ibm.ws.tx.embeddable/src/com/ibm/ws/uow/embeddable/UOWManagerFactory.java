/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2004, 2011 */
/* The source code for this program is not published or otherwise divested           */
/* of its trade secrets, irrespective of what has been deposited with the            */
/* U.S. Copyright Office.                                                            */
/*                                                                                   */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                     */
/*                                                                                   */
/*  DESCRIPTION:                                                                     */
/*                                                                                   */
/*  Change History:                                                                  */
/*                                                                                   */
/*  YY-MM-DD  Programmer  Defect  Description                                        */
/*  --------  ----------  ------  -----------                                        */
/*  04-05-12  awilkins    200172  Creation                                           */
/*  06/01/06   johawkes   306998.12      Use TraceComponent.isAnyTracingEnabled()    */
/*  06/02/14  johawkes    347212  New ras & ffdc                                     */
/*  06/02/23  johawkes    349301  Old ras & ffdc                                     */
/*  08/05/22  johawkes    522569  Perf trace                                         */
/*  09/11/09  johawkes    F743-305.1 EJB 3.1                                         */
/* ********************************************************************************* */

package com.ibm.ws.uow.embeddable;

public class UOWManagerFactory {
    private static UOWManager _clientuowManager;

    // uses the initialisation-on-demand holder idiom to provide safe and fast lazy loading
    private static class UOWManagerHolder {
        public static final UOWManager _INSTANCE = new EmbeddableUOWManagerImpl();
    }

    public static UOWManager getUOWManager() {
        return UOWManagerHolder._INSTANCE;
    }

    /**
     * @return
     */
    public static UOWManager getClientUOWManager() {
        if (_clientuowManager == null) {
            _clientuowManager = new ClientUOWManagerImpl();
        }

        return _clientuowManager;
    }

}