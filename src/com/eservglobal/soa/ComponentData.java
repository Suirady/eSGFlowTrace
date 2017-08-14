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
import java.util.*;
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
        exclusions = new ArrayList();
    }

    void setLoc(Locator loc) {
        this.loc = loc;
    }

    public static String getFilename() {
        return filename;
    }

    Locator getLoc() {
        return loc;
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

    public void storeAuditTrail() throws Exception {
        Path path = Paths.get(filename);
        // try-with-resources
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            if (getLoc() != null) {
                CompositeInstanceFilter compositeInFilter = new CompositeInstanceFilter();
                compositeInFilter.setId(instanceID);
                compositeInFilter.setOrderBy(CompositeInstanceFilter.ORDER_BY_CREATION_DATE_ASC);

                compositeInstances = getLoc().getCompositeInstances(compositeInFilter);
                //   bw.write("Number of composite instances is: " + compositeInstances.size());
                bw.write("\t\t\t\tAudit trail summary");
                bw.newLine();
                bw.newLine();
                Iterator compositeInstancesIterator = compositeInstances.iterator();
                if (!compositeInstancesIterator.hasNext()) {
                    throw new NoSuchElementException();
                } else {
                    while (compositeInstancesIterator.hasNext()) {
                        CompositeInstance compositeInstance = (CompositeInstance) compositeInstancesIterator.next();
                        if (!exclusions.contains(compositeInstance.getCompositeDN().getCompositeName())) {
                            bw.write(String.format("%s\n%s\n%s\n%s\n%s\n%s",
                                    "Parent composite instance id:" + compositeInstance.getParentId(),
                                    "Searched composite name: " + compositeInstance.getCompositeDN().getCompositeName(),
                                    "Searched composite instance id:" + compositeInstance.getId(),
                                    "Title :" + compositeInstance.getTitle(),
                                    "Created date: " + compositeInstance.getCreationDate(),
                                    "Domain name:" + compositeInstance.getCompositeDN().getDomainName()));
                            bw.newLine();

                            switch (compositeInstance.getState()) {
                                case CompositeInstance.STATE_COMPLETED_SUCCESSFULLY:
                                    bw.write("State : COMPLETED_SUCCESSFULLY");
                                    bw.newLine();
                                    break;
                                case CompositeInstance.STATE_FAULTED:
                                    bw.write("State : STATE_FAULTED");
                                    bw.newLine();
                                    break;
                                case CompositeInstance.STATE_RUNNING:
                                    bw.write("State : STATE_RUNNING");
                                    bw.newLine();
                                    break;
                                case CompositeInstance.STATE_STALE:
                                    bw.write("State : STATE_STALE");
                                    bw.newLine();
                                    break;
                                case CompositeInstance.STATE_RECOVERY_REQUIRED:
                                    bw.write("State : STATE_RECOVERY_REQUIRED");
                                    bw.newLine();
                                    break;
                                case CompositeInstance.STATE_SUSPENDED:
                                    bw.write("State : STATE_SUSPENDED");
                                    bw.newLine();
                                    break;
                                case CompositeInstance.STATE_TERMINATED_BY_USER:
                                    bw.write("State : TERMINATED_BY_USER");
                                    bw.newLine();
                                    break;
                                case CompositeInstance.STATE_UNKNOWN:
                                    bw.write("State : STATE_UNKNOWN");
                                    bw.newLine();
                                    break;
                                default:
                                    bw.write("State : UNDEFINED");
                                    bw.newLine();
                            }
                            bw.write("##################################################");
                            bw.newLine();
                            ComponentInstanceFilter filter1 = new ComponentInstanceFilter();
                            filter1.setECID(compositeInstance.getECID());
                            filter1.setOrderBy(ComponentInstanceFilter.ORDER_BY_CREATION_DATE_ASC);
                            List<ComponentInstance> childComponentInstances = compositeInstance.getChildComponentInstances(filter1);
                            Iterator<ComponentInstance> iterator = childComponentInstances.iterator();
                            while (iterator.hasNext()) {
                                ComponentInstance componentInstance = iterator.next();

                                bw.write(String.format("%s\n%s\n%s\n%s\n%s",
                                        "Child Component name :" + componentInstance.getComponentName(),
                                        "Child Component instance id :" + componentInstance.getId(),
                                        "State :" + componentInstance.getNormalizedStateAsString(),
                                        "##################################################",
                                        "Payload" + componentInstance.getAuditTrail()));
                                bw.newLine();
                                bw.write("##################################################");
                                bw.newLine();
                            }
                            bw.write("-----------------------------------------------------------");
                        }

                    }
                }

            }
        }
    }

    public void displaySummary() {
        Path path = Paths.get(filename);
        try (Scanner scan = new Scanner(Files.newBufferedReader(path))) {
            scan.useDelimiter("#");
            CenterPanelController.areaTextP.setText(scan.next());
            CenterPanelController.saveFileBtnP.setVisible(true);
            scan.close();
        } catch (NoSuchElementException ex) {
            CenterPanelController.areaTextP.clear();
            CenterPanelController.areaTextP.setText("Composite instance not found!");
            CenterPanelController.saveFileBtnP.setVisible(false);
        } catch (IOException e) {
            Logger.getLogger(ComponentData.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void deleteFile() {
        if (getLoc() != null) {
            Path path = Paths.get(filename);
            try {
                Files.delete(path);
            } catch (IOException e) {
                Logger.getLogger(ComponentData.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
}