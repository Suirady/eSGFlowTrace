package com.eservglobal.soa;

import oracle.soa.management.facade.ComponentInstance;
import oracle.soa.management.facade.CompositeInstance;
import oracle.soa.management.facade.Locator;
import oracle.soa.management.facade.LocatorFactory;
import oracle.soa.management.util.ComponentInstanceFilter;
import oracle.soa.management.util.CompositeInstanceFilter;

import javax.naming.Context;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class GetComponentsBasedOnCompositeId {
    private static String id = "737372";

    public static void main(String[] args) {
        Locator loc = null;

        List exclusions = new ArrayList();
        try {
            loc = LocatorFactory.createLocator(getConnectionDetails());
            CompositeInstanceFilter compositeInFilter =
                    new CompositeInstanceFilter();
            compositeInFilter.setId(id);
            compositeInFilter.setOrderBy(CompositeInstanceFilter.ORDER_BY_CREATION_DATE_ASC);
            List<CompositeInstance> compositeInstances =
                    loc.getCompositeInstances(compositeInFilter);
            System.out.println(compositeInstances.size());
            Iterator compositeInstancesIterator =
                    compositeInstances.iterator();
            while (compositeInstancesIterator.hasNext()) {
                CompositeInstance compositeInstance =
                        (CompositeInstance) compositeInstancesIterator.next();
                if (!exclusions.contains(compositeInstance.getCompositeDN().getCompositeName())) {
                    System.out.println("Parent composite name :" +
                            compositeInstance.getCompositeDN().getCompositeName());
                    System.out.println("Parent composite instance id :" +
                            compositeInstance.getId());
                    System.out.println("Title :" +
                            compositeInstance.getTitle());
                    System.out.println("Created date :" +
                            compositeInstance.getCreationDate());
                    if (compositeInstance.getState() ==
                            CompositeInstance.STATE_COMPLETED_SUCCESSFULLY) {
                        System.out.println("State : COMPLETED_SUCCESSFULLY");
                    } else if (compositeInstance.getState() ==
                            CompositeInstance.STATE_FAULTED) {
                        System.out.println("State : STATE_FAULTED");
                    } else if (compositeInstance.getState() ==
                            CompositeInstance.STATE_RUNNING) {
                        System.out.println("State : STATE_RUNNING");
                    } else if (compositeInstance.getState() ==
                            CompositeInstance.STATE_STALE) {
                        System.out.println("State : STATE_STALE");
                    } else if (compositeInstance.getState() ==
                            CompositeInstance.STATE_RECOVERY_REQUIRED) {
                        System.out.println("State : STATE_RECOVERY_REQUIRED");
                    } else if (compositeInstance.getState() ==
                            CompositeInstance.STATE_SUSPENDED) {
                        System.out.println("State : STATE_SUSPENDED");
                    } else if (compositeInstance.getState() ==
                            CompositeInstance.STATE_TERMINATED_BY_USER) {
                        System.out.println("State : STATE_SUSPENDED");
                    } else if (compositeInstance.getState() ==
                            CompositeInstance.STATE_UNKNOWN) {
                        System.out.println("State : STATE_UNKNOWN");
                    } else {
                        System.out.println("State : Undefined");
                    }
                    ComponentInstanceFilter filter1 =
                            new ComponentInstanceFilter();
                    filter1.setECID(compositeInstance.getECID());
                    filter1.setOrderBy(ComponentInstanceFilter.ORDER_BY_CREATION_DATE_ASC);
                    List<ComponentInstance> childComponentInstances =
                            compositeInstance.getChildComponentInstances(filter1);
                    Iterator<ComponentInstance> iterator =
                            childComponentInstances.iterator();
                    while (iterator.hasNext()) {
                        ComponentInstance componentInstance = iterator.next();
                        System.out.println("Child Component name :" +
                                componentInstance.getComponentName());
                        System.out.println("Child Component instance id :" +
                                componentInstance.getId());
                        System.out.println("State :" +
                                componentInstance.getNormalizedStateAsString());
                        System.out.println("Payload          :" +
                                componentInstance.getAuditTrail());
                        System.out.println("<-------------------------------------------->");
                    }
                    System.out.println("--------------------------------------------");

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (loc != null) {
                loc.close();
            }
        }

    }


/*    for exception:  weblogic/security/acl/UserInfo
    at weblogic.jndi.WLInitialContextFactory.getInitialContext

run as the weblogic.jar libary no longer contains UserInfo class:
oracle@zoneweblo> java -jar /opt/ORACLE/mw/wlserver/server/lib/wljarbuilder.jar
Creating new jar file: wlfullclient.jar
Integrating jar -->(0)/(0)//opt/ORACLE/mw/wlserver/server/lib/weblogic.jar
.....
    it will generate wlfullclient.jar
    */

    private static Hashtable getConnectionDetails() {
        Hashtable jndiProps = new Hashtable();


/*      Weblogic's implementation of the RMI specification uses a proprietary protocol known as T3.
        You can think of T3 (and secure T3S) as a layer sitting on top of http to expose/allow JNDI calls by clients.
        T3 is the protocol used to transport information between WebLogic servers and other types of Java programs.*/

        //Farm_thunder/thunder/AdminServer/soa-infra
        jndiProps.put(Context.PROVIDER_URL, "t3://10.40.13.238:7001");
        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");

        //put username in hashmap
        jndiProps.put(Context.SECURITY_PRINCIPAL, "weblogic");

        //put password in hashmap
        jndiProps.put(Context.SECURITY_CREDENTIALS, "welcome1");
        jndiProps.put("dedicated.connection", "true");
        return jndiProps;
    }

}