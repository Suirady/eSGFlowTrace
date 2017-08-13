package com.eservglobal.soa;

import com.eservglobal.mvc.CenterPanelController;
import oracle.soa.management.facade.ComponentInstance;
import oracle.soa.management.facade.CompositeInstance;
import oracle.soa.management.facade.Locator;
import oracle.soa.management.util.ComponentInstanceFilter;
import oracle.soa.management.util.CompositeInstanceFilter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton class to store data in a file
 * Normally the persistence is done via database or xml files etc. but for our application
 * we will read and write data to a file
 */

public class ComponentData {

    private static final String filename = "AuditTrail.txt";
    private static ComponentData componentData;

    private String instanceID;
    private Locator loc;
    private List<CompositeInstance> compositeInstances;
    private List exclusions;

    private ComponentData() {
    }

    public void setLoc(Locator loc) {
        this.loc = loc;
    }

    public static String getFilename() {
        return filename;
    }

    public Locator getLoc() {
        return loc;
    }

    public String getInstanceID() {
        return instanceID;
    }

    public void setInstanceID(String instanceID) {
        this.instanceID = instanceID;
    }

    /*    this app was not designed for multithreaded environment
    so a simple singleton implementation was chosen instead of class holder(private inner class)*/

    public static ComponentData getInstance() {
        if (componentData == null) {
            componentData = new ComponentData();
        }
        return componentData;
    }

    public void storeItems() throws Exception {
        Path path = Paths.get(filename);
        // try-with-resources
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            if (getLoc() != null) {
                CompositeInstanceFilter compositeInFilter = new CompositeInstanceFilter();
                compositeInFilter.setId(instanceID);
                compositeInFilter.setOrderBy(CompositeInstanceFilter.ORDER_BY_CREATION_DATE_ASC);

                compositeInstances = getLoc().getCompositeInstances(compositeInFilter);
                bw.write("Number of composite instances is: " + compositeInstances.size());
                bw.newLine();
                Iterator compositeInstancesIterator = compositeInstances.iterator();
                while (compositeInstancesIterator.hasNext()) {
                    CompositeInstance compositeInstance = (CompositeInstance) compositeInstancesIterator.next();
                    if (!exclusions.contains(compositeInstance.getCompositeDN().getCompositeName())) {
                        bw.write(String.format("%s\n%s\n%s\n%s\n%s",
                                "Parent composite name: " + compositeInstance.getCompositeDN().getCompositeName(),
                                "Parent composite instance id:" + compositeInstance.getId(),
                                "Parent composite instance id:" + compositeInstance.getId(),
                                "Title :" + compositeInstance.getTitle(),
                                "Created date: " + compositeInstance.getCreationDate()));
                        bw.newLine();

                        switch (compositeInstance.getState()) {
                            case CompositeInstance.STATE_COMPLETED_SUCCESSFULLY:
                                bw.write("STATE : COMPLETED_SUCCESSFULLY");
                                bw.newLine();
                                break;
                            case CompositeInstance.STATE_FAULTED:
                                bw.write("STATE : STATE_FAULTED");
                                bw.newLine();
                                break;
                            case CompositeInstance.STATE_RUNNING:
                                bw.write("STATE : STATE_RUNNING");
                                bw.newLine();
                                break;
                            case CompositeInstance.STATE_STALE:
                                bw.write("STATE : STATE_STALE");
                                bw.newLine();
                                break;
                            case CompositeInstance.STATE_RECOVERY_REQUIRED:
                                bw.write("STATE : STATE_RECOVERY_REQUIRED");
                                bw.newLine();
                                break;
                            case CompositeInstance.STATE_SUSPENDED:
                                bw.write("STATE : STATE_SUSPENDED");
                                bw.newLine();
                                break;
                            case CompositeInstance.STATE_TERMINATED_BY_USER:
                                bw.write("STATE : TERMINATED_BY_USER");
                                bw.newLine();
                                break;
                            case CompositeInstance.STATE_UNKNOWN:
                                bw.write("STATE : STATE_UNKNOWN");
                                bw.newLine();
                                break;
                            default:
                                bw.write("STATE : UNDEFINED");
                                bw.newLine();
                        }

                        ComponentInstanceFilter filter1 = new ComponentInstanceFilter();
                        filter1.setECID(compositeInstance.getECID());
                        filter1.setOrderBy(ComponentInstanceFilter.ORDER_BY_CREATION_DATE_ASC);
                        List<ComponentInstance> childComponentInstances = compositeInstance.getChildComponentInstances(filter1);
                        Iterator<ComponentInstance> iterator = childComponentInstances.iterator();
                        while (iterator.hasNext()) {
                            ComponentInstance componentInstance = iterator.next();

                            bw.write(String.format("%s\n%s\n%s\n%s",
                                    "Child Component name :" + componentInstance.getComponentName(),
                                    "Child Component instance id :" + componentInstance.getId(),
                                    "STATE :" + componentInstance.getNormalizedStateAsString(),
                                    "##################################################"));
                            bw.newLine();
                            bw.write("PAYLOAD" + componentInstance.getAuditTrail());
                            bw.write("##################################################");
                            bw.newLine();
                        }
                        bw.write("-----------------------------------------------------------");
                    }

                }
            }
        }
    }

    public void deleteFile() {
        if (getLoc() != null) {
            Path path = Paths.get(filename);
            try {
                Files.delete(path);
            } catch (IOException e) {
                Logger.getLogger(CenterPanelController.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
}

