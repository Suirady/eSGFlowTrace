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
    private String compositeId;
    private String address;
    private String username;
    private String password;
    private List exclusions;

    private static Locator loc;
    private static final String CONTEXT_FACTORY = "weblogic.jndi.WLInitialContextFactory";

    public GetComponentsBasedOnCompositeId(String address, String username, String password) {
        this.address = address;
        this.username = username;
        this.password = password;
        exclusions = new ArrayList();
    }

    public void connect() throws Exception {
        try {
            loc = LocatorFactory.createLocator(getConnectionDetails());
            CompositeInstanceFilter compositeInFilter = new CompositeInstanceFilter();
            compositeInFilter.setId(compositeId);
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
        } finally {
            closeLoc();
        }
    }

    public static void closeLoc() {
        if (loc != null) {
            loc.close();
            System.out.println("Closed Connection!");
        } else {
            System.out.println("No connection to close");
        }
    }


    @SuppressWarnings("unchecked")
    private Hashtable getConnectionDetails() {
        System.out.println(address + " " + username + " " + password + " " + CONTEXT_FACTORY);
        Hashtable jndiProps = new Hashtable();
        jndiProps.put(Context.PROVIDER_URL, "t3://" + address + ":7001");
        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY);
        //put username in hashmap
        jndiProps.put(Context.SECURITY_PRINCIPAL, username);
        //put password in hashmap
        jndiProps.put(Context.SECURITY_CREDENTIALS, password);
        jndiProps.put("dedicated.connection", "true");
        return jndiProps;
    }
}