package com.eservglobal.soa;

import oracle.soa.management.facade.Fault;
import oracle.soa.management.facade.Locator;
import oracle.soa.management.facade.LocatorFactory;
import oracle.soa.management.util.FaultFilter;

import javax.naming.Context;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class GetFaultsForCompositeId {
    public GetFaultsForCompositeId() {
        super();
    }

    public static void main(String[] args) {
        String compositeId = "1440095";
        Locator locator = null;
        try {
            locator = LocatorFactory.createLocator(getConnectionForLocator());
            System.out.println(locator);
            FaultFilter faultFilter = new FaultFilter();
            faultFilter.setCompositeInstanceId(compositeId);

            System.out.println("---------------------------------------------------------------");
            faultFilter.setOrderBy(FaultFilter.ORDER_BY_CREATION_DATE_DESC);
            //  faultFilter.setECID("d96112c160649698:231b4e4c:14b863930d4:-8000-00000000001b14a5");
            //faultFilter.setRecoverable(true);
            faultFilter.setPageSize(10);
            List<Fault> list = locator.getFaults(faultFilter);
            Iterator<Fault> faults = list.iterator();
            while (faults.hasNext()) {
                Fault fault = faults.next();
                System.out.println(fault.getCompositeInstanceId());
                System.out.println(fault.getCreationDate());
                System.out.println(fault.getComponentName());
                System.out.println(fault.getMessage());
                System.out.println("---------------------------------------------------------------");
            }
            System.out.println("---------------------------------------------------------------");
        } catch (Exception e) {
            System.out.println("Unknown Exception ");
            e.printStackTrace();
        } finally {
            try {
                if (locator != null) {
                    locator.close();
                    System.out.println("Locator closed successfully");
                }
            } catch (Exception e) {
                System.out.println("Exception while closing Locator handle");
                e.printStackTrace();
            }
        }

    }

    @SuppressWarnings("unchecked")
    private static Hashtable getConnectionForLocator() {
        Hashtable jndiProps = new Hashtable();
        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
        jndiProps.put(Context.PROVIDER_URL, "t3://xyz.com:12354/soa-infra");
        // username
        jndiProps.put(Context.SECURITY_PRINCIPAL, "xxx");
        // credentials
        jndiProps.put(Context.SECURITY_CREDENTIALS, "yyyy");
        jndiProps.put("dedicated.connection", "true");
        return jndiProps;
    }
}