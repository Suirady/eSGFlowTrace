package com.eservglobal.soa;

import oracle.soa.management.facade.Locator;
import oracle.soa.management.facade.LocatorFactory;
import javax.naming.Context;
import java.util.Hashtable;


public class EnableConnection {
    private String address;
    private String username;
    private String password;

    private static Locator loc = null;
    private static final String CONTEXT_FACTORY = "weblogic.jndi.WLInitialContextFactory";

    public EnableConnection(String address, String username, String password) {
        this.address = address;
        this.username = username;
        this.password = password;
    }

    public void connect() throws Exception {
        try {
            // if connection successful save data to singleton to be able to write it later to file
            ComponentData.getInstance().setLoc(LocatorFactory.createLocator(getConnectionDetails()));
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
    private Hashtable getConnectionDetails() throws NullPointerException {
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